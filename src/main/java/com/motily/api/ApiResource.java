package com.motily.api;

import com.motily.human.Human;
import com.motily.human.HumanService;
import com.motily.human.PopulationInitializer;
import com.motily.human.AsyncBatchService;
import com.motily.human.HumanExperience;
import com.motily.human.HumanMemory;
import com.motily.human.MemoryService;
import com.motily.cache.StatsCacheService;
import com.motily.society.Marriage;
import com.motily.society.SocialEvent;
import com.motily.society.SocialIndicator;
import com.motily.society.SocietyService;
import com.motily.timeline.Timeline;
import com.motily.timeline.TimelineService;
import com.motily.dna.DnaService;
import com.motily.dna.DnaStructure;
import com.motily.dna.DnaEncoderDecoder;
import com.motily.region.Region;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Random;
import com.motily.api.EventDTO;

@Path("/api")
@ApplicationScoped
public class ApiResource {
    @Inject
    HumanService humanService;

    @Inject
    StatsCacheService statsCacheService;

    @Inject
    AsyncBatchService asyncBatchService;

    @Inject
    PopulationInitializer populationInitializer;
    
    @Inject
    TimelineService timelineService;
    
    @Inject
    SocietyService societyService;

    @Inject
    com.motily.region.RegionService regionService;
    
    @Inject
    MemoryService memoryService;
    
    // 数字人管理接口
    @POST
    @Path("/human/generate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response generateHuman(HumanGenerateRequest request) {
        int count = request.count > 0 ? request.count : 10;
        String genderRatio = request.genderRatio != null ? request.genderRatio : "balanced";
        double wealthMin = request.wealthMin > 0 ? request.wealthMin : 1000;
        double wealthMax = request.wealthMax > 0 ? request.wealthMax : 100000;
        int birthYear = request.birthYear > 0 ? request.birthYear : 2000;
        
        for (int i = 0; i < count; i++) {
            // 根据性别比例生成性别
            int gender = generateGender(genderRatio, i);
            
            // 生成中文姓名（结合性别）
            String name = generateChineseName(gender);
            
            // 生成随机财富
            double wealth = wealthMin + Math.random() * (wealthMax - wealthMin);
            
            humanService.createHuman(name, gender, birthYear, wealth, null, null);
        }
        return Response.ok().entity("{\"code\": 200, \"data\": {\"generatedCount\": " + count + "}}")
                .build();
    }
    
    // 百家姓（包括复姓）
    private static final String[] SURNAMES = {
        "王", "李", "张", "刘", "陈", "杨", "黄", "赵", "吴", "周",
        "徐", "孙", "马", "朱", "胡", "郭", "何", "高", "林", "罗",
        "郑", "梁", "谢", "宋", "唐", "许", "韩", "冯", "邓", "曹",
        "彭", "曾", "肖", "田", "董", "袁", "潘", "于", "蒋", "蔡",
        "余", "杜", "叶", "程", "苏", "魏", "吕", "丁", "任", "沈"
    };
    
    // 复姓
    private static final String[] COMPOUND_SURNAMES = {
        "欧阳", "上官", "司马", "诸葛", "司徒", "司空", "公孙", "东方", "皇甫", "尉迟"
    };
    
    // 男性名字常用字（单字和双字）
    private static final String[] MALE_NAME_SINGLE = {
        "伟", "强", "军", "勇", "杰", "涛", "磊", "超", "明", "华",
        "洋", "健", "辉", "刚", "峰", "宇", "博", "浩", "轩", "辰"
    };
    
    private static final String[] MALE_NAME_DOUBLE = {
        "明轩", "子豪", "浩然", "雨泽", "宇轩", "俊驰", "文博", "天佑", "子骞", "昊然",
        "致远", "俊楠", "鸿涛", "伟祺", "荣轩", "越泽", "浩宇", "瑾瑜", "皓轩", "擎宇"
    };
    
    // 女性名字常用字（单字和双字）
    private static final String[] FEMALE_NAME_SINGLE = {
        "芳", "娜", "婷", "静", "丽", "艳", "敏", "霞", "燕", "玲",
        "娟", "莉", "雪", "梅", "兰", "菊", "萍", "瑶", "玉", "蓉"
    };
    
    private static final String[] FEMALE_NAME_DOUBLE = {
        "雨桐", "梦琪", "忆柳", "之桃", "慕青", "问兰", "尔岚", "元香", "初夏", "沛菡",
        "傲珊", "曼文", "乐菱", "痴珊", "恨玉", "惜文", "香寒", "新柔", "语蓉", "海安"
    };
    
