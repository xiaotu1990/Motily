package com.motily.engine;

import com.motily.human.Human;
import com.motily.human.HumanService;
import com.motily.society.SocialEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@ApplicationScoped
public class DemographyEngine {

    @Inject
    HumanService humanService;

    @Inject
    GeneticsEngine geneticsEngine;

    private static final String[] SURNAMES = {
        "赵", "钱", "孙", "李", "周", "吴", "郑", "王", "冯", "陈",
        "褚", "卫", "蒋", "沈", "韩", "杨", "朱", "秦", "尤", "许",
        "何", "吕", "施", "张", "孔", "曹", "严", "华", "金", "魏",
        "陶", "姜", "戚", "谢", "邹", "喻", "柏", "水", "窦", "章",
        "云", "苏", "潘", "葛", "奚", "范", "彭", "郎", "鲁", "韦",
        "昌", "马", "苗", "凤", "花", "方", "俞", "任", "袁", "柳",
        "酆", "鲍", "史", "唐", "费", "廉", "岑", "薛", "雷", "贺",
        "倪", "汤", "滕", "殷", "罗", "毕", "郝", "邬", "安", "常"
    };

    private static final String[] MALE_NAMES = {
        "伟", "强", "磊", "军", "勇", "杰", "涛", "明", "超", "刚",
        "平", "辉", "鹏", "华", "飞", "龙", "斌", "波", "宇", "浩",
        "凯", "毅", "俊", "峰", "光", "亮", "健", "林", "海", "东",
        "昊", "然", "博", "文", "轩", "泽", "志", "翔", "天", "瑞"
    };

    private static final String[] FEMALE_NAMES = {
        "芳", "娟", "敏", "静", "丽", "艳", "娜", "燕", "玲", "萍",
        "红", "梅", "莉", "霞", "婷", "慧", "颖", "琳", "倩", "雪",
        "洁", "薇", "璐", "妍", "怡", "悦", "萱", "欣", "瑶", "琪",
        "雯", "晶", "茜", "蕾", "露", "琪", "宁", "馨", "媛", "菲"
    };

    @Transactional
    public void processWeeklyDemography(int currentYear, int currentWeek, Random rng) {
        List<Human> allHumans = Human.findAll().list();

        processDeaths(allHumans, currentYear, rng);
        checkPregnancy(allHumans, currentYear, rng);
        advancePregnanciesAndDeliver(allHumans, currentYear, currentWeek, rng);
    }

    @Transactional
    protected void processDeaths(List<Human> humans, int currentYear, Random rng) {
        for (Human human : humans) {
            if (human.deathYear != null) {
                continue;
            }

            int age = currentYear - human.birthYear;
            double weeklyDeathProb = calculateWeeklyDeathProbability(age);

            if (rng.nextDouble() < weeklyDeathProb) {
                human.deathYear = currentYear;
                human.updatedAt = LocalDateTime.now();
                human.persist();
                recordDeathEvent(human, currentYear);
            }
        }
    }

    @Transactional
    protected void checkPregnancy(List<Human> humans, int currentYear, Random rng) {
        for (Human human : humans) {
            if (!isEligibleForPregnancy(human, currentYear)) {
                continue;
            }

            int age = currentYear - human.birthYear;
            double weeklyPregnancyProb = calculateWeeklyPregnancyProbability(age);

            if (rng.nextDouble() < weeklyPregnancyProb) {
                human.pregnancyWeeks = 1;
                human.updatedAt = LocalDateTime.now();
                human.persist();
            }
        }
    }

    private boolean isEligibleForPregnancy(Human woman, int currentYear) {
        if (woman.gender != 0) {
            return false;
        }
        if (!"married".equals(woman.maritalStatus)) {
            return false;
        }
        if (woman.deathYear != null) {
            return false;
        }
        if (woman.pregnancyWeeks == null || woman.pregnancyWeeks > 0) {
            return false;
        }

        int age = currentYear - woman.birthYear;
        return age >= 20 && age <= 42;
    }

    @Transactional
    protected void advancePregnanciesAndDeliver(List<Human> humans, int currentYear, int currentWeek, Random rng) {
        for (Human woman : humans) {
            if (woman.gender != 0) {
                continue;
            }
            if (woman.pregnancyWeeks == null || woman.pregnancyWeeks <= 0) {
                continue;
            }

            woman.pregnancyWeeks++;

            if (woman.pregnancyWeeks >= 40) {
                deliverBaby(woman, currentYear, currentWeek, rng);
            } else {
                woman.updatedAt = LocalDateTime.now();
                woman.persist();
            }
        }
    }

