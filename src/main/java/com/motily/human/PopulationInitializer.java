package com.motily.human;

import com.motily.dna.DnaService;
import com.motily.occupation.OccupationRegistry;
import com.motily.region.RegionService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@ApplicationScoped
public class PopulationInitializer {

    @Inject
    OccupationRegistry occupationRegistry;

    @Inject
    RegionService regionService;

    @Inject
    HumanService humanService;

    private static final String[] SURNAMES = {
        "王", "李", "张", "刘", "陈", "杨", "黄", "赵", "吴", "周",
        "徐", "孙", "马", "朱", "胡", "郭", "何", "高", "林", "罗",
        "郑", "梁", "谢", "宋", "唐", "许", "韩", "冯", "邓", "曹",
        "彭", "曾", "肖", "田", "董", "袁", "潘", "于", "蒋", "蔡",
        "余", "杜", "叶", "程", "苏", "魏", "吕", "丁", "任", "沈"
    };

    private static final String[] COMPOUND_SURNAMES = {
        "欧阳", "上官", "司马", "诸葛", "司徒", "司空", "公孙", "东方", "皇甫", "尉迟"
    };

    private static final String[] MALE_NAME_SINGLE = {
        "伟", "强", "军", "勇", "杰", "涛", "磊", "超", "明", "华",
        "洋", "健", "辉", "刚", "峰", "宇", "博", "浩", "轩", "辰"
    };

    private static final String[] MALE_NAME_DOUBLE = {
        "明轩", "子豪", "浩然", "雨泽", "宇轩", "俊驰", "文博", "天佑", "子骞", "昊然",
        "致远", "俊楠", "鸿涛", "伟祺", "荣轩", "越泽", "浩宇", "瑾瑜", "皓轩", "擎宇"
    };

    private static final String[] FEMALE_NAME_SINGLE = {
        "芳", "娜", "婷", "静", "丽", "艳", "敏", "霞", "燕", "玲",
        "娟", "莉", "雪", "梅", "兰", "菊", "萍", "瑶", "玉", "蓉"
    };

    private static final String[] FEMALE_NAME_DOUBLE = {
        "雨桐", "梦琪", "忆柳", "之桃", "慕青", "问兰", "尔岚", "元香", "初夏", "沛菡",
        "傲珊", "曼文", "乐菱", "痴珊", "恨玉", "惜文", "香寒", "新柔", "语蓉", "海安"
    };

