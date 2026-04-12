package com.motily.economy;

import com.motily.human.Human;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@ApplicationScoped
public class InvestmentEngine {

    private static final String[] INVESTMENT_TYPES = {"股票", "房地产", "创业", "债券", "基金"};
    private static final double[] RISK_LEVELS = {0.8, 0.5, 0.9, 0.2, 0.4};
    private static final double[] RETURN_RATES = {0.15, 0.08, 0.3, 0.04, 0.07};
    private static final double[] VOLATILITY = {0.2, 0.1, 0.4, 0.05, 0.15};

    public void processWeeklyInvestment(int currentYear, int currentWeek, Random rng) {
        List<Human> humans = Human.find("deathYear is null").list();

        for (Human human : humans) {
            int age = currentYear - human.birthYear;
            if (age < 18) {
                continue;
            }

            processInvestmentForHuman(human, age, currentYear, rng);
        }
    }

    protected void processInvestmentForHuman(Human human, int age, int currentYear, Random rng) {
        if (shouldInvest(human, age, rng)) {
            makeInvestment(human, rng);
        }

        updateExistingInvestments(human, currentYear, rng);
    }

    private boolean shouldInvest(Human human, int age, Random rng) {
        if (human.wealth < 10000) {
            return false;
        }

        double investProbability = calculateInvestProbability(human, age);
        return rng.nextDouble() < investProbability;
    }

    private double calculateInvestProbability(Human human, int age) {
        double baseProbability = 0.1;

        double wealthFactor = Math.min(0.5, human.wealth / 100000.0);
        double ageFactor = 1.0;

        if (age >= 25 && age <= 50) {
            ageFactor = 1.5;
        } else if (age > 60) {
            ageFactor = 0.5;
        }

        return baseProbability + wealthFactor * ageFactor;
    }

    protected void makeInvestment(Human human, Random rng) {
        int investmentTypeIndex = rng.nextInt(INVESTMENT_TYPES.length);

        double investmentAmount = human.wealth * (0.05 + rng.nextDouble() * 0.15);
        if (investmentAmount > 0 && human.wealth > investmentAmount) {
            human.wealth -= investmentAmount;
            human.updatedAt = LocalDateTime.now();
            human.persist();
        }
    }

    protected void updateExistingInvestments(Human human, int currentYear, Random rng) {
        double returnRate = calculateInvestmentReturn(rng);
        double investmentValue = human.wealth * 0.2;
        double returnAmount = investmentValue * returnRate / 52.0;

        if (returnAmount != 0) {
            human.wealth += returnAmount;
            human.updatedAt = LocalDateTime.now();
            human.persist();
        }
    }

    private double calculateInvestmentReturn(Random rng) {
        double baseReturn = 0.07;
        double volatility = 0.15;
        double weeklyReturn = baseReturn / 52.0 + (rng.nextDouble() * 2 - 1) * volatility / Math.sqrt(52);
        return weeklyReturn;
    }

    public double getRiskLevel(String investmentType) {
        for (int i = 0; i < INVESTMENT_TYPES.length; i++) {
            if (INVESTMENT_TYPES[i].equals(investmentType)) {
                return RISK_LEVELS[i];
            }
        }
        return 0.5;
    }

    public double getExpectedReturn(String investmentType) {
        for (int i = 0; i < INVESTMENT_TYPES.length; i++) {
            if (INVESTMENT_TYPES[i].equals(investmentType)) {
                return RETURN_RATES[i];
            }
        }
        return 0.05;
    }

    public double getVolatility(String investmentType) {
        for (int i = 0; i < INVESTMENT_TYPES.length; i++) {
            if (INVESTMENT_TYPES[i].equals(investmentType)) {
                return VOLATILITY[i];
            }
        }
        return 0.1;
    }

    public String getSuitableInvestmentType(Human human, int currentYear) {
        int age = currentYear - human.birthYear;
        double riskTolerance = calculateRiskTolerance(human, age);

        int bestTypeIndex = 0;
        double bestMatch = Double.MAX_VALUE;

        for (int i = 0; i < INVESTMENT_TYPES.length; i++) {
            double riskDifference = Math.abs(RISK_LEVELS[i] - riskTolerance);
            if (riskDifference < bestMatch) {
                bestMatch = riskDifference;
                bestTypeIndex = i;
            }
        }

        return INVESTMENT_TYPES[bestTypeIndex];
    }

    private double calculateRiskTolerance(Human human, int age) {
        double baseTolerance = 0.5;

        double ageFactor = 1.0;
        if (age < 30) {
            ageFactor = 1.2;
        } else if (age > 60) {
            ageFactor = 0.6;
        }

        double wealthFactor = Math.min(1.5, human.wealth / 500000.0);

        return Math.min(1.0, baseTolerance * ageFactor * wealthFactor);
    }

    public double calculateInvestmentDiversificationBenefit(int investmentCount) {
        return Math.min(0.3, investmentCount * 0.05);
    }
}
