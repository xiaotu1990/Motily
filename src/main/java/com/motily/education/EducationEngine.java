package com.motily.education;

import com.motily.human.Human;
import com.motily.human.MemoryService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@ApplicationScoped
public class EducationEngine {

    @Inject
    MemoryService memoryService;

    private static final String[] EDUCATION_LEVELS = {"小学", "初中", "高中", "大学", "研究生"};
    private static final double[] EDUCATION_INVESTMENT_RATES = {0.05, 0.1, 0.15, 0.2, 0.25};
    private static final double[] EDUCATION_RETURN_RATES = {1.0, 1.2, 1.5, 2.0, 2.5};
    private static final int[] EDUCATION_DURATION = {6, 3, 3, 4, 2};

    public void processWeeklyEducation(int currentYear, int currentWeek, Random rng) {
        List<Human> humans = Human.find("deathYear is null").list();

        for (Human human : humans) {
            int age = currentYear - human.birthYear;
            if (age < 6) {
                continue;
            }

            processEducationForHuman(human, age, currentYear, rng);
        }
    }

    protected void processEducationForHuman(Human human, int age, int currentYear, Random rng) {
        if (shouldUpgradeEducation(human, age, rng)) {
            upgradeEducation(human, currentYear, rng);
        }

        processEducationInvestment(human, rng);
        applyEducationReturn(human);
    }

    private boolean shouldUpgradeEducation(Human human, int age, Random rng) {
        int currentLevelIndex = getEducationLevelIndex(human.educationLevel);
        if (currentLevelIndex >= EDUCATION_LEVELS.length - 1) {
            return false;
        }

        int nextLevelAge = getNextLevelAge(currentLevelIndex);
        if (age < nextLevelAge) {
            return false;
        }

        double upgradeProbability = getUpgradeProbability(human, currentLevelIndex, rng);
        return rng.nextDouble() < upgradeProbability;
    }

    private int getEducationLevelIndex(String educationLevel) {
        for (int i = 0; i < EDUCATION_LEVELS.length; i++) {
            if (EDUCATION_LEVELS[i].equals(educationLevel)) {
                return i;
            }
        }
        return 0;
    }

    private int getNextLevelAge(int currentLevelIndex) {
        int age = 6;
        for (int i = 0; i <= currentLevelIndex; i++) {
            age += EDUCATION_DURATION[i];
        }
        return age;
    }

    private double getUpgradeProbability(Human human, int currentLevelIndex, Random rng) {
        double baseProbability = 0.8;

        double wealthFactor = Math.min(1.5, human.wealth / 50000.0);
        double socialClassFactor = human.socialClass / 3.0;
        double ageFactor = 1.0;

        double probability = baseProbability * wealthFactor * socialClassFactor * ageFactor;
        return Math.min(0.95, probability);
    }

    protected void upgradeEducation(Human human, int currentYear, Random rng) {
        int currentLevelIndex = getEducationLevelIndex(human.educationLevel);
        if (currentLevelIndex >= EDUCATION_LEVELS.length - 1) {
            return;
        }

        String nextLevel = EDUCATION_LEVELS[currentLevelIndex + 1];
        String currentLevel = human.educationLevel;
        human.educationLevel = nextLevel;
        human.updatedAt = LocalDateTime.now();
        human.persist();

        try {
            String description = "从" + currentLevel + "升级到" + nextLevel;
            memoryService.formMemory(human, "education", currentYear, description, 2);
        } catch (Exception e) {
            System.err.println("记录教育记忆失败: " + e.getMessage());
        }
    }

    protected void processEducationInvestment(Human human, Random rng) {
        int levelIndex = getEducationLevelIndex(human.educationLevel);
        double investmentRate = EDUCATION_INVESTMENT_RATES[levelIndex];

        double investmentAmount = human.wealth * investmentRate / 52.0;
        if (investmentAmount > 0 && human.wealth > investmentAmount) {
            human.wealth -= investmentAmount;
            human.updatedAt = LocalDateTime.now();
            human.persist();
        }
    }

    protected void applyEducationReturn(Human human) {
        int levelIndex = getEducationLevelIndex(human.educationLevel);
        double returnRate = EDUCATION_RETURN_RATES[levelIndex];

        double weeklyReturn = human.wealth * (returnRate - 1.0) / 52.0 / 10.0;
        if (weeklyReturn > 0) {
            human.wealth += weeklyReturn;
            human.updatedAt = LocalDateTime.now();
            human.persist();
        }
    }

    public double getEducationReturnMultiplier(String educationLevel) {
        int levelIndex = getEducationLevelIndex(educationLevel);
        return EDUCATION_RETURN_RATES[levelIndex];
    }

    public int getEducationLevelValue(String educationLevel) {
        return getEducationLevelIndex(educationLevel) + 1;
    }

    public boolean isEligibleForHigherEducation(Human human, int currentYear) {
        int age = currentYear - human.birthYear;
        int currentLevelIndex = getEducationLevelIndex(human.educationLevel);

        return age >= 18 && currentLevelIndex < 3;
    }

    public double calculateLearningAbility(int age) {
        if (age < 6) {
            return 0.2;
        } else if (age < 12) {
            return 0.5;
        } else if (age < 25) {
            return 1.0;
        } else if (age < 40) {
            return 0.8;
        } else if (age < 60) {
            return 0.6;
        } else {
            return 0.4;
        }
    }
}
