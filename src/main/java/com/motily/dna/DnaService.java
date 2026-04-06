package com.motily.dna;

import java.util.*;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * DNA服务类
 * 提供DNA验证、生成和分析功能
 */
@ApplicationScoped
public class DnaService {
    
    /**
     * 验证DNA字符串的有效性
     * @param dnaString DNA字符串
     * @return 验证结果，包含是否有效以及错误信息
     */
    public static ValidationResult validateDna(String dnaString) {
        ValidationResult result = new ValidationResult();
        
        // 检查字符串是否为空
        if (dnaString == null || dnaString.isEmpty()) {
            result.setValid(false);
            result.setErrorMessage("DNA字符串不能为空");
            return result;
        }
        
        try {
            // 尝试解码
            int[] featureValues = DnaEncoderDecoder.decode(dnaString);
            
            // 检查特征值数量
            if (featureValues.length != 128) {
                result.setValid(false);
                result.setErrorMessage("DNA必须包含128个特征值");
                return result;
            }
            
            // 检查特征值范围
            for (int i = 0; i < featureValues.length; i++) {
                int value = featureValues[i];
                if (value < 0 || value > 3) {
                    result.setValid(false);
                    result.setErrorMessage("特征值必须在0-3之间，第" + (i + 1) + "个特征值无效: " + value);
                    return result;
                }
            }
            
            // 验证通过
            result.setValid(true);
            result.setErrorMessage("DNA验证通过");
            return result;
        } catch (Exception e) {
            result.setValid(false);
            result.setErrorMessage("DNA格式无效: " + e.getMessage());
            return result;
        }
    }
    
    /**
     * 生成随机DNA
     * @return 随机生成的DNA字符串
     */
    public static String generateRandomDna() {
        return DnaEncoderDecoder.generateRandomDna();
    }
    
    /**
     * 根据特征值生成DNA
     * @param featureValues 特征值数组
     * @return 生成的DNA字符串
     */
    public static String generateDna(int[] featureValues) {
        return DnaEncoderDecoder.encode(featureValues);
    }
    
    /**
     * 生成具有特定特征的DNA
     * @param categoryPreferences 分类偏好，指定每个分类的平均强度
     * @return 生成的DNA字符串
     */
    public static String generateDnaWithPreferences(Map<String, Integer> categoryPreferences) {
        int[] featureValues = new int[128];
        
        // 遍历所有特征
        for (int i = 0; i < 128; i++) {
            DnaStructure.Feature feature = DnaStructure.getFeatureById(i);
            if (feature != null) {
                String category = feature.getCategory();
                int baseValue = 2; // 默认值
                
                // 如果指定了该分类的偏好
                if (categoryPreferences.containsKey(category)) {
                    baseValue = categoryPreferences.get(category);
                }
                
                // 添加一些随机变化
                int randomVariation = (int) (Math.random() * 3) - 1; // -1, 0, 或 1
                int finalValue = baseValue + randomVariation;
                
                // 确保值在0-3之间
                finalValue = Math.max(0, Math.min(3, finalValue));
                featureValues[i] = finalValue;
            } else {
                // 如果特征不存在，使用默认值
                featureValues[i] = 2;
            }
        }
        
        return DnaEncoderDecoder.encode(featureValues);
    }
    