    // 生成中文姓名
    private String generateChineseName(int gender) {
        // 10%的概率使用复姓
        String surname;
        if (Math.random() < 0.1) {
            surname = COMPOUND_SURNAMES[(int) (Math.random() * COMPOUND_SURNAMES.length)];
        } else {
            surname = SURNAMES[(int) (Math.random() * SURNAMES.length)];
        }
        
        // 70%的概率使用双字名，30%的概率使用单字名
        String name;
        if (gender == 1) { // 男性
            if (Math.random() < 0.7) {
                name = MALE_NAME_DOUBLE[(int) (Math.random() * MALE_NAME_DOUBLE.length)];
            } else {
                name = MALE_NAME_SINGLE[(int) (Math.random() * MALE_NAME_SINGLE.length)];
            }
        } else { // 女性
            if (Math.random() < 0.7) {
                name = FEMALE_NAME_DOUBLE[(int) (Math.random() * FEMALE_NAME_DOUBLE.length)];
            } else {
                name = FEMALE_NAME_SINGLE[(int) (Math.random() * FEMALE_NAME_SINGLE.length)];
            }
        }
        
        return surname + name;
    }
    
    // 根据性别比例生成性别
    private int generateGender(String genderRatio, int index) {
        if ("moreMale".equals(genderRatio)) {
            return Math.random() > 0.3 ? 1 : 0; // 70% 男性
        } else if ("moreFemale".equals(genderRatio)) {
            return Math.random() > 0.3 ? 0 : 1; // 70% 女性
        } else {
            return index % 2; // 平衡
        }
    }
    
    // 数字人生成请求类
    public static class HumanGenerateRequest {
        public int count;
        public String genderRatio;
        public double wealthMin;
        public double wealthMax;
        public int birthYear;
    }
    
    @GET
    @Path("/human/list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listHumans(@QueryParam("page") int page, @QueryParam("size") int size) {
        List<Human> humans = humanService.listHumans(page, size);
        long total = humanService.countHumans();
        
        java.util.Map<String, Object> responseData = new java.util.HashMap<>();
        responseData.put("list", humans);
        responseData.put("total", total);
        
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("code", 200);
        response.put("data", responseData);
        
        return Response.ok(response).build();
    }
    
    @GET
    @Path("/human/detail")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHumanDetail(@QueryParam("id") Long id) {
        Human human = humanService.getHumanById(id);
        
        if (human == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"code\": 404, \"message\": \"Human not found\"}")
                    .build();
        }
        
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("code", 200);
        response.put("data", human);
        
