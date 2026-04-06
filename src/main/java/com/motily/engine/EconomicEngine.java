package com.motily.engine;

import com.motily.human.Human;
import com.motily.human.HumanLifecycle;
import com.motily.human.HumanService;
import com.motily.society.SocialEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@ApplicationScoped
public class EconomicEngine {

    private static final int WEEKS_PER_YEAR = 52;
    private static final int CYCLE_YEARS = 12;
    private static final int TOTAL_CYCLE_WEEKS = CYCLE_YEARS * WEEKS_PER_YEAR;
    private static final double MIN_WEALTH = 100.0;
    private static final double RANDOM_FLUCTUATION_RANGE = 0.001;

    @Inject
    HumanService humanService;

    @Inject
    HumanLifecycle humanLifecycle;

    private Random random = new Random();

    @Transactional
    public void processWeeklyEconomy(int currentYear, int currentWeek) {
        int totalWeek = currentYear * WEEKS_PER_YEAR + currentWeek;
        String phase = getEconomicPhase(totalWeek);

        List<Human> humans = humanService.getHumansByYear(currentYear);

        for (Human human : humans) {
            if (!humanLifecycle.isAlive(human, currentYear)) {
                continue;
            }

            double oldWealth = human.wealth;

            double annualRate = getPhaseGrowthRate(human.socialClass, phase);
            double weeklyRate = annualRate / WEEKS_PER_YEAR;
            double fluctuation = (random.nextDouble() * 2 - 1) * RANDOM_FLUCTUATION_RANGE;
            double actualWeeklyRate = weeklyRate + fluctuation;

            double wealthChange = human.wealth * actualWeeklyRate;
            human.wealth += wealthChange;

            if (human.wealth < MIN_WEALTH) {
                human.wealth = MIN_WEALTH;
            }

            human.updatedAt = LocalDateTime.now();
            human.persist();

            recordEconomicEvent(human, oldWealth, human.wealth, phase, currentYear);
        }
    }

    public String getEconomicPhase(int totalWeek) {
        int positionInCycle = totalWeek % TOTAL_CYCLE_WEEKS;
        int phaseLength = TOTAL_CYCLE_WEEKS / 5;

        if (positionInCycle < phaseLength) {
            return "prosperity";
        } else if (positionInCycle < phaseLength * 2) {
            return "overheat";
        } else if (positionInCycle < phaseLength * 3) {
            return "recession";
        } else if (positionInCycle < phaseLength * 4) {
            return "depression";
        } else {
            return "recovery";
        }
    }

    public double getPhaseGrowthRate(int socialClass, String phase) {
        switch (phase) {
            case "prosperity":
                switch (socialClass) {
                    case 1: return 0.02;
                    case 2: return 0.05;
                    case 3: return 0.08;
                    default: return 0.03;
                }
            case "overheat":
                switch (socialClass) {
                    case 1: return -0.01;
                    case 2: return 0.03;
                    case 3: return 0.10;
                    default: return 0.02;
                }
            case "recession":
                switch (socialClass) {
                    case 1: return -0.12;
                    case 2: return -0.08;
                    case 3: return -0.05;
                    default: return -0.08;
                }
            case "depression":
                switch (socialClass) {
                    case 1: return -0.20;
                    case 2: return -0.15;
                    case 3: return -0.08;
                    default: return -0.15;
                }
            case "recovery":
                switch (socialClass) {
                    case 1: return 0.03;
                    case 2: return 0.04;
                    case 3: return 0.03;
                    default: return 0.03;
                }
            default:
                return 0.03;
        }
    }

    private void recordEconomicEvent(Human human, double oldWealth, double newWealth, String phase, int year) {
        double changePercent = Math.abs(newWealth - oldWealth) / Math.max(oldWealth, 1.0);
        if (changePercent < 0.30) return;

        SocialEvent event = new SocialEvent();
        event.eventYear = year;
        event.eventType = "经济变化";
        String direction = newWealth > oldWealth ? "增长" : "缩水";
        event.description = human.name + "财富" + direction + String.format("%.1f%%", changePercent * 100)
                + "（" + phase + "期），现¥" + String.format("%,.0f", newWealth);
        event.influenceScore = (int)(changePercent * 50);
        event.probability = 100;
        event.createdAt = java.time.LocalDateTime.now();
        event.persist();
    }
}
