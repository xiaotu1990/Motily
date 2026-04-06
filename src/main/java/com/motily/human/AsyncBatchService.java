package com.motily.human;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.UserTransaction;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@ApplicationScoped
public class AsyncBatchService {

    @Inject
    PopulationInitializer populationInitializer;

    @Inject
    HumanService humanService;

    @Inject
    UserTransaction utx;

    @PersistenceContext
    EntityManager em;

    private final ExecutorService executorService = Executors.newFixedThreadPool(5);
    private final Map<String, BatchTask> taskMap = new ConcurrentHashMap<>();

    public String startBatchCreate(int count) {
        String taskId = "batch-" + System.currentTimeMillis();
        BatchTask task = new BatchTask(taskId, count);
        taskMap.put(taskId, task);

        executorService.submit(() -> {
            processBatchTask(task);
        });

        return taskId;
    }

    public void processBatchTask(BatchTask task) {
        System.out.println("开始执行批量任务: " + task.taskId + ", 数量: " + task.count);
        try {
            task.status = BatchTask.Status.RUNNING;
            task.startTime = LocalDateTime.now();
            System.out.println("任务状态更新为 RUNNING: " + task.taskId);

            int currentYear = java.time.LocalDate.now().getYear();
            java.util.Random rng = new java.util.Random();
            int chunkSize = 5000;
            int totalCreated = 0;

            // 手动开始事务
            utx.begin();
            System.out.println("事务开始: " + task.taskId);

            for (int i = 0; i < task.count; i += chunkSize) {
                int batchSize = Math.min(chunkSize, task.count - i);
                System.out.println("处理批次: " + (i/chunkSize + 1) + ", 大小: " + batchSize);
                for (int j = 0; j < batchSize; j++) {
                    Human human = populationInitializer.generateSingleHuman(currentYear, rng);
                    em.persist(human);
                    totalCreated++;
                    if (j % 1000 == 0) {
                        System.out.println("已创建: " + totalCreated + " 人");
                    }
                }
                em.flush();
                em.clear();
                System.out.println("批次完成，已持久化: " + totalCreated + " 人");
            }

            // 提交事务
            utx.commit();
            System.out.println("事务提交成功: " + task.taskId);

            task.totalCreated = totalCreated;
            task.totalPopulation = humanService.countHumans();
            task.status = BatchTask.Status.COMPLETED;
            task.endTime = LocalDateTime.now();
            System.out.println("任务完成: " + task.taskId + ", 创建: " + totalCreated + " 人, 总人口: " + task.totalPopulation);
        } catch (Exception e) {
            // 回滚事务
            try {
                if (utx != null) {
                    utx.rollback();
                    System.out.println("事务回滚: " + task.taskId);
                }
            } catch (Exception rollbackEx) {
                rollbackEx.printStackTrace();
            }
            task.status = BatchTask.Status.FAILED;
            task.error = e.getMessage();
            System.err.println("任务失败: " + task.taskId);
            e.printStackTrace();
        }
    }

    public BatchTask getTaskStatus(String taskId) {
        return taskMap.get(taskId);
    }

    public static class BatchTask {
        public enum Status {
            PENDING, RUNNING, COMPLETED, FAILED
        }

        public String taskId;
        public int count;
        public Status status = Status.PENDING;
        public int totalCreated = 0;
        public long totalPopulation = 0;
        public String error = null;
        public LocalDateTime startTime = null;
        public LocalDateTime endTime = null;

        public BatchTask(String taskId, int count) {
            this.taskId = taskId;
            this.count = count;
        }

        public Map<String, Object> toMap() {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("taskId", taskId);
            map.put("count", count);
            map.put("status", status.toString());
            map.put("totalCreated", totalCreated);
            map.put("totalPopulation", totalPopulation);
            map.put("error", error);
            map.put("startTime", startTime);
            map.put("endTime", endTime);
            return map;
        }
    }
}