    public List<Human> initializePopulation(int count, int baseYear, Random rng) {
        List<Human> population = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            Human human = generateSingleHuman(baseYear, rng);
            population.add(human);
        }
        return population;
    }

    public Human generateSingleHuman(int baseYear, Random rng) {
        int age = generateAge(rng);
        int gender = generateGender(rng);
        int socialClass = generateSocialClass(rng);
        double wealth = generateParetoWealth(socialClass, rng);
        String name = generateChineseName(gender, rng);
        String dnsCode = DnaService.generateRandomDna();
        String occupation = occupationRegistry.getRandomOccupation(socialClass, rng).name();
        int regionId = regionService.assignRandomRegion(rng);
        int birthYear = baseYear - age;

        Human human = new Human();
        human.dnsCode = dnsCode;
        human.name = name;
        human.gender = gender;
        human.birthYear = birthYear;
        human.wealth = wealth;
        human.socialClass = socialClass;
        human.occupation = occupation;
        human.personality = "{}";
        human.talent = "{}";
        human.belief = "{}";
        human.regionId = regionId;
        human.educationLevel = getEducationLevelByAge(age, socialClass, rng);
        human.healthStatus = "健康";
        human.healthValue = 100;
        human.industry = getIndustryByOccupation(occupation);
        human.networkSize = getNetworkSizeByAge(age, socialClass, rng);
        human.maritalStatus = "single";
        human.spouseId = null;
        human.pregnancyWeeks = 0;
        human.createdAt = LocalDateTime.now();
        human.updatedAt = LocalDateTime.now();

        if (age >= 25 && rng.nextDouble() < 0.55) {
            human.maritalStatus = "married";
        }

        return human;
    }

    private int generateAge(Random rng) {
        double mu = 35.0;
        double sigma = 15.0;
        int age;
        do {
            age = (int) Math.round(rng.nextGaussian() * sigma + mu);
        } while (age < 18 || age > 80);
        return age;
    }

    private int generateGender(Random rng) {
        return rng.nextDouble() < 0.51 ? 1 : 0;
    }

    private int generateSocialClass(Random rng) {
        double r = rng.nextDouble();
        if (r < 0.60) return 1;
        if (r < 0.90) return 2;
        return 3;
    }

    private double generateParetoWealth(int socialClass, Random rng) {
        double shape = 2.0;
        double paretoValue = -Math.pow(rng.nextDouble(), -1.0 / shape);

        return switch (socialClass) {
            case 1 -> {
                double min = 2000.0, max = 80000.0, median = 15000.0;
                yield Math.max(min, Math.min(max, median * paretoValue));
            }
            case 2 -> {
                double min = 50000.0, max = 500000.0, median = 150000.0;
                yield Math.max(min, Math.min(max, median * paretoValue));
            }
            case 3 -> {
                double min = 200000.0, max = 50000000.0, median = 2000000.0;
                yield Math.max(min, Math.min(max, median * paretoValue));
            }
            default -> throw new IllegalArgumentException("Invalid social class: " + socialClass);
        };
    }

    private String generateChineseName(int gender, Random rng) {
        String surname;
        if (rng.nextDouble() < 0.05) {
            surname = COMPOUND_SURNAMES[rng.nextInt(COMPOUND_SURNAMES.length)];
        } else {
            surname = SURNAMES[rng.nextInt(SURNAMES.length)];
        }

        String givenName;
        if (gender == 1) {
            if (rng.nextDouble() < 0.7) {
                givenName = MALE_NAME_DOUBLE[rng.nextInt(MALE_NAME_DOUBLE.length)];
            } else {
                givenName = MALE_NAME_SINGLE[rng.nextInt(MALE_NAME_SINGLE.length)];
            }
        } else {
            if (rng.nextDouble() < 0.7) {
                givenName = FEMALE_NAME_DOUBLE[rng.nextInt(FEMALE_NAME_DOUBLE.length)];
            } else {
                givenName = FEMALE_NAME_SINGLE[rng.nextInt(FEMALE_NAME_SINGLE.length)];
            }
        }

        return surname + givenName;
    }

    private String getEducationLevelByAge(int age, int socialClass, Random rng) {
        String[] educationLevels = {"小学", "初中", "高中", "大学", "研究生"};
        if (age < 12) {
            return "小学";
        } else if (age < 15) {
            return "初中";
        } else if (age < 18) {
            return "高中";
        } else if (age < 22) {
            if (socialClass >= 2 || rng.nextDouble() < 0.6) {
                return "大学";
            } else {
                return "高中";
            }
        } else {
            if (socialClass == 3 && rng.nextDouble() < 0.4) {
                return "研究生";
            } else if (socialClass >= 2 || rng.nextDouble() < 0.5) {
                return "大学";
            } else {
                return "高中";
            }
        }
    }

    private String getIndustryByOccupation(String occupation) {
        if (occupation == null) {
            return null;
        }
        String lowerOccupation = occupation.toLowerCase();
        if (lowerOccupation.contains("程序员") || lowerOccupation.contains("工程师") || lowerOccupation.contains("设计师")) {
            return "科技";
        } else if (lowerOccupation.contains("教师") || lowerOccupation.contains("教授")) {
            return "教育";
        } else if (lowerOccupation.contains("医生") || lowerOccupation.contains("护士")) {
            return "医疗";
        } else if (lowerOccupation.contains("银行") || lowerOccupation.contains("投资") || lowerOccupation.contains("会计")) {
            return "金融";
        } else if (lowerOccupation.contains("农民")) {
            return "农业";
        } else if (lowerOccupation.contains("工人") || lowerOccupation.contains("工程师")) {
            return "制造业";
        } else if (lowerOccupation.contains("服务员") || lowerOccupation.contains("销售")) {
            return "服务业";
        } else if (lowerOccupation.contains("公务员") || lowerOccupation.contains("政府")) {
            return "政府";
        } else {
            return "其他";
        }
    }

    private int getNetworkSizeByAge(int age, int socialClass, Random rng) {
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
        
        if (age < 18) {
            return baseSize / 2;
        } else if (age < 30) {
            return baseSize;
        } else if (age < 50) {
            return baseSize + rng.nextInt(20);
        } else {
            return baseSize - rng.nextInt(10);
        }
    }
}
