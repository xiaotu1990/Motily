package com.motily.engine;

import com.motily.human.Human;
import com.motily.society.SocialEvent;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@ApplicationScoped
public class EconomicEngine {

    private double gdpGrowthRate = 0.065;
    private double inflationRate = 0.025;
    private double unemploymentRate = 0.05;
    private int economicCycle = 0;

    public void processWeeklyEconomy(int currentYear, int currentWeek, Random rng) {
        updateEconomicIndicators(rng);
        applyEconomicEffects(currentYear, rng);
    }

    protected void updateEconomicIndicators(Random rng) {
        economicCycle = (economicCycle + 1) % 520;

        double cycleFactor = Math.sin(economicCycle / 520.0 * 2 * Math.PI);

        gdpGrowthRate = 0.065 + cycleFactor * 0.03 + (rng.nextDouble() * 0.01 - 0.005);
        inflationRate = 0.025 + cycleFactor * 0.015 + (rng.nextDouble() * 0.005 - 0.0025);
        unemploymentRate = 0.05 - cycleFactor * 0.02 + (rng.nextDouble() * 0.005 - 0.0025);

        gdpGrowthRate = Math.max(-0.05, Math.min(0.15, gdpGrowthRate));
        inflationRate = Math.max(0.0, Math.min(0.1, inflationRate));
        unemploymentRate = Math.max(0.01, Math.min(0.2, unemploymentRate));
    }

    protected void applyEconomicEffects(int currentYear, Random rng) {
        List<Human> humans = Human.find("deathYear is null").list();

        for (Human human : humans) {
            applyIncomeEffect(human);
            applyInflationEffect(human);
            applyUnemploymentEffect(human, rng);
        }
    }

    protected void applyIncomeEffect(Human human) {
        double weeklyIncome = calculateWeeklyIncome(human);
        if (weeklyIncome > 0) {
            human.wealth += weeklyIncome;
            human.updatedAt = LocalDateTime.now();
            human.persist();
        }
    }

    private double calculateWeeklyIncome(Human human) {
        double baseIncome = getBaseIncomeByClass(human.socialClass);
        double gdpEffect = 1.0 + gdpGrowthRate / 52.0;
        return baseIncome * gdpEffect;
    }

    private double getBaseIncomeByClass(int socialClass) {
        switch (socialClass) {
            case 1: return 200.0;
            case 2: return 800.0;
            case 3: return 3000.0;
            default: return 300.0;
        }
    }

    protected void applyInflationEffect(Human human) {
        double weeklyInflation = inflationRate / 52.0;
        double purchasingPowerLoss = human.wealth * weeklyInflation * 0.5;
        if (purchasingPowerLoss > 0) {
            human.wealth -= purchasingPowerLoss;
            human.updatedAt = LocalDateTime.now();
            human.persist();
        }
    }

    protected void applyUnemploymentEffect(Human human, Random rng) {
        if (rng.nextDouble() < unemploymentRate / 52.0) {
            if (human.occupation != null && rng.nextDouble() < 0.01) {
                String oldOccupation = human.occupation;
                human.occupation = null;
                human.updatedAt = LocalDateTime.now();
                human.persist();
            }
        } else if (human.occupation == null && rng.nextDouble() < 0.05) {
            human.occupation = "临时工";
            human.updatedAt = LocalDateTime.now();
            human.persist();
        }
    }

    public double getGdpGrowthRate() {
        return gdpGrowthRate;
    }

    public double getInflationRate() {
        return inflationRate;
    }

    public double getUnemploymentRate() {
        return unemploymentRate;
    }

    public String getEconomicPhase() {
        if (gdpGrowthRate > 0.08) {
            return "繁荣";
        } else if (gdpGrowthRate > 0.05) {
            return "增长";
        } else if (gdpGrowthRate > 0.02) {
            return "稳定";
        } else if (gdpGrowthRate > 0) {
            return "放缓";
        } else {
            return "衰退";
        }
    }

    public void applyEconomicShock(double shockMagnitude, int currentYear) {
        gdpGrowthRate -= shockMagnitude;
        unemploymentRate += shockMagnitude * 2;

        SocialEvent event = new SocialEvent();
        event.eventYear = currentYear;
        event.eventType = "经济事件";
        event.description = "经济冲击：GDP增速下降" + String.format("%.1f", shockMagnitude * 100) + "%，失业率上升至" + String.format("%.1f", unemploymentRate * 100) + "%";
        event.influenceScore = (int) (shockMagnitude * 100);
        event.probability = 80;
        event.createdAt = LocalDateTime.now();
        event.persist();
    }

    public double calculateWealthTax(double wealth) {
        if (wealth > 1000000) {
            return wealth * 0.03;
        } else if (wealth > 500000) {
            return wealth * 0.02;
        } else if (wealth > 200000) {
            return wealth * 0.01;
        } else {
            return 0;
        }
    }

    public double calculateSocialWelfare(int socialClass, int age) {
        if (socialClass == 1) {
            if (age > 65) {
                return 1500.0;
            } else if (age < 18) {
                return 500.0;
            } else {
                return 800.0;
            }
        } else if (socialClass == 2 && age > 65) {
            return 1000.0;
        }
        return 0;
    }
}