    @Transactional
    protected void deliverBaby(Human mother, int currentYear, int currentWeek, Random rng) {
        Human father = null;
        if (mother.spouseId != null) {
            father = Human.findById(mother.spouseId);
        }

        String babyDnsCode;
        if (father != null && father.dnsCode != null && mother.dnsCode != null) {
            babyDnsCode = geneticsEngine.inheritDNA(father.dnsCode, mother.dnsCode, rng);
        } else {
            babyDnsCode = geneticsEngine.generateRandomDNA(rng);
        }

        int babyGender = geneticsEngine.determineChildGender(rng);

        int babySocialClass;
        if (father != null) {
            babySocialClass = geneticsEngine.calculateInheritedSocialClass(father, mother, rng);
        } else {
            babySocialClass = Math.max(1, Math.min(3, mother.socialClass + (rng.nextBoolean() ? 0 : -1)));
        }

        String babyName = generateChineseBabyName(babyGender, rng);

        Human newborn = new Human();
        newborn.name = babyName;
        newborn.gender = babyGender;
        newborn.birthYear = currentYear;
        newborn.deathYear = null;
        newborn.father = father;
        newborn.mother = mother;
        newborn.dnsCode = babyDnsCode;
        newborn.wealth = 500.0;
        newborn.socialClass = babySocialClass;
        newborn.occupation = null;
        newborn.personality = "{}";
        newborn.talent = "{}";
        newborn.belief = "{}";
        newborn.regionId = mother.regionId;
        newborn.maritalStatus = "single";
        newborn.spouseId = null;
        newborn.pregnancyWeeks = 0;
        newborn.createdAt = LocalDateTime.now();
        newborn.updatedAt = LocalDateTime.now();
        newborn.persist();
        recordBirthEvent(newborn, mother, currentYear);

        mother.pregnancyWeeks = 0;
        mother.updatedAt = LocalDateTime.now();
        mother.persist();
    }

    public double calculateWeeklyDeathProbability(int age) {
        double annualRate;

        if (age <= 1) {
            annualRate = 0.005;
        } else if (age <= 4) {
            annualRate = 0.0005;
        } else if (age <= 14) {
            annualRate = 0.0002;
        } else if (age <= 29) {
            annualRate = 0.0005;
        } else if (age <= 44) {
            annualRate = 0.001;
        } else if (age <= 59) {
            annualRate = 0.003;
        } else if (age <= 74) {
            annualRate = 0.012;
        } else {
            annualRate = 0.05;
        }

        return annualRate / 52.0;
    }

    public double calculateWeeklyPregnancyProbability(int age) {
        double baseRate = 0.0019;

        double ageMultiplier;
        if (age >= 20 && age <= 25) {
            ageMultiplier = 1.2;
        } else if (age >= 26 && age <= 32) {
            ageMultiplier = 1.5;
        } else if (age >= 33 && age <= 37) {
            ageMultiplier = 1.0;
        } else if (age >= 38 && age <= 42) {
            ageMultiplier = 0.5;
        } else {
            ageMultiplier = 0.0;
        }

        return baseRate * ageMultiplier;
    }

    private String generateChineseBabyName(int gender, Random rng) {
        String surname = SURNAMES[rng.nextInt(SURNAMES.length)];

        String givenName;
        if (gender == 1) {
            givenName = MALE_NAMES[rng.nextInt(MALE_NAMES.length)];
        } else {
            givenName = FEMALE_NAMES[rng.nextInt(FEMALE_NAMES.length)];
        }

        if (rng.nextDouble() < 0.3) {
            if (gender == 1) {
                givenName += MALE_NAMES[rng.nextInt(MALE_NAMES.length)];
            } else {
                givenName += FEMALE_NAMES[rng.nextInt(FEMALE_NAMES.length)];
            }
        }

        return surname + givenName;
    }

    private void recordDeathEvent(Human human, int year) {
        SocialEvent event = new SocialEvent();
        event.eventYear = year;
        event.eventType = "死亡";
        event.description = human.name + "（" + (year - human.birthYear) + "岁" + (human.gender == 1 ? ",男" : ",女") + "）于" + year + "年去世";
        event.influenceScore = human.socialClass == 3 ? 15 : (human.socialClass == 2 ? 10 : 5);
        event.probability = 100;
        event.createdAt = java.time.LocalDateTime.now();
        event.persist();
    }

    private void recordBirthEvent(Human baby, Human mother, int year) {
        SocialEvent event = new SocialEvent();
        event.eventYear = year;
        event.eventType = "出生";
        String genderStr = baby.gender == 1 ? "男婴" : "女婴";
        event.description = mother.name + "于" + year + "年产下一" + genderStr + "，名为" + baby.name;
        event.influenceScore = 8;
        event.probability = 100;
        event.createdAt = java.time.LocalDateTime.now();
        event.persist();
    }
}
