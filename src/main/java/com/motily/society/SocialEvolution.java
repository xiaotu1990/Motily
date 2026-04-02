package com.motily.society;

import com.motily.human.Human;
import com.motily.human.HumanService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class SocialEvolution {
    @Inject
    HumanService humanService;
    
    public SocialIndicator calculateSocialIndicators(int year) {
        List<Human> humans = humanService.getHumansByYear(year);
        SocialIndicator indicator = new SocialIndicator();
        indicator.year = year;
        
        // 计算阶层分布
        Map<String, Integer> classDistribution = calculateClassDistribution(humans);
        indicator.classDistribution = classDistribution.toString();
        
        // 计算职业结构
        Map<String, Integer> occupationStructure = calculateOccupationStructure(humans);
        indicator.occupationStructure = occupationStructure.toString();
        
        // 计算财富分布
        Map<String, Double> wealthDistribution = calculateWealthDistribution(humans);
        indicator.wealthDistribution = wealthDistribution.toString();
        
        indicator.createdAt = LocalDateTime.now();
        return indicator;
    }
    
    private Map<String, Integer> calculateClassDistribution(List<Human> humans) {
        Map<String, Integer> distribution = new HashMap<>();
        distribution.put("lower", 0);
        distribution.put("middle", 0);
        distribution.put("upper", 0);
        
        for (Human human : humans) {
            switch (human.socialClass) {
                case 1: distribution.put("lower", distribution.get("lower") + 1); break;
                case 2: distribution.put("middle", distribution.get("middle") + 1); break;
                case 3: distribution.put("upper", distribution.get("upper") + 1); break;
            }
        }
        
        return distribution;
    }
    
    private Map<String, Integer> calculateOccupationStructure(List<Human> humans) {
        Map<String, Integer> structure = new HashMap<>();
        
        for (Human human : humans) {
            if (human.occupation != null) {
                structure.put(human.occupation, structure.getOrDefault(human.occupation, 0) + 1);
            }
        }
        
        return structure;
    }
    
    private Map<String, Double> calculateWealthDistribution(List<Human> humans) {
        Map<String, Double> distribution = new HashMap<>();
        double totalWealth = 0;
        
        for (Human human : humans) {
            totalWealth += human.wealth;
        }
        
        distribution.put("total", totalWealth);
        distribution.put("average", totalWealth / humans.size());
        
        // 计算不同阶层的财富占比
        double lowerWealth = 0;
        double middleWealth = 0;
        double upperWealth = 0;
        
        for (Human human : humans) {
            switch (human.socialClass) {
                case 1: lowerWealth += human.wealth; break;
                case 2: middleWealth += human.wealth; break;
                case 3: upperWealth += human.wealth; break;
            }
        }
        
        distribution.put("lower", lowerWealth);
        distribution.put("middle", middleWealth);
        distribution.put("upper", upperWealth);
        
        return distribution;
    }
    
    public SocialEvent generateSocialEvent(int year) {
        // 简单生成社会事件，实际应该更复杂
        SocialEvent event = new SocialEvent();
        event.eventYear = year;
        event.eventType = "社会事件";
        event.description = "在" + year + "年发生的重要社会事件";
        event.influenceScore = 50;
        event.createdAt = LocalDateTime.now();
        return event;
    }
    
    public void evolveSociety(int year) {
        // 计算社会指标
        SocialIndicator indicator = calculateSocialIndicators(year);
        indicator.persist();
        
        // 生成社会事件
        SocialEvent event = generateSocialEvent(year);
        event.persist();
    }
}
