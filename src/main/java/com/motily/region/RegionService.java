package com.motily.region;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@ApplicationScoped
public class RegionService {

    private double[] regionDevelopmentLevels;
    private double[] regionResourceEndowments;
    private double[] regionEconomicPolicies;

    @PostConstruct
    void onStartUp() {
        initRegions();
        initRegionDevelopmentLevels();
        initRegionResourceEndowments();
        initRegionEconomicPolicies();
    }

    @Transactional
    public void initRegions() {
        long count = Region.count();
        if (count > 0) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        createRegion(1L, "110000", "北京", 0.018, 1, now);
        createRegion(2L, "120000", "天津", 0.010, 1, now);
        createRegion(3L, "130000", "河北", 0.055, 2, now);
        createRegion(4L, "140000", "山西", 0.028, 2, now);
        createRegion(5L, "150000", "内蒙古", 0.022, 3, now);
        createRegion(6L, "210000", "辽宁", 0.032, 2, now);
        createRegion(7L, "220000", "吉林", 0.020, 2, now);
        createRegion(8L, "230000", "黑龙江", 0.028, 2, now);
        createRegion(9L, "310000", "上海", 0.024, 1, now);
        createRegion(10L, "320000", "江苏", 0.060, 2, now);
        createRegion(11L, "330000", "浙江", 0.048, 2, now);
        createRegion(12L, "340000", "安徽", 0.042, 2, now);
        createRegion(13L, "350000", "福建", 0.032, 2, now);
        createRegion(14L, "360000", "江西", 0.030, 2, now);
        createRegion(15L, "370000", "山东", 0.075, 2, now);
        createRegion(16L, "410000", "河南", 0.072, 2, now);
        createRegion(17L, "420000", "湖北", 0.042, 2, now);
        createRegion(18L, "430000", "湖南", 0.040, 2, now);
        createRegion(19L, "440000", "广东", 0.092, 2, now);
        createRegion(20L, "450000", "广西", 0.036, 3, now);
        createRegion(21L, "460000", "海南", 0.012, 2, now);
        createRegion(22L, "500000", "重庆", 0.029, 1, now);
        createRegion(23L, "510000", "四川", 0.056, 2, now);
        createRegion(24L, "520000", "贵州", 0.027, 2, now);
        createRegion(25L, "530000", "云南", 0.032, 2, now);
        createRegion(26L, "540000", "西藏", 0.004, 3, now);
        createRegion(27L, "610000", "陕西", 0.032, 2, now);
        createRegion(28L, "620000", "甘肃", 0.020, 2, now);
        createRegion(29L, "630000", "青海", 0.006, 3, now);
        createRegion(30L, "640000", "宁夏", 0.007, 3, now);
        createRegion(31L, "650000", "新疆", 0.019, 3, now);
        createRegion(32L, "810000", "港澳台", 0.018, 4, now);
    }

    private void createRegion(Long id, String code, String name, double populationWeight, int level, LocalDateTime now) {
        Region region = new Region();
        region.id = id;
        region.code = code;
        region.name = name;
        region.populationWeight = populationWeight;
        region.level = level;
        region.createdAt = now;
        region.updatedAt = now;
        region.persist();
    }

    private void initRegionDevelopmentLevels() {
        List<Region> regions = Region.findAll().list();
        regionDevelopmentLevels = new double[regions.size() + 1]; // 索引从1开始
        
        for (Region region : regions) {
            switch (region.level) {
                case 1:
                    regionDevelopmentLevels[region.id.intValue()] = 1.0; // 发达地区
                    break;
                case 2:
                    regionDevelopmentLevels[region.id.intValue()] = 0.7; // 中等地区
                    break;
                case 3:
                    regionDevelopmentLevels[region.id.intValue()] = 0.4; // 欠发达地区
                    break;
                case 4:
                    regionDevelopmentLevels[region.id.intValue()] = 1.2; // 特别发达地区
                    break;
                default:
                    regionDevelopmentLevels[region.id.intValue()] = 0.7;
            }
        }
    }

    private void initRegionResourceEndowments() {
        List<Region> regions = Region.findAll().list();
        regionResourceEndowments = new double[regions.size() + 1]; // 索引从1开始
        
        for (Region region : regions) {
            regionResourceEndowments[region.id.intValue()] = 0.5 + Math.random() * 0.5; // 0.5-1.0
        }
    }

