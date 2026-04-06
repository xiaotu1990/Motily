package com.motily.region;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@ApplicationScoped
public class RegionService {

    @PostConstruct
    void onStartUp() {
        initRegions();
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
}
