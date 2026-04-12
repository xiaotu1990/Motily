package com.motily.engine;

import com.motily.human.Human;
import com.motily.human.HumanService;
import com.motily.human.MemoryService;
import com.motily.society.Marriage;
import com.motily.society.SocialEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@ApplicationScoped
public class MarriageEngine {
    @Inject
    HumanService humanService;
    
    @Inject
    MemoryService memoryService;

    private final Random random = new Random();

    public boolean checkEligibility(Human h1, Human h2, int currentYear) {
        if (h1 == null || h2 == null) {
            return false;
        }

        int age1 = currentYear - h1.birthYear;
        int age2 = currentYear - h2.birthYear;

        boolean bothAlive = h1.deathYear == null && h2.deathYear == null;
        boolean oppositeGender = h1.gender != h2.gender;
        boolean bothSingle = "single".equals(h1.maritalStatus) && "single".equals(h2.maritalStatus);
        boolean bothAdult = age1 >= 18 && age2 >= 18;
        boolean notCloseRelatives = !isCloseRelative(h1, h2);

        return bothAlive && oppositeGender && bothSingle && bothAdult && notCloseRelatives;
    }

    private boolean isCloseRelative(Human h1, Human h2) {
        if (h1.father != null && h2.father != null) {
            if (h1.father.id.equals(h2.father.id)) {
                return true;
            }
        }
        if (h1.mother != null && h2.mother != null) {
            if (h1.mother.id.equals(h2.mother.id)) {
                return true;
            }
        }
        return false;
    }

    public Marriage performMarriage(Human husband, Human wife, int year, int week) {
        Marriage marriage = new Marriage();
        marriage.husbandId = husband.id;
        marriage.wifeId = wife.id;
        marriage.weddingYear = year;
        marriage.weddingWeek = week;
        marriage.status = "active";
        marriage.createdAt = LocalDateTime.now();
        marriage.updatedAt = LocalDateTime.now();
        marriage.persist();

        husband.maritalStatus = "married";
        husband.spouseId = wife.id;
        husband.persist();

        wife.maritalStatus = "married";
        wife.spouseId = husband.id;
        wife.persist();

        // 记录婚姻经历和记忆
        String husbandDescription = "与" + wife.name + "结婚";
        String wifeDescription = "与" + husband.name + "结婚";
        memoryService.formMemory(husband, "marriage", year, husbandDescription, 3);
        memoryService.formMemory(wife, "marriage", year, wifeDescription, 3);

        recordMarriageEvent(husband, wife, year, week);

        return marriage;
    }

    public void processWeeklyMarriages(int currentYear, int currentWeek) {
        List<Human> eligibleMen = getEligibleMen(currentYear);
        List<Human> eligibleWomen = getEligibleWomen(currentYear);

        if (eligibleWomen.isEmpty()) {
            return;
        }

        List<Human> availableWomen = new ArrayList<>(eligibleWomen);

        for (Human man : eligibleMen) {
            if (availableWomen.isEmpty()) {
                break;
            }

            double probability = calculateMarriageProbability(man, currentYear);
            if (random.nextDouble() < probability) {
                Human selectedWife = selectBestMatch(man, availableWomen, currentYear);
                if (selectedWife != null) {
                    performMarriage(man, selectedWife, currentYear, currentWeek);
                    availableWomen.remove(selectedWife);
                }
            }
        }
    }

    private List<Human> getEligibleMen(int currentYear) {
        return Human.find(
            "gender = 1 and maritalStatus = 'single' and deathYear is null and birthYear <= ?1 and birthYear >= ?2",
            currentYear - 18,
            currentYear - 50
        ).list();
    }

    private List<Human> getEligibleWomen(int currentYear) {
        return Human.find(
            "gender = 0 and maritalStatus = 'single' and deathYear is null and birthYear <= ?1 and birthYear >= ?2",
            currentYear - 18,
            currentYear - 45
        ).list();
    }

    private Human selectBestMatch(Human man, List<Human> candidates, int currentYear) {
        if (candidates.isEmpty()) {
            return null;
        }

        List<Human> scoredCandidates = new ArrayList<>(candidates);
        Collections.shuffle(scoredCandidates);

        scoredCandidates.sort((w1, w2) -> {
            int score1 = calculateMatchScore(man, w1, currentYear);
            int score2 = calculateMatchScore(man, w2, currentYear);
            return Integer.compare(score2, score1);
        });

        return scoredCandidates.get(0);
    }

    private int calculateMatchScore(Human man, Human woman, int currentYear) {
        int score = 0;

        if (man.regionId != null && man.regionId.equals(woman.regionId)) {
            score += 30;
        }

        int ageDiff = Math.abs((currentYear - man.birthYear) - (currentYear - woman.birthYear));
        if (ageDiff <= 3) {
            score += 25;
        } else if (ageDiff <= 5) {
            score += 15;
        } else if (ageDiff <= 8) {
            score += 5;
        }

        int classDiff = Math.abs(man.socialClass - woman.socialClass);
        if (classDiff == 0) {
            score += 20;
        } else if (classDiff == 1) {
            score += 10;
        }

        return score;
    }

    public double calculateMarriageProbability(Human human, int currentYear) {
        int age = currentYear - human.birthYear;

        if (age < 18 || age > 50) {
            return 0;
        }

        double baseProbability;
        if (age >= 22 && age <= 35) {
            baseProbability = 0.003;
        } else if (age >= 18) {
            baseProbability = 0.002;
        } else {
            baseProbability = 0.0015;
        }

        switch (human.socialClass) {
            case 2:
                baseProbability *= 1.2;
                break;
            case 3:
                baseProbability *= 0.9;
                break;
            case 1:
                baseProbability *= 0.95;
                break;
        }

        return baseProbability;
    }

    private void recordMarriageEvent(Human husband, Human wife, int year, int week) {
        SocialEvent event = new SocialEvent();
        event.eventYear = year;
        event.eventType = "结婚";
        event.description = husband.name + "与" + wife.name + "于第" + year + "年第" + week + "周结为夫妻";
        event.influenceScore = 6;
        event.probability = 100;
        event.createdAt = java.time.LocalDateTime.now();
        event.persist();
    }
}
