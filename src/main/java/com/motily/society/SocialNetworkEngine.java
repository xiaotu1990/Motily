package com.motily.society;

import com.motily.human.Human;
import com.motily.human.MemoryService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@ApplicationScoped
public class SocialNetworkEngine {

    @Inject
    MemoryService memoryService;

    public void processWeeklyNetwork(int currentYear, int currentWeek, Random rng) {
        updateSocialNetworks(currentYear, rng);
        spreadInformation(currentYear, rng);
        applyNetworkEffects();
    }

    protected void updateSocialNetworks(int currentYear, Random rng) {
        List<Human> humans = Human.find("deathYear is null").list();

        for (Human human : humans) {
            int age = currentYear - human.birthYear;
            if (age < 18) {
                continue;
            }

            updateNetworkForHuman(human, age, currentYear, rng);
        }
    }

    protected void updateNetworkForHuman(Human human, int age, int currentYear, Random rng) {
        int originalNetworkSize = human.networkSize;

        if (rng.nextDouble() < 0.1) {
            int networkChange = rng.nextInt(3) - 1;
            human.networkSize = Math.max(0, human.networkSize + networkChange);
            human.updatedAt = LocalDateTime.now();
            human.persist();
        }

        if (age >= 18 && age <= 30) {
            if (rng.nextDouble() < 0.05) {
                human.networkSize += 1;
                human.updatedAt = LocalDateTime.now();
                human.persist();
            }
        } else if (age > 60) {
            if (rng.nextDouble() < 0.03) {
                human.networkSize = Math.max(0, human.networkSize - 1);
                human.updatedAt = LocalDateTime.now();
                human.persist();
            }
        }

        if (human.networkSize != originalNetworkSize) {
            try {
                String description;
                if (human.networkSize > originalNetworkSize) {
                    description = "社交网络从" + originalNetworkSize + "人增加到" + human.networkSize + "人";
                } else {
                    description = "社交网络从" + originalNetworkSize + "人减少到" + human.networkSize + "人";
                }
                memoryService.formMemory(human, "social", currentYear, description, 1);
            } catch (Exception e) {
                System.err.println("记录社交记忆失败: " + e.getMessage());
            }
        }
    }

    protected void spreadInformation(int currentYear, Random rng) {
        List<Human> humans = Human.find("deathYear is null").list();

        for (Human human : humans) {
            if (rng.nextDouble() < 0.05) {
                spreadInformationFromHuman(human, humans, rng);
            }
        }
    }

    protected void spreadInformationFromHuman(Human source, List<Human> humans, Random rng) {
        int spreadCount = Math.min(3, source.networkSize / 10);

        for (int i = 0; i < spreadCount; i++) {
            Human target = humans.get(rng.nextInt(humans.size()));
            if (target.id == source.id) {
                continue;
            }
        }
    }

    protected void applyNetworkEffects() {
        List<Human> humans = Human.find("deathYear is null").list();

        for (Human human : humans) {
            applyNetworkEffectToHuman(human);
        }
    }

    protected void applyNetworkEffectToHuman(Human human) {
        double networkBonus = calculateNetworkBonus(human.networkSize);

        double wealthEffect = human.wealth * networkBonus * 0.001;
        if (wealthEffect > 0) {
            human.wealth += wealthEffect;
            human.updatedAt = LocalDateTime.now();
            human.persist();
        }
    }

    private double calculateNetworkBonus(int networkSize) {
        return Math.min(0.5, networkSize * 0.01);
    }

    public double calculateSocialCapital(Human human) {
        double baseCapital = 1.0;
        double networkFactor = 1.0 + calculateNetworkBonus(human.networkSize);
        double educationFactor = 1.0 + (getEducationLevelValue(human.educationLevel) * 0.1);
        double wealthFactor = 1.0 + Math.min(0.5, human.wealth / 100000.0);

        return baseCapital * networkFactor * educationFactor * wealthFactor;
    }

    private int getEducationLevelValue(String educationLevel) {
        switch (educationLevel) {
            case "小学": return 1;
            case "初中": return 2;
            case "高中": return 3;
            case "大学": return 4;
            case "研究生": return 5;
            default: return 2;
        }
    }

    public double calculateNetworkCompatibility(Human human1, Human human2) {
        double ageCompatibility = calculateAgeCompatibility(human1, human2);
        double educationCompatibility = calculateEducationCompatibility(human1, human2);
        double industryCompatibility = calculateIndustryCompatibility(human1, human2);

        return (ageCompatibility + educationCompatibility + industryCompatibility) / 3.0;
    }

    private double calculateAgeCompatibility(Human human1, Human human2) {
        int ageDiff = Math.abs(human1.birthYear - human2.birthYear);
        return Math.max(0.3, 1.0 - ageDiff * 0.02);
    }

    private double calculateEducationCompatibility(Human human1, Human human2) {
        int level1 = getEducationLevelValue(human1.educationLevel);
        int level2 = getEducationLevelValue(human2.educationLevel);
        int levelDiff = Math.abs(level1 - level2);

        return Math.max(0.3, 1.0 - levelDiff * 0.1);
    }

    private double calculateIndustryCompatibility(Human human1, Human human2) {
        if (human1.industry == null || human2.industry == null) {
            return 0.5;
        }

        return human1.industry.equals(human2.industry) ? 1.0 : 0.5;
    }

    public boolean shouldFormConnection(Human human1, Human human2, Random rng) {
        double compatibility = calculateNetworkCompatibility(human1, human2);
        double connectionProbability = compatibility * 0.1;

        return rng.nextDouble() < connectionProbability;
    }

    public int getOptimalNetworkSize(int age, int socialClass) {
        int baseSize;
        switch (socialClass) {
            case 1: baseSize = 20;
                break;
            case 2: baseSize = 40;
                break;
            case 3: baseSize = 60;
                break;
            default: baseSize = 30;
        }

        if (age < 25) {
            return baseSize;
        } else if (age < 45) {
            return baseSize + 10;
        } else if (age < 65) {
            return baseSize + 5;
        } else {
            return baseSize - 5;
        }
    }

    public double calculateInformationSpreadRate(Human human) {
        double baseRate = 0.1;
        double networkFactor = Math.min(2.0, 1.0 + human.networkSize * 0.01);
        double activityFactor = 0.8 + Math.random() * 0.4;

        return baseRate * networkFactor * activityFactor;
    }
}