    /**
     * 分析DNA的特征分布
     * @param dnaString DNA字符串
     * @return 特征分布分析结果
     */
    public static DnaAnalysisResult analyzeDna(String dnaString) {
        DnaAnalysisResult result = new DnaAnalysisResult();
        
        try {
            int[] featureValues = DnaEncoderDecoder.decode(dnaString);
            
            // 计算总体统计
            int totalSum = 0;
            for (int value : featureValues) {
                totalSum += value;
            }
            double average = (double) totalSum / featureValues.length;
            result.setOverallAverage(average);
            
            // 计算每个分类的统计
            Map<String, CategoryStats> categoryStatsMap = new HashMap<>();
            
            for (int i = 0; i < featureValues.length; i++) {
                DnaStructure.Feature feature = DnaStructure.getFeatureById(i);
                if (feature != null) {
                    String category = feature.getCategory();
                    int value = featureValues[i];
                    
                    CategoryStats stats = categoryStatsMap.getOrDefault(category, new CategoryStats());
                    stats.addValue(value);
                    categoryStatsMap.put(category, stats);
                }
            }
            
            result.setCategoryStats(categoryStatsMap);
            
            // 计算特征强度分布
            Map<Integer, Integer> intensityDistribution = new HashMap<>();
            for (int value : featureValues) {
                intensityDistribution.put(value, intensityDistribution.getOrDefault(value, 0) + 1);
            }
            result.setIntensityDistribution(intensityDistribution);
            
            // 找出最高和最低的特征
            List<FeatureValue> featureValuesList = new ArrayList<>();
            for (int i = 0; i < featureValues.length; i++) {
                DnaStructure.Feature feature = DnaStructure.getFeatureById(i);
                if (feature != null) {
                    featureValuesList.add(new FeatureValue(feature, featureValues[i]));
                }
            }
            
            // 按值排序
            featureValuesList.sort(Comparator.comparingInt(FeatureValue::getValue).reversed());
            
            // 取前10个最高的特征
            List<FeatureValue> topFeatures = featureValuesList.subList(0, Math.min(10, featureValuesList.size()));
            result.setTopFeatures(topFeatures);
            
            // 取后10个最低的特征
            List<FeatureValue> bottomFeatures = featureValuesList.subList(Math.max(0, featureValuesList.size() - 10), featureValuesList.size());
            Collections.reverse(bottomFeatures);
            result.setBottomFeatures(bottomFeatures);
            
        } catch (Exception e) {
            result.setError(e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 获取DNA的特征值
     * @param dnaString DNA字符串
     * @return 特征值映射，键为特征ID，值为特征强度
     */
    public static Map<Integer, Integer> getFeatureValues(String dnaString) {
        Map<Integer, Integer> featureValuesMap = new HashMap<>();
        
        try {
            int[] featureValues = DnaEncoderDecoder.decode(dnaString);
            for (int i = 0; i < featureValues.length; i++) {
                featureValuesMap.put(i, featureValues[i]);
            }
        } catch (Exception e) {
            // 忽略错误，返回空映射
        }
        
        return featureValuesMap;
    }
    
    /**
     * 更新DNA的特征值
     * @param dnaString DNA字符串
     * @param featureValuesMap 要更新的特征值映射
     * @return 更新后的DNA字符串
     */
    public static String updateFeatureValues(String dnaString, Map<Integer, Integer> featureValuesMap) {
        try {
            int[] featureValues = DnaEncoderDecoder.decode(dnaString);
            
            // 更新特征值
            for (Map.Entry<Integer, Integer> entry : featureValuesMap.entrySet()) {
                int featureId = entry.getKey();
                int value = entry.getValue();
                
                if (DnaStructure.isValidFeatureId(featureId) && value >= 0 && value <= 3) {
                    featureValues[featureId] = value;
                }
            }
            
            return DnaEncoderDecoder.encode(featureValues);
        } catch (Exception e) {
            // 如果出错，返回原始DNA
            return dnaString;
        }
    }
    
    // 内部类：验证结果
    public static class ValidationResult {
        private boolean valid;
        private String errorMessage;
        
        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    }
    
    // 内部类：分类统计
    public static class CategoryStats {
        private int count;
        private int sum;
        private double average;
        
        public void addValue(int value) {
            count++;
            sum += value;
            average = (double) sum / count;
        }
        
        public int getCount() { return count; }
        public int getSum() { return sum; }
        public double getAverage() { return average; }
    }
    
    // 内部类：特征值
    public static class FeatureValue {
        private DnaStructure.Feature feature;
        private int value;
        
        public FeatureValue(DnaStructure.Feature feature, int value) {
            this.feature = feature;
            this.value = value;
        }
        
        public DnaStructure.Feature getFeature() { return feature; }
        public int getValue() { return value; }
    }
    
    // 内部类：DNA分析结果
    public static class DnaAnalysisResult {
        private double overallAverage;
        private Map<String, CategoryStats> categoryStats;
        private Map<Integer, Integer> intensityDistribution;
        private List<FeatureValue> topFeatures;
        private List<FeatureValue> bottomFeatures;
        private String error;
        
        public double getOverallAverage() { return overallAverage; }
        public void setOverallAverage(double overallAverage) { this.overallAverage = overallAverage; }
        public Map<String, CategoryStats> getCategoryStats() { return categoryStats; }
        public void setCategoryStats(Map<String, CategoryStats> categoryStats) { this.categoryStats = categoryStats; }
        public Map<Integer, Integer> getIntensityDistribution() { return intensityDistribution; }
        public void setIntensityDistribution(Map<Integer, Integer> intensityDistribution) { this.intensityDistribution = intensityDistribution; }
        public List<FeatureValue> getTopFeatures() { return topFeatures; }
        public void setTopFeatures(List<FeatureValue> topFeatures) { this.topFeatures = topFeatures; }
        public List<FeatureValue> getBottomFeatures() { return bottomFeatures; }
        public void setBottomFeatures(List<FeatureValue> bottomFeatures) { this.bottomFeatures = bottomFeatures; }
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
    }
}