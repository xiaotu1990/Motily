package com.motily.cache;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class StatsCacheService {

    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private static final long CACHE_TTL = TimeUnit.MINUTES.toMillis(5); // 5分钟过期

    // 缓存键常量
    public static final String KEY_HUMAN_STATS = "human_stats";
    public static final String KEY_SOCIAL_CLASS_DISTRIBUTION = "social_class_distribution";
    public static final String KEY_OCCUPATION_DISTRIBUTION = "occupation_distribution";
    public static final String KEY_WEALTH_DISTRIBUTION = "wealth_distribution";
    public static final String KEY_REGION_DISTRIBUTION = "region_distribution";
    public static final String KEY_DERIVED_STATS = "derived_stats";

    // 缓存条目
    private static class CacheEntry {
        final Map<String, Object> value;
        final long timestamp;

        CacheEntry(Map<String, Object> value) {
            this.value = value;
            this.timestamp = System.currentTimeMillis();
        }

        boolean isExpired() {
            return System.currentTimeMillis() - timestamp > CACHE_TTL;
        }
    }

    // 获取缓存
    public Map<String, Object> get(String key) {
        CacheEntry entry = cache.get(key);
        if (entry != null && !entry.isExpired()) {
            return entry.value;
        }
        // 过期或不存在，清除并返回 null
        cache.remove(key);
        return null;
    }

    // 设置缓存
    public void put(String key, Map<String, Object> value) {
        cache.put(key, new CacheEntry(value));
    }

    // 清除指定缓存
    public void remove(String key) {
        cache.remove(key);
    }

    // 清除所有统计相关缓存
    public void clearAllStats() {
        cache.clear();
    }

    // 批量添加数字人后调用，清除相关缓存
    public void clearBatchCreateCache() {
        clearAllStats();
    }
}
