package com.motily.engine;

import com.motily.human.Human;
import com.motily.human.HumanService;
import com.motily.society.SocialEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Random;

@ApplicationScoped
public class MobilityEngine {
    @Inject
    HumanService humanService;

    private final Random random = new Random();

    private static final double PROB_LOWER_TO_MIDDLE = 0.00154;
    private static final double PROB_MIDDLE_TO_LOWER = 0.00096;
    private static final double PROB_MIDDLE_TO_UPPER = 0.00058;
    private static final double PROB_UPPER_TO_MIDDLE = 0.00096;
    private static final double PROB_UPPER_TO_LOWER = 0.00058;

    private static final double WEALTH_THRESHOLD_LOWER_TO_MIDDLE = 30000.0;
    private static final double WEALTH_THRESHOLD_MIDDLE_TO_LOWER = 20000.0;
    private static final double WEALTH_THRESHOLD_MIDDLE_TO_UPPER = 300000.0;
    private static final double WEALTH_THRESHOLD_UPPER_TO_MIDDLE = 150000.0;
    private static final double WEALTH_THRESHOLD_UPPER_TO_LOWER = 50000.0;

    private static final int MIN_AGE = 18;
    private static final int MAX_AGE = 60;
    private static final int PRIME_AGE_MIN = 25;
    private static final int PRIME_AGE_MAX = 45;
    private static final double PRIME_AGE_MULTIPLIER = 1.3;
    private static final double NON_PRIME_AGE_MULTIPLIER = 0.7;

    @Transactional
    public void processWeeklyMobility(int currentYear) {
        List<Human> livingHumans = Human.find("deathYear is null").list();

        for (Human human : livingHumans) {
            int age = currentYear - human.birthYear;

            if (age < MIN_AGE || age > MAX_AGE) {
                continue;
            }

            double ageMultiplier = calculateAgeMultiplier(age);
            processMobilityForHuman(human, ageMultiplier, currentYear);
        }
    }

    private void processMobilityForHuman(Human human, double ageMultiplier, int currentYear) {
        switch (human.socialClass) {
            case 1:
                processLowerClassMobility(human, ageMultiplier, currentYear);
                break;
            case 2:
                processMiddleClassMobility(human, ageMultiplier, currentYear);
                break;
            case 3:
                processUpperClassMobility(human, ageMultiplier, currentYear);
                break;
        }
    }

    private void processLowerClassMobility(Human human, double ageMultiplier, int currentYear) {
        double adjustedProb = PROB_LOWER_TO_MIDDLE * ageMultiplier;

        if (random.nextDouble() < adjustedProb) {
            if (checkWealthCondition(human, 2)) {
                int fromClass = human.socialClass;
                human.socialClass = 2;
                human.persist();
                recordMobilityEvent(human, fromClass, 2, currentYear);
            }
        }
    }

    private void processMiddleClassMobility(Human human, double ageMultiplier, int currentYear) {
        double probToLower = PROB_MIDDLE_TO_LOWER * ageMultiplier;
        double probToUpper = PROB_MIDDLE_TO_UPPER * ageMultiplier;

        if (random.nextDouble() < probToLower) {
            if (checkWealthCondition(human, 1)) {
                int fromClass = human.socialClass;
                human.socialClass = 1;
                human.persist();
                recordMobilityEvent(human, fromClass, 1, currentYear);
                return;
            }
        }

        if (random.nextDouble() < probToUpper) {
            if (checkWealthCondition(human, 3)) {
                int fromClass = human.socialClass;
                human.socialClass = 3;
                human.persist();
                recordMobilityEvent(human, fromClass, 3, currentYear);
            }
        }
    }

    private void processUpperClassMobility(Human human, double ageMultiplier, int currentYear) {
        double probToMiddle = PROB_UPPER_TO_MIDDLE * ageMultiplier;
        double probToLower = PROB_UPPER_TO_LOWER * ageMultiplier;

        if (random.nextDouble() < probToMiddle) {
            if (checkWealthCondition(human, 2)) {
                int fromClass = human.socialClass;
                human.socialClass = 2;
                human.persist();
                recordMobilityEvent(human, fromClass, 2, currentYear);
                return;
            }
        }

        if (random.nextDouble() < probToLower) {
            if (checkWealthCondition(human, 1)) {
                int fromClass = human.socialClass;
                human.socialClass = 1;
                human.persist();
                recordMobilityEvent(human, fromClass, 1, currentYear);
            }
        }
    }

    public double getWeeklyTransitionProbability(int fromClass, int toClass) {
        return switch (fromClass) {
            case 1 -> switch (toClass) {
                case 2 -> PROB_LOWER_TO_MIDDLE;
                default -> 0.0;
            };
            case 2 -> switch (toClass) {
                case 1 -> PROB_MIDDLE_TO_LOWER;
                case 3 -> PROB_MIDDLE_TO_UPPER;
                default -> 0.0;
            };
            case 3 -> switch (toClass) {
                case 2 -> PROB_UPPER_TO_MIDDLE;
                case 1 -> PROB_UPPER_TO_LOWER;
                default -> 0.0;
            };
            default -> 0.0;
        };
    }

    public boolean checkWealthCondition(Human human, int targetClass) {
        int currentClass = human.socialClass;

        if (currentClass == 1 && targetClass == 2) {
            return human.wealth > WEALTH_THRESHOLD_LOWER_TO_MIDDLE;
        } else if (currentClass == 2 && targetClass == 1) {
            return human.wealth < WEALTH_THRESHOLD_MIDDLE_TO_LOWER;
        } else if (currentClass == 2 && targetClass == 3) {
            return human.wealth > WEALTH_THRESHOLD_MIDDLE_TO_UPPER;
        } else if (currentClass == 3 && targetClass == 2) {
            return human.wealth < WEALTH_THRESHOLD_UPPER_TO_MIDDLE;
        } else if (currentClass == 3 && targetClass == 1) {
            return human.wealth < WEALTH_THRESHOLD_UPPER_TO_LOWER;
        }

        return false;
    }

    private double calculateAgeMultiplier(int age) {
        if (age >= PRIME_AGE_MIN && age <= PRIME_AGE_MAX) {
            return PRIME_AGE_MULTIPLIER;
        } else {
            return NON_PRIME_AGE_MULTIPLIER;
        }
    }

    private void recordMobilityEvent(Human human, int fromClass, int toClass, int year) {
        SocialEvent event = new SocialEvent();
        event.eventYear = year;
        event.eventType = "阶层流动";
        String[] classNames = {"", "底层", "中层", "上层"};
        event.description = human.name + "从" + classNames[fromClass] + "跃升/滑落至" + classNames[toClass] + "，财富¥" + String.format("%,.0f", human.wealth);
        event.influenceScore = Math.abs(toClass - fromClass) * 10;
        event.probability = 100;
        event.createdAt = java.time.LocalDateTime.now();
        event.persist();
    }
}
