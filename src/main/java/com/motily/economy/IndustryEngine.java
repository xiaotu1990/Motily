package com.motily.economy;

import com.motily.human.Human;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@ApplicationScoped
public class IndustryEngine {

    private static final String[] INDUSTRIES = {"科技", "金融", "制造", "服务", "医疗", "教育", "农业", "能源"};
    private static final double[] BASE_GROWTH_RATES = {0.15, 0.10, 0.05, 0.08, 0.12, 0.07, 0.02, 0.06};
    private static final double[] VOLATILITY = {0.2, 0.15, 0.1, 0.12, 0.1, 0.08, 0.05, 0.18};
    private static final int[] CYCLE_LENGTHS = {8, 10, 12, 8, 6, 10, 15, 12};

    private double[] industryGrowthRates = new double[INDUSTRIES.length];
    private int[] industryCycles = new int[INDUSTRIES.length];

    public IndustryEngine() {
        for (int i = 0; i < INDUSTRIES.length; i++) {
            industryGrowthRates[i] = BASE_GROWTH_RATES[i];
            industryCycles[i] = 0;
        }
    }

    public void processWeeklyIndustry(int currentYear, int currentWeek, Random rng) {
        updateIndustryCycles(rng);
        updateIndustryGrowthRates();
        applyIndustryEffects(currentYear, rng);
    }

    protected void updateIndustryCycles(Random rng) {
        for (int i = 0; i < INDUSTRIES.length; i++) {
            industryCycles[i] = (industryCycles[i] + 1) % CYCLE_LENGTHS[i];
        }
    }

    protected void updateIndustryGrowthRates() {
        for (int i = 0; i < INDUSTRIES.length; i++) {
            double cycleFactor = calculateCycleFactor(industryCycles[i], CYCLE_LENGTHS[i]);
            industryGrowthRates[i] = BASE_GROWTH_RATES[i] * cycleFactor;
        }
    }

    private double calculateCycleFactor(int currentCycle, int cycleLength) {
        double phase = (double) currentCycle / cycleLength * 2 * Math.PI;
        return 0.8 + 0.4 * Math.sin(phase);
    }

    protected void applyIndustryEffects(int currentYear, Random rng) {
        List<Human> humans = Human.find("deathYear is null AND industry IS NOT NULL").list();

        for (Human human : humans) {
            applyIndustryEffectToHuman(human, currentYear, rng);
        }
    }

    protected void applyIndustryEffectToHuman(Human human, int currentYear, Random rng) {
        int industryIndex = getIndustryIndex(human.industry);
        if (industryIndex == -1) {
            return;
        }

        double growthRate = industryGrowthRates[industryIndex];
        double weeklyGrowth = growthRate / 52.0;

        double incomeEffect = human.wealth * weeklyGrowth * 0.1;
        if (incomeEffect > 0) {
            human.wealth += incomeEffect;
            human.updatedAt = LocalDateTime.now();
            human.persist();
        }

        if (rng.nextDouble() < 0.01) {
            updateOccupationBasedOnIndustry(human, industryIndex, rng);
        }
    }

    protected void updateOccupationBasedOnIndustry(Human human, int industryIndex, Random rng) {
        String industry = INDUSTRIES[industryIndex];
        double growthRate = industryGrowthRates[industryIndex];

        if (growthRate > 0.1) {
            if (rng.nextDouble() < 0.3) {
                human.socialClass = Math.min(3, human.socialClass + 1);
                human.updatedAt = LocalDateTime.now();
                human.persist();
            }
        } else if (growthRate < 0.03) {
            if (rng.nextDouble() < 0.2) {
                human.socialClass = Math.max(1, human.socialClass - 1);
                if (rng.nextDouble() < 0.1) {
                    human.occupation = null;
                }
                human.updatedAt = LocalDateTime.now();
                human.persist();
            }
        }
    }

    private int getIndustryIndex(String industry) {
        for (int i = 0; i < INDUSTRIES.length; i++) {
            if (INDUSTRIES[i].equals(industry)) {
                return i;
            }
        }
        return -1;
    }

    public double getIndustryGrowthRate(String industry) {
        int index = getIndustryIndex(industry);
        return index != -1 ? industryGrowthRates[index] : 0.05;
    }

    public String getIndustryStatus(String industry) {
        double growthRate = getIndustryGrowthRate(industry);
        if (growthRate > 0.15) {
            return "繁荣";
        } else if (growthRate > 0.08) {
            return "增长";
        } else if (growthRate > 0.03) {
            return "稳定";
        } else {
            return "衰退";
        }
    }

    public String getPromisingIndustry(Random rng) {
        double maxGrowthRate = -1;
        int bestIndustryIndex = 0;

        for (int i = 0; i < INDUSTRIES.length; i++) {
            if (industryGrowthRates[i] > maxGrowthRate) {
                maxGrowthRate = industryGrowthRates[i];
                bestIndustryIndex = i;
            }
        }

        return INDUSTRIES[bestIndustryIndex];
    }

    public String getDecliningIndustry(Random rng) {
        double minGrowthRate = Double.MAX_VALUE;
        int worstIndustryIndex = 0;

        for (int i = 0; i < INDUSTRIES.length; i++) {
            if (industryGrowthRates[i] < minGrowthRate) {
                minGrowthRate = industryGrowthRates[i];
                worstIndustryIndex = i;
            }
        }

        return INDUSTRIES[worstIndustryIndex];
    }

    public double calculateIndustryCompatibility(String industry, String occupation) {
        if (industry == null || occupation == null) {
            return 0.5;
        }

        String lowerIndustry = industry.toLowerCase();
        String lowerOccupation = occupation.toLowerCase();

        if (lowerIndustry.contains("科技") && (lowerOccupation.contains("程序员") || lowerOccupation.contains("工程师"))) {
            return 0.9;
        } else if (lowerIndustry.contains("金融") && (lowerOccupation.contains("银行") || lowerOccupation.contains("投资"))) {
            return 0.9;
        } else if (lowerIndustry.contains("医疗") && (lowerOccupation.contains("医生") || lowerOccupation.contains("护士"))) {
            return 0.9;
        } else if (lowerIndustry.contains("教育") && (lowerOccupation.contains("教师") || lowerOccupation.contains("教授"))) {
            return 0.9;
        } else {
            return 0.5;
        }
    }
}
