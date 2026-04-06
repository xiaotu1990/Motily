package com.motily.society;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public SocialIndicator calculateSocialIndicators(int year) {
        List<Human> humans = humanService.getHumansByYear(year);
        SocialIndicator indicator = new SocialIndicator();
        indicator.year = year;

        try {
            Map<String, Integer> classDistribution = calculateClassDistribution(humans);
            indicator.classDistribution = objectMapper.writeValueAsString(classDistribution);

            Map<String, Integer> occupationStructure = calculateOccupationStructure(humans);
            indicator.occupationStructure = objectMapper.writeValueAsString(occupationStructure);

            Map<String, Double> wealthDistribution = calculateWealthDistribution(humans);
            double dependencyRatio = calculateDependencyRatio(humans, year);
            double urbanizationRate = calculateUrbanizationRate(humans);
            wealthDistribution.put("dependencyRatio", dependencyRatio);
            wealthDistribution.put("urbanizationRate", urbanizationRate);
            indicator.wealthDistribution = objectMapper.writeValueAsString(wealthDistribution);
        } catch (JsonProcessingException e) {
            indicator.classDistribution = "{}";
            indicator.occupationStructure = "{}";
            indicator.wealthDistribution = "{}";
        }

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

    private double calculateDependencyRatio(List<Human> humans, int currentYear) {
        int dependent = 0;
        int workingAge = 0;

        for (Human human : humans) {
            if (human.deathYear != null) continue;
            int age = currentYear - human.birthYear;
            if (age < 18 || age >= 65) {
                dependent++;
            } else {
                workingAge++;
            }
        }

        if (workingAge == 0) return 0.0;
        return (double) dependent / workingAge * 100.0;
    }

    private double calculateUrbanizationRate(List<Human> humans) {
        long urban = 0;
        long total = 0;

        for (Human human : humans) {
            if (human.deathYear != null) continue;
            total++;
            if (human.socialClass >= 2) {
                urban++;
            } else if (human.occupation != null) {
                String occ = human.occupation.toLowerCase();
                if (occ.contains("程序员") || occ.contains("工程师") || occ.contains("设计师") ||
                    occ.contains("银行") || occ.contains("公务员") || occ.contains("教师") ||
                    occ.contains("医生") || occ.contains("护士") || occ.contains("会计") ||
                    occ.contains("律师") || occ.contains("经理") || occ.contains("销售") ||
                    occ.contains("主播") || occ.contains("运营")) {
                    urban++;
                }
            }
        }

        if (total == 0) return 0.0;
        return (double) urban / total * 100.0;
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
    
    public void evolveSociety(int year, String theme) {
        // 计算社会指标
        SocialIndicator indicator = calculateSocialIndicators(year);
        indicator.persist();
        
        // 生成社会事件，考虑模拟主题
        SocialEvent event = generateSocialEvent(year, theme);
        event.persist();
    }
    
    public SocialEvent generateSocialEvent(int year, String theme) {
        // 根据模拟主题生成不同类型的社会事件
        SocialEvent event = new SocialEvent();
        event.eventYear = year;
        event.createdAt = LocalDateTime.now();
        
        // 根据主题生成事件
        String eventType = "社会事件";
        String description = "在" + year + "年发生的重要社会事件";
        int influenceScore = 50;
        int probability = 50;
        
        switch (theme) {
            case "inflation":
                eventType = "经济事件";
                description = "在" + year + "年，通货膨胀率持续上升，物价普遍上涨";
                influenceScore = 70;
                probability = 80;
                break;
            case "deflation":
                eventType = "经济事件";
                description = "在" + year + "年，通货紧缩现象明显，物价持续下降";
                influenceScore = 65;
                probability = 75;
                break;
            case "recession":
                eventType = "经济事件";
                description = "在" + year + "年，经济陷入衰退，失业率上升";
                influenceScore = 80;
                probability = 70;
                break;
            case "boom":
                eventType = "经济事件";
                description = "在" + year + "年，经济繁荣发展，就业机会增加";
                influenceScore = 60;
                probability = 65;
                break;
            default:
                // 正常经济情况下的事件
                eventType = "社会事件";
                description = "在" + year + "年，社会稳定发展，各项指标正常";
                influenceScore = 50;
                probability = 50;
                break;
        }
        
        event.eventType = eventType;
        event.description = description;
        event.influenceScore = influenceScore;
        event.probability = probability;
        
        return event;
    }
}
