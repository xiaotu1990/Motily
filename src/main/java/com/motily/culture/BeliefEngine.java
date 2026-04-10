package com.motily.culture;

import com.motily.human.Human;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@ApplicationScoped
public class BeliefEngine {

    private static final String[] CULTURAL_VALUES = {"传统主义", "现代主义", "自由主义", "保守主义", "进步主义"};
    private static final String[] POLITICAL_BELIEFS = {"左派", "右派", "中间派", "自由主义", "保守主义"};
    private static final String[] RELIGIOUS_BELIEFS = {"无神论", "有神论", "佛教", "基督教", "伊斯兰教"};

    @Transactional
    public void processWeeklyBeliefs(int currentYear, int currentWeek, Random rng) {
        List<Human> humans = Human.findAll().list();
        
        for (Human human : humans) {
            if (human.deathYear != null) {
                continue;
            }
            
            int age = currentYear - human.birthYear;
            processBeliefsForHuman(human, age, currentYear, rng);
        }
    }

    @Transactional
    protected void processBeliefsForHuman(Human human, int age, int currentYear, Random rng) {
        updateBeliefs(human, age, rng);
        spreadBeliefs(human, rng);
        applyBeliefEffects(human);
    }

    @Transactional
    protected void updateBeliefs(Human human, int age, Random rng) {
        if (age < 18) {
            return;
        }
        
        double changeProbability = calculateBeliefChangeProbability(age);
        if (rng.nextDouble() < changeProbability) {
            updateCulturalValues(human, rng);
            updatePoliticalBeliefs(human, rng);
            updateReligiousBeliefs(human, rng);
            human.updatedAt = LocalDateTime.now();
            human.persist();
        }
    }

    private double calculateBeliefChangeProbability(int age) {
        if (age < 25) {
            return 0.05;
        } else if (age < 40) {
            return 0.02;
        } else if (age < 60) {
            return 0.01;
        } else {
            return 0.005;
        }
    }

    private void updateCulturalValues(Human human, Random rng) {
        String newValue = CULTURAL_VALUES[rng.nextInt(CULTURAL_VALUES.length)];
        // 这里应该更新 human.belief JSON 字段，为了简化，我们暂时跳过具体实现
    }

    private void updatePoliticalBeliefs(Human human, Random rng) {
        String newBelief = POLITICAL_BELIEFS[rng.nextInt(POLITICAL_BELIEFS.length)];
        // 这里应该更新 human.belief JSON 字段，为了简化，我们暂时跳过具体实现
    }

    private void updateReligiousBeliefs(Human human, Random rng) {
        String newBelief = RELIGIOUS_BELIEFS[rng.nextInt(RELIGIOUS_BELIEFS.length)];
        // 这里应该更新 human.belief JSON 字段，为了简化，我们暂时跳过具体实现
    }

    @Transactional
    protected void spreadBeliefs(Human human, Random rng) {
        double spreadProbability = 0.01;
        if (rng.nextDouble() < spreadProbability) {
            // 这里应该实现信念通过社交网络传播的逻辑
            // 为了简化，我们暂时跳过具体实现
        }
    }

    @Transactional
    protected void applyBeliefEffects(Human human) {
        // 信念对行为的影响
        // 为了简化，我们暂时跳过具体实现
    }

    public double calculateCulturalCompatibility(Human human1, Human human2) {
        // 计算两个数字人之间的文化兼容性
        // 为了简化，我们暂时返回一个随机值
        return Math.random();
    }

    public double calculatePoliticalCompatibility(Human human1, Human human2) {
        // 计算两个数字人之间的政治兼容性
        // 为了简化，我们暂时返回一个随机值
        return Math.random();
    }

    public double calculateReligiousCompatibility(Human human1, Human human2) {
        // 计算两个数字人之间的宗教兼容性
        // 为了简化，我们暂时返回一个随机值
        return Math.random();
    }

    public double getMarriageCompatibility(Human human1, Human human2) {
        double culturalCompatibility = calculateCulturalCompatibility(human1, human2);
        double politicalCompatibility = calculatePoliticalCompatibility(human1, human2);
        double religiousCompatibility = calculateReligiousCompatibility(human1, human2);
        
        return (culturalCompatibility + politicalCompatibility + religiousCompatibility) / 3.0;
    }

    public String getOccupationPreference(String culturalValue) {
        switch (culturalValue) {
            case "传统主义":
                return "教师";
            case "现代主义":
                return "程序员";
            case "自由主义":
                return "设计师";
            case "保守主义":
                return "公务员";
            case "进步主义":
                return "社会工作者";
            default:
                return "工人";
        }
    }

    public double getPoliticalParticipationRate(String politicalBelief) {
        switch (politicalBelief) {
            case "左派":
                return 0.8;
            case "右派":
                return 0.7;
            case "中间派":
                return 0.5;
            case "自由主义":
                return 0.6;
            case "保守主义":
                return 0.7;
            default:
                return 0.4;
        }
    }

    public double getSocialMovementParticipationRate(String politicalBelief, String culturalValue) {
        double baseRate = 0.1;
        
        if (politicalBelief.equals("左派") || politicalBelief.equals("进步主义")) {
            baseRate += 0.3;
        }
        
        if (culturalValue.equals("现代主义") || culturalValue.equals("自由主义")) {
            baseRate += 0.2;
        }
        
        return Math.min(0.8, baseRate);
    }
}