        return Response.ok(response).build();
    }
    
    @PUT
    @Path("/human/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateHuman(Human human) {
        humanService.updateHuman(human);
        return Response.ok().entity("{\"code\": 200, \"data\": {\"success\": true}}")
                .build();
    }
    
    // 获取数字人经历
    @GET
    @Path("/human/{id}/experiences")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHumanExperiences(@PathParam("id") Long id) {
        Human human = Human.findById(id);
        if (human == null) {
            return Response.ok().entity("{\"code\": 404, \"data\": {\"message\": \"数字人不存在\"}}")
                    .build();
        }
        List<HumanExperience> experiences = HumanExperience.find("human.id = ?1 ORDER BY eventYear", id).list();
        return Response.ok().entity("{\"code\": 200, \"data\": " + experiences + "}")
                .build();
    }
    
    // 获取数字人记忆
    @GET
    @Path("/human/{id}/memories")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHumanMemories(@PathParam("id") Long id) {
        Human human = Human.findById(id);
        if (human == null) {
            return Response.ok().entity("{\"code\": 404, \"data\": {\"message\": \"数字人不存在\"}}")
                    .build();
        }
        List<HumanMemory> memories = HumanMemory.find("human.id = ?1 ORDER BY lastAccessedAt DESC", id).list();
        return Response.ok().entity("{\"code\": 200, \"data\": " + memories + "}")
                .build();
    }
    
    // 获取数字人生活轨迹
    @GET
    @Path("/human/{id}/life-path")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHumanLifePath(@PathParam("id") Long id) {
        Human human = Human.findById(id);
        if (human == null) {
            return Response.ok().entity("{\"code\": 404, \"data\": {\"message\": \"数字人不存在\"}}")
                    .build();
        }
        List<HumanExperience> experiences = HumanExperience.find("human.id = ?1 ORDER BY eventYear", id).list();
        List<HumanMemory> recentMemories = HumanMemory.find("human.id = ?1 ORDER BY lastAccessedAt DESC", id).page(io.quarkus.panache.common.Page.of(0, 10)).list();
        return Response.ok().entity("{\"code\": 200, \"data\": {\"human\": " + human + ", \"experiences\": " + experiences + ", \"recentMemories\": " + recentMemories + "}}")
                .build();
    }
    
    // 社会模拟接口
    @POST
    @Path("/simulation/start")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response startSimulation(SimulationRequest request) {
        int years = request.years > 0 ? request.years : 10;
        String theme = request.theme != null ? request.theme : "normal";
        Timeline timeline = timelineService.createTimeline(2000);
        timelineService.runSimulation(timeline, years, theme);
        return Response.ok().entity("{\"code\": 200, \"data\": {\"simulationId\": " + timeline.id + "}}")
                .build();
    }
    
    // 获取当前模拟时间
    @GET
    @Path("/simulation/time")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurrentSimulationTime() {
        Timeline timeline = Timeline.find("status = 1 ORDER BY id DESC").firstResult();
        if (timeline == null) {
            return Response.ok().entity("{\"code\": 200, \"data\": {\"year\": 2024, \"week\": 1, \"simulationId\": null}}")
                    .build();
        }
        return Response.ok().entity("{\"code\": 200, \"data\": {\"year\": " + timeline.currentYear + ", \"week\": " + timeline.currentWeek + ", \"simulationId\": " + timeline.id + "}}")
                .build();
    }
    
    // 单步推进模拟时间
    @POST
    @Path("/simulation/step")
    @Produces(MediaType.APPLICATION_JSON)
    public Response stepSimulation() {
        try {
            Timeline timeline = Timeline.find("status = 1 ORDER BY id DESC").firstResult();
            if (timeline == null) {
                return Response.ok().entity("{\"code\": 400, \"data\": {\"message\": \"没有活跃的模拟\"}}")
                        .build();
            }
            
            // 保存当前时间，用于在事务失败时返回
            int currentYear = timeline.currentYear;
            int currentWeek = timeline.currentWeek;
            
            // 尝试推进时间
            timelineService.stepForward(timeline);
            
            // 重新获取timeline以获取更新后的数据
            Timeline updatedTimeline = Timeline.find("status = 1 ORDER BY id DESC").firstResult();
            if (updatedTimeline != null) {
                return Response.ok().entity("{\"code\": 200, \"data\": {\"year\": " + updatedTimeline.currentYear + ", \"week\": " + updatedTimeline.currentWeek + "}}")
                        .build();
            } else {
                return Response.ok().entity("{\"code\": 200, \"data\": {\"year\": " + currentYear + ", \"week\": " + currentWeek + "}}")
                        .build();
            }
        } catch (Exception e) {
            // 捕获异常，返回当前时间
            Timeline timeline = Timeline.find("status = 1 ORDER BY id DESC").firstResult();
            if (timeline != null) {
                return Response.ok().entity("{\"code\": 200, \"data\": {\"year\": " + timeline.currentYear + ", \"week\": " + timeline.currentWeek + "}}")
                        .build();
            } else {
                return Response.ok().entity("{\"code\": 400, \"data\": {\"message\": \"没有活跃的模拟\"}}")
                        .build();
            }
        }
    }
    
    @GET
    @Path("/simulation/events")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSimulationEvents(@QueryParam("simulationId") Long simulationId) {
        List<SocialEvent> events = societyService.listSocialEventsByTimeline(simulationId);
        
        java.util.List<EventDTO> eventDTOs = new java.util.ArrayList<>();
        for (SocialEvent event : events) {
            eventDTOs.add(new EventDTO(event));
        }
        
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("code", 200);
        response.put("data", eventDTOs);
        
        return Response.ok(response).build();
    }
    
    // 模拟请求类
    public static class SimulationRequest {
        public int years;
        public String theme;
    }
    
    @POST
    @Path("/simulation/pause")
    @Produces(MediaType.APPLICATION_JSON)
    public Response pauseSimulation(@QueryParam("simulationId") Long simulationId) {
        Timeline timeline = timelineService.getTimelineById(simulationId);
        timelineService.pauseTimeline(timeline);
        return Response.ok().entity("{\"code\": 200, \"data\": {\"success\": true}}")
                .build();
    }
    
    @POST
    @Path("/simulation/resume")
    @Produces(MediaType.APPLICATION_JSON)
    public Response resumeSimulation(@QueryParam("simulationId") Long simulationId) {
        Timeline timeline = timelineService.getTimelineById(simulationId);
        timelineService.resumeTimeline(timeline);
        return Response.ok().entity("{\"code\": 200, \"data\": {\"success\": true}}")
                .build();
    }
    
    @POST
    @Path("/simulation/stop")
    @Produces(MediaType.APPLICATION_JSON)
    public Response stopSimulation(@QueryParam("simulationId") Long simulationId) {
        Timeline timeline = timelineService.getTimelineById(simulationId);
        timelineService.stopTimeline(timeline);
        return Response.ok().entity("{\"code\": 200, \"data\": {\"success\": true}}")
                .build();
    }
    
    @GET
    @Path("/simulation/status")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSimulationStatus(@QueryParam("simulationId") Long simulationId) {
        Timeline timeline = timelineService.getTimelineById(simulationId);
        return Response.ok().entity("{\"code\": 200, \"data\": {\"status\": " + timeline.status + ", \"currentYear\": " + timeline.currentYear + "}}")
                .build();
    }
    
    // 社会指标接口
    @GET
    @Path("/indicator/year")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getIndicatorByYear(@QueryParam("year") int year) {
        SocialIndicator indicator = societyService.getSocialIndicatorByYear(year);
        
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("code", 200);
        response.put("data", indicator);
        
        return Response.ok(response).build();
    }
    
    @GET
    @Path("/indicator/trend")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getIndicatorTrend(@QueryParam("startYear") int startYear, @QueryParam("endYear") int endYear) {
        List<SocialIndicator> indicators = societyService.listSocialIndicators(startYear, endYear);
        
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("code", 200);
        response.put("data", indicators);
        
        return Response.ok(response).build();
    }
    
    // 人口和财富统计接口
    @GET
    @Path("/human/stats")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHumanStats() {
        // 尝试从缓存获取
        java.util.Map<String, Object> cachedData = statsCacheService.get(StatsCacheService.KEY_HUMAN_STATS);
        if (cachedData != null) {
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("code", 200);
            response.put("data", cachedData);
            return Response.ok(response).build();
        }

        // 缓存未命中，计算并缓存
        long totalPopulation = Human.count("deathYear is null");
        Number totalWealthResult = Human.getEntityManager().createQuery("SELECT SUM(h.wealth) FROM Human h WHERE h.deathYear IS NULL", Number.class).getSingleResult();
        double totalWealth = totalWealthResult != null ? totalWealthResult.doubleValue() : 0.0;

        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("totalPopulation", totalPopulation);
        data.put("totalWealth", totalWealth);

        // 缓存结果
        statsCacheService.put(StatsCacheService.KEY_HUMAN_STATS, data);

        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("code", 200);
        response.put("data", data);

        return Response.ok(response).build();
    }
    
    // 人类分布统计接口 - 阶层分布
    @GET
    @Path("/human/distribution/social-class")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSocialClassDistribution() {
        // 尝试从缓存获取
        java.util.Map<String, Object> cachedData = statsCacheService.get(StatsCacheService.KEY_SOCIAL_CLASS_DISTRIBUTION);
        if (cachedData != null) {
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("code", 200);
            response.put("data", cachedData);
            return Response.ok(response).build();
        }

        // 缓存未命中，计算并缓存
        List<Human> humans = Human.find("deathYear is null").list();

        java.util.Map<String, Integer> classCount = new java.util.HashMap<>();
        classCount.put("底层", 0);
        classCount.put("中层", 0);
        classCount.put("上层", 0);

        for (Human human : humans) {
            String className;
            switch (human.socialClass) {
                case 1: className = "底层"; break;
                case 2: className = "中层"; break;
                case 3: className = "上层"; break;
                default: className = "其他";
            }
            classCount.put(className, classCount.get(className) + 1);
        }

        long total = humans.size();
        java.util.List<java.util.Map<String, Object>> distribution = new java.util.ArrayList<>();

        for (java.util.Map.Entry<String, Integer> entry : classCount.entrySet()) {
            java.util.Map<String, Object> item = new java.util.HashMap<>();
            item.put("category", entry.getKey());
            item.put("value", entry.getValue());
            item.put("percentage", total > 0 ? Math.round((entry.getValue() * 100.0 / total) * 10.0) / 10.0 : 0.0);
            distribution.add(item);
        }

        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("distribution", distribution);
        data.put("total", total);

        // 缓存结果
        statsCacheService.put(StatsCacheService.KEY_SOCIAL_CLASS_DISTRIBUTION, data);

        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("code", 200);
        response.put("data", data);

        return Response.ok(response).build();
    }
    
    // 人类分布统计接口 - 职业分布
    @GET
    @Path("/human/distribution/occupation")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOccupationDistribution() {
        List<Human> humans = Human.find("deathYear is null").list();

        java.util.Map<String, Integer> occupationCount = new java.util.HashMap<>();

        for (Human human : humans) {
            String occupation = human.occupation != null && !human.occupation.isEmpty() ? human.occupation : "无业";
            occupationCount.put(occupation, occupationCount.getOrDefault(occupation, 0) + 1);
        }

        long total = humans.size();
        java.util.List<java.util.Map<String, Object>> distribution = new java.util.ArrayList<>();

        for (java.util.Map.Entry<String, Integer> entry : occupationCount.entrySet()) {
            java.util.Map<String, Object> item = new java.util.HashMap<>();
            item.put("category", entry.getKey());
            item.put("value", entry.getValue());
            item.put("percentage", total > 0 ? Math.round((entry.getValue() * 100.0 / total) * 10.0) / 10.0 : 0.0);
            distribution.add(item);
        }

        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("distribution", distribution);
        data.put("total", total);

        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("code", 200);
        response.put("data", data);

        return Response.ok(response).build();
    }
    
    // 人类分布统计接口 - 财富分布
    @GET
    @Path("/human/distribution/wealth")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getWealthDistribution() {
        List<Human> humans = Human.find("deathYear is null").list();

        java.util.Map<String, java.util.Map<String, Object>> wealthRanges = new java.util.LinkedHashMap<>();
        wealthRanges.put("低收入 (0-1 万)", new java.util.HashMap<String, Object>() {{ put("count", 0); put("total", 0.0); }});
        wealthRanges.put("中低收入 (1 万 -5 万)", new java.util.HashMap<String, Object>() {{ put("count", 0); put("total", 0.0); }});
        wealthRanges.put("中等收入 (5 万 -20 万)", new java.util.HashMap<String, Object>() {{ put("count", 0); put("total", 0.0); }});
        wealthRanges.put("中高收入 (20 万 -50 万)", new java.util.HashMap<String, Object>() {{ put("count", 0); put("total", 0.0); }});
        wealthRanges.put("高收入 (50 万+)", new java.util.HashMap<String, Object>() {{ put("count", 0); put("total", 0.0); }});

        for (Human human : humans) {
            double wealth = human.wealth;
            String range;
            if (wealth < 10000) {
                range = "低收入 (0-1 万)";
            } else if (wealth < 50000) {
                range = "中低收入 (1 万 -5 万)";
            } else if (wealth < 200000) {
                range = "中等收入 (5 万 -20 万)";
            } else if (wealth < 500000) {
                range = "中高收入 (20 万 -50 万)";
            } else {
                range = "高收入 (50 万+)";
            }

            java.util.Map<String, Object> rangeData = wealthRanges.get(range);
            rangeData.put("count", (Integer) rangeData.get("count") + 1);
            rangeData.put("total", (Double) rangeData.get("total") + wealth);
        }

        long total = humans.size();
        java.util.List<java.util.Map<String, Object>> distribution = new java.util.ArrayList<>();

        for (java.util.Map.Entry<String, java.util.Map<String, Object>> entry : wealthRanges.entrySet()) {
            java.util.Map<String, Object> rangeData = entry.getValue();
            java.util.Map<String, Object> item = new java.util.HashMap<>();
            item.put("category", entry.getKey());
            item.put("value", rangeData.get("count"));
            item.put("totalWealth", rangeData.get("total"));
            int count = (Integer) rangeData.get("count");
            item.put("percentage", total > 0 ? Math.round((count * 100.0 / total) * 10.0) / 10.0 : 0.0);
            distribution.add(item);
        }

        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("distribution", distribution);
        data.put("total", total);

        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("code", 200);
        response.put("data", data);

        return Response.ok(response).build();
    }
    
    // 家族管理接口
    @GET
    @Path("/family/list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listFamilies() {
        List<com.motily.society.Family> families = societyService.listFamilies();
        return Response.ok().entity("{\"code\": 200, \"data\": " + families + "}")
                .build();
    }
    
    // DNA相关接口
    @GET
    @Path("/dna/structure")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDnaStructure() {
        return Response.ok().entity("{\"code\": 200, \"data\": {\"features\": " + DnaStructure.ALL_FEATURES + ", \"categories\": " + DnaStructure.getAllCategories() + "}}")
                .build();
    }
    
    @POST
    @Path("/dna/validate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response validateDna(DnaValidateRequest request) {
        DnaService.ValidationResult result = DnaService.validateDna(request.dna);
        return Response.ok().entity("{\"code\": 200, \"data\": {\"valid\": " + result.isValid() + ", \"errorMessage\": \"" + result.getErrorMessage() + "\"}}")
                .build();
    }
    
    @POST
    @Path("/dna/generate/random")
    @Produces(MediaType.APPLICATION_JSON)
    public Response generateRandomDna() {
        String dna = DnaService.generateRandomDna();
        return Response.ok().entity("{\"code\": 200, \"data\": {\"dna\": \"" + dna + "\"}}")
                .build();
    }
    
    @POST
    @Path("/dna/generate/preferences")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response generateDnaWithPreferences(DnaGenerateRequest request) {
        String dna = DnaService.generateDnaWithPreferences(request.preferences);
        return Response.ok().entity("{\"code\": 200, \"data\": {\"dna\": \"" + dna + "\"}}")
                .build();
    }
    
    @POST
    @Path("/dna/analyze")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response analyzeDna(DnaAnalyzeRequest request) {
        DnaService.DnaAnalysisResult result = DnaService.analyzeDna(request.dna);
        return Response.ok().entity("{\"code\": 200, \"data\": " + result + "}")
                .build();
    }
    
    @POST
    @Path("/dna/features")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDnaFeatures(DnaFeaturesRequest request) {
        java.util.Map<Integer, Integer> features = DnaService.getFeatureValues(request.dna);
        return Response.ok().entity("{\"code\": 200, \"data\": " + features + "}")
                .build();
    }
    
    @POST
    @Path("/dna/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateDnaFeatures(DnaUpdateRequest request) {
        String updatedDna = DnaService.updateFeatureValues(request.dna, request.features);
        return Response.ok().entity("{\"code\": 200, \"data\": {\"dna\": \"" + updatedDna + "\"}}")
                .build();
    }
    
    // DNA相关请求类
    public static class DnaValidateRequest {
        public String dna;
    }
    
    public static class DnaGenerateRequest {
        public java.util.Map<String, Integer> preferences;
    }
    
    public static class DnaAnalyzeRequest {
        public String dna;
    }
    
    public static class DnaFeaturesRequest {
        public String dna;
    }
    
    public static class DnaUpdateRequest {
        public String dna;
        public java.util.Map<Integer, Integer> features;
    }

    @POST
    @Path("/human/batch-create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response batchCreateHumans(BatchCreateRequest request) {
        int count = request.count > 0 ? request.count : 1000;
        if (count > 50000) {
            count = 50000;
        }

        int totalCreated = 0;
        int transactionSize = 5000; // 每个事务处理5000个

        System.out.println("开始批量创建: " + count + " 人");

        for (int i = 0; i < count; i += transactionSize) {
            int batchSize = Math.min(transactionSize, count - i);
            System.out.println("处理事务: " + (i/transactionSize + 1) + ", 大小: " + batchSize);
            totalCreated += createBatchInTransaction(batchSize);
            System.out.println("事务完成，累计创建: " + totalCreated + " 人");
        }

        long totalPopulation = humanService.countHumans();
        System.out.println("批量创建完成: " + totalCreated + " 人, 总人口: " + totalPopulation);

        // 清除相关缓存，确保统计数据更新
        statsCacheService.clearBatchCreateCache();

        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("created", totalCreated);
        data.put("totalPopulation", totalPopulation);

        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("code", 200);
        response.put("data", data);

        return Response.ok(response).build();
    }

    @Transactional
    public int createBatchInTransaction(int count) {
        Timeline activeTimeline = Timeline.find("status = 1 ORDER BY id DESC").firstResult();
        int currentYear = activeTimeline != null ? activeTimeline.currentYear : java.time.LocalDate.now().getYear();
        Random rng = new Random();
        int chunkSize = 2000;
        int totalCreated = 0;
        var em = Human.getEntityManager();

        for (int i = 0; i < count; i += chunkSize) {
            int batchSize = Math.min(chunkSize, count - i);
            for (int j = 0; j < batchSize; j++) {
                Human human = populationInitializer.generateSingleHuman(currentYear, rng);
                em.persist(human);
                totalCreated++;
            }
            em.flush();
            em.clear();
        }

        return totalCreated;
    }

    @GET
    @Path("/human/batch-status/{taskId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBatchStatus(@PathParam("taskId") String taskId) {
        AsyncBatchService.BatchTask task = asyncBatchService.getTaskStatus(taskId);
        if (task == null) {
            return Response.status(Response.Status.NOT_FOUND).entity(java.util.Map.of("code", 404, "message", "任务不存在")).build();
        }
        return Response.ok(java.util.Map.of("code", 200, "data", task.toMap())).build();
    }

    @GET
    @Path("/human/distribution/region")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRegionDistribution() {
        List<Human> humans = Human.find("deathYear is null").list();

        java.util.Map<Integer, Integer> regionCount = new java.util.HashMap<>();

        for (Human human : humans) {
            int regionId = human.regionId != null ? human.regionId : 0;
            regionCount.put(regionId, regionCount.getOrDefault(regionId, 0) + 1);
        }

        long total = humans.size();
        java.util.List<java.util.Map<String, Object>> distribution = new java.util.ArrayList<>();

        for (java.util.Map.Entry<Integer, Integer> entry : regionCount.entrySet()) {
            java.util.Map<String, Object> item = new java.util.HashMap<>();
            item.put("regionId", entry.getKey());
            item.put("count", entry.getValue());
            item.put("percentage", total > 0 ? Math.round((entry.getValue() * 100.0 / total) * 10.0) / 10.0 : 0.0);
            distribution.add(item);
        }

        distribution.sort((a, b) -> ((Number) b.get("count")).intValue() - ((Number) a.get("count")).intValue());

        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("distribution", distribution);
        data.put("total", total);

        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("code", 200);
        response.put("data", data);

        return Response.ok(response).build();
    }

    @GET
    @Path("/marriage/list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listMarriages(@QueryParam("page") int page, @QueryParam("size") int size) {
        if (page < 0) page = 0;
        if (size <= 0) size = 20;

        long total = Marriage.count();
        List<Marriage> marriages = Marriage.findAll().page(page, size).list();

        java.util.Map<String, Object> responseData = new java.util.HashMap<>();
        responseData.put("list", marriages);
        responseData.put("total", total);

        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("code", 200);
        response.put("data", responseData);

        return Response.ok(response).build();
    }

    @GET
    @Path("/region/list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listRegions() {
        regionService.initRegions();
        List<Region> regions = Region.findAll().list();

        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("code", 200);
        response.put("data", regions);

        return Response.ok(response).build();
    }

    @GET
    @Path("/human/derived-stats")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDerivedStats() {
        List<Human> allHumans = Human.findAll().list();
        List<Human> aliveHumans = allHumans.stream().filter(h -> h.deathYear == null).collect(java.util.stream.Collectors.toList());
        long totalPopulation = aliveHumans.size();

        if (totalPopulation == 0) {
            java.util.Map<String, Object> emptyData = new java.util.HashMap<>();
            emptyData.put("birthRate", 0.0);
            emptyData.put("deathRate", 0.0);
            emptyData.put("marriageRate", 0.0);
            emptyData.put("giniCoefficient", 0.0);
            emptyData.put("avgWealthByClass", new java.util.HashMap<>());
            emptyData.put("populationGrowthRate", 0.0);
            emptyData.put("ageDistribution", new java.util.HashMap<>());

            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("code", 200);
            response.put("data", emptyData);

            return Response.ok(response).build();
        }

        Timeline activeTimeline = Timeline.find("status = 1 ORDER BY id DESC").firstResult();
        int currentYear = activeTimeline != null ? activeTimeline.currentYear : java.time.LocalDate.now().getYear();
        long birthCount = 0;
        long deathCount = 0;
        long marriageCount = 0;
        double totalWealth = 0.0;
        long maleCount = 0;
        long femaleCount = 0;

        java.util.Map<Integer, Long> classWealthSum = new java.util.HashMap<>();
        java.util.Map<Integer, Long> classCount = new java.util.HashMap<>();
        java.util.Map<String, Integer> ageDistribution = new java.util.HashMap<>();
        ageDistribution.put("0-18", 0);
        ageDistribution.put("19-35", 0);
        ageDistribution.put("36-55", 0);
        ageDistribution.put("56+", 0);

        for (Human h : aliveHumans) {
            if (h.birthYear == currentYear) birthCount++;
            if ("married".equals(h.maritalStatus)) marriageCount++;
            totalWealth += h.wealth;

            int gender = h.gender;
            if (gender == 1) {
                maleCount++;
            } else if (gender == 0) {
                femaleCount++;
            }

            int cls = h.socialClass;
            classWealthSum.put(cls, classWealthSum.getOrDefault(cls, 0L) + (long) h.wealth);
            classCount.put(cls, classCount.getOrDefault(cls, 0L) + 1L);

            int age = currentYear - h.birthYear;
            if (age <= 18) {
                ageDistribution.put("0-18", ageDistribution.get("0-18") + 1);
            } else if (age <= 35) {
                ageDistribution.put("19-35", ageDistribution.get("19-35") + 1);
            } else if (age <= 55) {
                ageDistribution.put("36-55", ageDistribution.get("36-55") + 1);
            } else {
                ageDistribution.put("56+", ageDistribution.get("56+") + 1);
            }
        }

        for (Human h : allHumans) {
            if (h.deathYear != null && h.deathYear == currentYear) deathCount++;
        }

        double birthRate = birthCount * 1.0 / totalPopulation;
        double deathRate = deathCount * 1.0 / totalPopulation;

        long marriageAgeCount = 0;
        for (Human h : aliveHumans) {
            int age = currentYear - h.birthYear;
            if (age >= 22) {
                marriageAgeCount++;
            }
        }
        double marriageRate = marriageAgeCount > 0 ? marriageCount * 1.0 / marriageAgeCount : 0.0;

        java.util.List<double[]> wealthList = new java.util.ArrayList<>();
        for (Human h : aliveHumans) {
            wealthList.add(new double[]{h.wealth});
        }
        wealthList.sort((a, b) -> Double.compare(a[0], b[0]));

        double giniCoefficient = calculateGini(wealthList);

        java.util.Map<String, Double> avgWealthByClass = new java.util.LinkedHashMap<>();
        avgWealthByClass.put("lower", classCount.containsKey(1) ? classWealthSum.get(1) * 1.0 / classCount.get(1) : 0.0);
        avgWealthByClass.put("middle", classCount.containsKey(2) ? classWealthSum.get(2) * 1.0 / classCount.get(2) : 0.0);
        avgWealthByClass.put("upper", classCount.containsKey(3) ? classWealthSum.get(3) * 1.0 / classCount.get(3) : 0.0);

        double populationGrowthRate = (birthCount - deathCount) * 1.0 / totalPopulation;

        long youngCount = 0, workingCount = 0, oldCount = 0;
        for (Human h : aliveHumans) {
            int age = currentYear - h.birthYear;
            if (age < 18) youngCount++;
            else if (age < 65) workingCount++;
            else oldCount++;
        }
        double dependencyRatio = (workingCount > 0) ? (double)(youngCount + oldCount) / workingCount * 100.0 : 0.0;

        long urbanCount = 0;
        for (Human h : aliveHumans) {
            if (h.socialClass >= 2) {
                urbanCount++;
                continue;
            }
            if (h.occupation != null) {
                String occ = h.occupation.toLowerCase();
                if (occ.contains("程序员") || occ.contains("工程师") || occ.contains("设计师") ||
                    occ.contains("银行") || occ.contains("公务员") || occ.contains("教师") ||
                    occ.contains("医生") || occ.contains("护士") || occ.contains("会计") ||
                    occ.contains("律师") || occ.contains("经理") || occ.contains("销售") ||
                    occ.contains("主播") || occ.contains("运营")) {
                    urbanCount++;
                }
            }
        }
        double urbanizationRate = (totalPopulation > 0) ? (double) urbanCount / totalPopulation * 100.0 : 0.0;

        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("birthRate", Math.round(birthRate * 10000.0) / 10000.0);
        data.put("deathRate", Math.round(deathRate * 10000.0) / 10000.0);
        data.put("marriageRate", Math.round(marriageRate * 10000.0) / 10000.0);
        data.put("giniCoefficient", Math.round(giniCoefficient * 10000.0) / 10000.0);
        data.put("avgWealthByClass", avgWealthByClass);
        data.put("populationGrowthRate", Math.round(populationGrowthRate * 10000.0) / 10000.0);
        data.put("ageDistribution", ageDistribution);
        data.put("dependencyRatio", Math.round(dependencyRatio * 100.0) / 100.0);
        data.put("urbanizationRate", Math.round(urbanizationRate * 100.0) / 100.0);
        data.put("maleCount", maleCount);
        data.put("femaleCount", femaleCount);
        data.put("maleRatio", Math.round(maleCount * 100.0 / totalPopulation * 10.0) / 10.0);
        data.put("femaleRatio", Math.round(femaleCount * 100.0 / totalPopulation * 10.0) / 10.0);

        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("code", 200);
        response.put("data", data);

        return Response.ok(response).build();
    }

    private double calculateGini(java.util.List<double[]> sortedWealth) {
        int n = sortedWealth.size();
        if (n <= 1) return 0.0;

        double totalWealth = 0.0;
        for (double[] w : sortedWealth) {
            totalWealth += w[0];
        }
        if (totalWealth == 0.0) return 0.0;

        double cumulativePopulation = 0.0;
        double cumulativeWealth = 0.0;
        double areaUnderCurve = 0.0;

        for (int i = 0; i < n; i++) {
            double popShare = 1.0 / n;
            double wealthShare = sortedWealth.get(i)[0] / totalWealth;

            double prevPopShare = cumulativePopulation;
            double prevWealthShare = cumulativeWealth;

            cumulativePopulation += popShare;
            cumulativeWealth += wealthShare;

            areaUnderCurve += (prevWealthShare + cumulativeWealth) * popShare / 2.0;
        }

        double gini = 1.0 - 2.0 * areaUnderCurve;
        return Math.max(0.0, gini);
    }

    public static class BatchCreateRequest {
        public int count;
    }

    @POST
    @Path("/human/backfill-region")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response backfillRegionIds() {
        long missingCount = Human.count("regionId IS NULL OR regionId = 0");

        if (missingCount == 0) {
            java.util.Map<String, Object> data = new java.util.HashMap<>();
            data.put("updated", 0);
            data.put("message", "所有记录已有 regionId，无需回填");

            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("code", 200);
            response.put("data", data);

            return Response.ok(response).build();
        }

        Random rng = new Random(42);
        int batchSize = 500;
        int updatedCount = 0;
        int totalBatches = (int) Math.ceil((double) missingCount / batchSize);

        for (int batch = 0; batch < totalBatches; batch++) {
            List<Human> batchHumans = Human.find("regionId IS NULL OR regionId = 0")
                    .range(0, batchSize)
                    .list();

            if (batchHumans.isEmpty()) break;

            for (Human human : batchHumans) {
                human.regionId = regionService.assignRandomRegion(rng);
                human.persist();
            }

            Human.getEntityManager().flush();
            Human.getEntityManager().clear();

            updatedCount += batchHumans.size();
        }

        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("updated", updatedCount);
        data.put("message", "成功回填 " + updatedCount + " 条记录的 regionId");

        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("code", 200);
        response.put("data", data);

        return Response.ok(response).build();
    }
}