    private void initRegionEconomicPolicies() {
        List<Region> regions = Region.findAll().list();
        regionEconomicPolicies = new double[regions.size() + 1]; // 索引从1开始
        
        for (Region region : regions) {
            regionEconomicPolicies[region.id.intValue()] = 0.8 + Math.random() * 0.4; // 0.8-1.2
        }
    }

    public int assignRandomRegion(Random rng) {
        List<Region> regions = Region.findAll().list();
        if (regions.isEmpty()) {
            return 1;
        }

        double randomValue = rng.nextDouble();
        double cumulativeProbability = 0.0;

        for (Region region : regions) {
            cumulativeProbability += region.populationWeight;
            if (randomValue <= cumulativeProbability) {
                return region.id.intValue();
            }
        }

        return regions.get(regions.size() - 1).id.intValue();
    }

    public Region getRegionById(int id) {
        return Region.findById((long) id);
    }

    public List<Region> getAllRegions() {
        return Region.findAll().list();
    }

    public String getRegionName(int id) {
        Region region = Region.findById((long) id);
        return region != null ? region.name : null;
    }

    public double getDevelopmentLevel(int regionId) {
        if (regionDevelopmentLevels == null || regionId >= regionDevelopmentLevels.length) {
            return 0.7; // 默认中等发展水平
        }
        return regionDevelopmentLevels[regionId];
    }

    public double getResourceEndowment(int regionId) {
        if (regionResourceEndowments == null || regionId >= regionResourceEndowments.length) {
            return 0.7; // 默认中等资源禀赋
        }
        return regionResourceEndowments[regionId];
    }

    public double getEconomicPolicy(int regionId) {
        if (regionEconomicPolicies == null || regionId >= regionEconomicPolicies.length) {
            return 1.0; // 默认中性政策
        }
        return regionEconomicPolicies[regionId];
    }

    public double calculateRegionalEconomicBonus(int regionId) {
        double developmentBonus = getDevelopmentLevel(regionId) * 0.2;
        double resourceBonus = getResourceEndowment(regionId) * 0.1;
        double policyBonus = (getEconomicPolicy(regionId) - 1.0) * 0.1;
        
        return developmentBonus + resourceBonus + policyBonus;
    }

    public int getPromisingRegion(Random rng) {
        List<Region> regions = Region.findAll().list();
        if (regions.isEmpty()) {
            return 1;
        }
        
        double maxScore = -1;
        int bestRegionId = 1;
        
        for (Region region : regions) {
            double score = calculateRegionalEconomicBonus(region.id.intValue());
            if (score > maxScore) {
                maxScore = score;
                bestRegionId = region.id.intValue();
            }
        }
        
        return bestRegionId;
    }

    public boolean shouldMigrate(int fromRegionId, int toRegionId, Random rng) {
        double fromBonus = calculateRegionalEconomicBonus(fromRegionId);
        double toBonus = calculateRegionalEconomicBonus(toRegionId);
        
        double migrationThreshold = 0.1;
        if (toBonus - fromBonus > migrationThreshold) {
            double migrationProbability = (toBonus - fromBonus) * 0.5;
            return rng.nextDouble() < migrationProbability;
        }
        
        return false;
    }

    public void updateRegionalFactors(Random rng) {
        List<Region> regions = Region.findAll().list();
        for (Region region : regions) {
            int regionId = region.id.intValue();
            // 随机更新地区发展水平
            regionDevelopmentLevels[regionId] = Math.max(0.3, Math.min(1.3, 
                regionDevelopmentLevels[regionId] + (rng.nextDouble() * 0.02 - 0.01)));
            
            // 随机更新地区资源禀赋
            regionResourceEndowments[regionId] = Math.max(0.3, Math.min(1.2, 
                regionResourceEndowments[regionId] + (rng.nextDouble() * 0.02 - 0.01)));
            
            // 随机更新地区经济政策
            regionEconomicPolicies[regionId] = Math.max(0.7, Math.min(1.3, 
                regionEconomicPolicies[regionId] + (rng.nextDouble() * 0.02 - 0.01)));
        }
    }
}
