package com.motily.human;

import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@ApplicationScoped
public class MemoryService {
    // 形成记忆
    public void formMemory(Human human, String eventType, int year, String description, int impactLevel) {
        // 创建经历记录
        HumanExperience experience = new HumanExperience();
        experience.human = human;
        experience.eventType = eventType;
        experience.eventYear = year;
        experience.description = description;
        experience.impactLevel = impactLevel;
        experience.createdAt = LocalDateTime.now();
        experience.persist();
        
        // 创建记忆记录
        HumanMemory memory = new HumanMemory();
        memory.human = human;
        memory.eventType = eventType;
        memory.eventYear = year;
        memory.description = description;
        memory.impactLevel = impactLevel;
        memory.memoryStrength = calculateInitialMemoryStrength(impactLevel);
        memory.relatedExperienceId = experience.id;
        memory.createdAt = LocalDateTime.now();
        memory.lastAccessedAt = LocalDateTime.now();
        memory.persist();
    }
    
    // 计算初始记忆强度
    private double calculateInitialMemoryStrength(int impactLevel) {
        switch (impactLevel) {
            case 1: return 0.6; // 低影响
            case 2: return 0.8; // 中影响
            case 3: return 0.95; // 高影响
            default: return 0.7;
        }
    }
    
    // 获取记忆
    public List<HumanMemory> getMemories(Human human) {
        return HumanMemory.find("human.id = ?1 ORDER BY lastAccessedAt DESC", human.id).list();
    }
    
    // 获取最近的记忆
    public List<HumanMemory> getRecentMemories(Human human, int limit) {
        return HumanMemory.find("human.id = ?1 ORDER BY lastAccessedAt DESC", human.id).page(io.quarkus.panache.common.Page.of(0, limit)).list();
    }
    
    // 分析记忆对决策的影响
    public Map<String, Double> analyzeMemoryImpact(Human human) {
        List<HumanMemory> memories = getMemories(human);
        Map<String, Double> impactMap = new HashMap<>();
        
        // 初始化影响因素
        impactMap.put("education", 0.0);
        impactMap.put("career", 0.0);
        impactMap.put("marriage", 0.0);
        impactMap.put("health", 0.0);
        impactMap.put("social", 0.0);
        
        // 分析记忆影响
        for (HumanMemory memory : memories) {
            String eventType = memory.eventType;
            double strength = memory.memoryStrength;
            
            // 根据事件类型更新影响因素
            switch (eventType) {
                case "education":
                    impactMap.put("education", impactMap.get("education") + strength);
                    break;
                case "career":
                    impactMap.put("career", impactMap.get("career") + strength);
                    break;
                case "marriage":
                    impactMap.put("marriage", impactMap.get("marriage") + strength);
                    break;
                case "health":
                    impactMap.put("health", impactMap.get("health") + strength);
                    break;
                case "social":
                    impactMap.put("social", impactMap.get("social") + strength);
                    break;
            }
        }
        
        return impactMap;
    }
    
    // 记忆衰减
    public void decayMemories(Human human) {
        List<HumanMemory> memories = HumanMemory.find("human.id = ?1", human.id).list();
        for (HumanMemory memory : memories) {
            // 计算记忆衰减
            double timeSinceCreation = Duration.between(memory.createdAt, LocalDateTime.now()).toDays();
            double decayFactor = Math.exp(-timeSinceCreation / 365.0); // 一年衰减到原来的 1/e
            memory.memoryStrength *= decayFactor;
            
            // 如果记忆强度低于阈值，删除记忆
            if (memory.memoryStrength < 0.1) {
                memory.delete();
            } else {
                memory.persist();
            }
        }
    }
}