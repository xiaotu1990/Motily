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
import java.util.Random;

@ApplicationScoped
public class SocialEvolution {
    @Inject
    HumanService humanService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String[] QUARTERLY_EVENT_TYPES = {
        "政策调整", "科技突破", "文化事件", "教育改革", "公共卫生",
        "环境变化", "人口迁移", "社会运动", "法律修订", "外交事件"
    };

    private static final String[] QUARTERLY_EVENT_DESCS = {
        "政府出台新的社会保障政策，扩大福利覆盖范围",
        "重大科技创新成果发布，推动产业升级转型",
        "大型文化活动举办，促进社会文化交流融合",
        "教育体系改革方案落地，调整人才培养方向",
        "公共卫生体系加强建设，提升疾病防控能力",
        "极端天气频发，环境保护议题引发社会关注",
        "区域发展不均衡加剧人口流动，城市化进程加快",
        "民间社会组织活跃，推动社区治理模式创新",
        "新法律法规正式实施，影响社会生活多个方面",
        "国际交流合作深化，对外开放水平持续提升"
    };
    
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

    public void generateQuarterlyEvents(int year, int week, Random rng) {
        List<Human> aliveHumans = Human.find("deathYear is null").list();
        if (aliveHumans.isEmpty()) {
            return;
        }

        int quarter = (week - 1) / 13 + 1;

        int eventIndex = rng.nextInt(QUARTERLY_EVENT_TYPES.length);
        String eventType = QUARTERLY_EVENT_TYPES[eventIndex];
        String eventDesc = QUARTERLY_EVENT_DESCS[eventIndex];

        SocialEvent event = new SocialEvent();
        event.eventYear = year;
        event.eventType = eventType;
        event.description = year + "年第" + quarter + "季度：" + eventDesc;
        event.influenceScore = 20 + rng.nextInt(40);
        event.probability = 40 + rng.nextInt(50);
        event.createdAt = LocalDateTime.now();
        event.persist();

        long lowerCount = aliveHumans.stream().filter(h -> h.socialClass == 1).count();
        long middleCount = aliveHumans.stream().filter(h -> h.socialClass == 2).count();
        long upperCount = aliveHumans.stream().filter(h -> h.socialClass == 3).count();
        long total = aliveHumans.size();

        if (total > 0) {
            double lowerRatio = (double) lowerCount / total;
            if (lowerRatio > 0.7 && rng.nextDouble() < 0.3) {
                SocialEvent inequalityEvent = new SocialEvent();
                inequalityEvent.eventYear = year;
                inequalityEvent.eventType = "社会事件";
                inequalityEvent.description = year + "年第" + quarter + "季度：底层人口占比过高，社会不平等加剧，引发广泛关注";
                inequalityEvent.influenceScore = 55 + rng.nextInt(25);
                inequalityEvent.probability = 70 + rng.nextInt(20);
                inequalityEvent.createdAt = LocalDateTime.now();
                inequalityEvent.persist();
            }

            if (middleCount > 0 && rng.nextDouble() < 0.2) {
                SocialEvent middleEvent = new SocialEvent();
                middleEvent.eventYear = year;
                middleEvent.eventType = "经济事件";
                middleEvent.description = year + "年第" + quarter + "季度：中产阶级规模达" + Math.round((double) middleCount / total * 100) + "%，消费市场持续扩大";
                middleEvent.influenceScore = 30 + rng.nextInt(20);
                middleEvent.probability = 50 + rng.nextInt(30);
                middleEvent.createdAt = LocalDateTime.now();
                middleEvent.persist();
            }
        }
    }
}
