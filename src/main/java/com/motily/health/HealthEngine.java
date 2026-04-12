package com.motily.health;

import com.motily.human.Human;
import com.motily.human.MemoryService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@ApplicationScoped
public class HealthEngine {

    @Inject
    MemoryService memoryService;

    private static final String[] HEALTH_STATUS = {"健康", "亚健康", "疾病", "重疾"};
    private static final int[] HEALTH_VALUE_RANGES = {80, 60, 40, 0};
    private static final double[] MORTALITY_MULTIPLIERS = {1.0, 1.5, 3.0, 10.0};
    private static final double[] FERTILITY_MULTIPLIERS = {1.0, 0.8, 0.5, 0.1};
    private static final double[] PRODUCTIVITY_MULTIPLIERS = {1.0, 0.9, 0.6, 0.2};

    public void processWeeklyHealth(int currentYear, int currentWeek, Random rng) {
        List<Human> humans = Human.find("deathYear is null").list();

        for (Human human : humans) {
            int age = currentYear - human.birthYear;
            processHealthForHuman(human, age, currentYear, rng);
        }
    }

    protected void processHealthForHuman(Human human, int age, int currentYear, Random rng) {
        updateHealthValue(human, age, rng);
        updateHealthStatus(human, currentYear);
        processDisease(human, age, rng);
        applyHealthEffects(human);
    }

    protected void updateHealthValue(Human human, int age, Random rng) {
        int baseChange = calculateBaseHealthChange(age);
        int randomChange = rng.nextInt(5) - 2;
        int totalChange = baseChange + randomChange;

        human.healthValue = Math.max(0, Math.min(100, human.healthValue + totalChange));
        human.updatedAt = LocalDateTime.now();
        human.persist();
    }

    private int calculateBaseHealthChange(int age) {
        if (age < 18) {
            return 1;
        } else if (age < 30) {
            return 0;
        } else if (age < 50) {
            return -1;
        } else if (age < 70) {
            return -2;
        } else {
            return -3;
        }
    }

    protected void updateHealthStatus(Human human, int currentYear) {
        String newStatus = getHealthStatusByValue(human.healthValue);
        if (!newStatus.equals(human.healthStatus)) {
            String oldStatus = human.healthStatus;
            human.healthStatus = newStatus;
            human.updatedAt = LocalDateTime.now();
            human.persist();

            try {
                String description = "健康状态从" + oldStatus + "变为" + newStatus;
                int impactLevel = 2;
                if (newStatus.equals("重疾")) {
                    impactLevel = 3;
                }
                memoryService.formMemory(human, "health", currentYear, description, impactLevel);
            } catch (Exception e) {
                System.err.println("记录健康记忆失败: " + e.getMessage());
            }
        }
    }

    private String getHealthStatusByValue(int healthValue) {
        for (int i = 0; i < HEALTH_VALUE_RANGES.length; i++) {
            if (healthValue >= HEALTH_VALUE_RANGES[i]) {
                return HEALTH_STATUS[i];
            }
        }
        return HEALTH_STATUS[HEALTH_STATUS.length - 1];
    }

    protected void processDisease(Human human, int age, Random rng) {
        double diseaseProbability = calculateDiseaseProbability(age, human.healthValue);
        if (rng.nextDouble() < diseaseProbability) {
            int diseaseImpact = rng.nextInt(10) + 5;
            human.healthValue = Math.max(0, human.healthValue - diseaseImpact);
            human.updatedAt = LocalDateTime.now();
            human.persist();
        }
    }

    private double calculateDiseaseProbability(int age, int healthValue) {
        double baseProbability = 0.01;
        double ageFactor = 1.0;
        double healthFactor = 1.0;

        if (age > 60) {
            ageFactor = 2.0;
        } else if (age > 40) {
            ageFactor = 1.5;
        }

        if (healthValue < 50) {
            healthFactor = 2.0;
        } else if (healthValue < 70) {
            healthFactor = 1.5;
        }

        return baseProbability * ageFactor * healthFactor;
    }

    protected void applyHealthEffects(Human human) {
        int statusIndex = getHealthStatusIndex(human.healthStatus);
        if (statusIndex >= 2) {
            double medicalCost = human.wealth * 0.005 * (statusIndex - 1);
            if (medicalCost > 0 && human.wealth > medicalCost) {
                human.wealth -= medicalCost;
                human.updatedAt = LocalDateTime.now();
                human.persist();
            }
        }
    }

    private int getHealthStatusIndex(String healthStatus) {
        for (int i = 0; i < HEALTH_STATUS.length; i++) {
            if (HEALTH_STATUS[i].equals(healthStatus)) {
                return i;
            }
        }
        return 0;
    }

    public double getMortalityMultiplier(String healthStatus) {
        int statusIndex = getHealthStatusIndex(healthStatus);
        return MORTALITY_MULTIPLIERS[statusIndex];
    }

    public double getFertilityMultiplier(String healthStatus) {
        int statusIndex = getHealthStatusIndex(healthStatus);
        return FERTILITY_MULTIPLIERS[statusIndex];
    }

    public double getProductivityMultiplier(String healthStatus) {
        int statusIndex = getHealthStatusIndex(healthStatus);
        return PRODUCTIVITY_MULTIPLIERS[statusIndex];
    }

    public boolean isHealthy(Human human) {
        return human.healthStatus.equals("健康") || human.healthStatus.equals("亚健康");
    }

    public boolean isSeriouslyIll(Human human) {
        return human.healthStatus.equals("重疾");
    }

    public double calculateHealthCareAccess(int regionId) {
        int regionType = regionId % 3;
        switch (regionType) {
            case 0: return 1.0;
            case 1: return 0.7;
            case 2: return 0.4;
            default: return 0.7;
        }
    }

    public double calculateRecoveryRate(Human human, int regionId, int currentYear) {
        double baseRate = 0.02;
        double healthcareAccess = calculateHealthCareAccess(regionId);
        double ageFactor = Math.max(0.5, 1.0 - (currentYear - human.birthYear) / 100.0);

        return baseRate * healthcareAccess * ageFactor;
    }
}
