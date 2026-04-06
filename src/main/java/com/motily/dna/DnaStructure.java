package com.motily.dna;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * DNA结构定义类
 * 定义了128个特征的具体含义和分类
 */
public class DnaStructure {
    
    // 特征分类
    public static final String CATEGORY_BASIC = "基础属性";
    public static final String CATEGORY_PERSONALITY = "性格特质";
    public static final String CATEGORY_INTELLIGENCE = "智力能力";
    public static final String CATEGORY_HEALTH = "健康状况";
    public static final String CATEGORY_SOCIAL = "社会属性";
    
    // 特征分类映射
    public static final Map<String, Integer> CATEGORY_START_POSITIONS = new HashMap<>();
    static {
        CATEGORY_START_POSITIONS.put(CATEGORY_BASIC, 0);
        CATEGORY_START_POSITIONS.put(CATEGORY_PERSONALITY, 64);
        CATEGORY_START_POSITIONS.put(CATEGORY_INTELLIGENCE, 112);
        CATEGORY_START_POSITIONS.put(CATEGORY_HEALTH, 160);
        CATEGORY_START_POSITIONS.put(CATEGORY_SOCIAL, 208);
    }
    
    // 特征分类大小
    public static final Map<String, Integer> CATEGORY_SIZES = new HashMap<>();
    static {
        CATEGORY_SIZES.put(CATEGORY_BASIC, 32);
        CATEGORY_SIZES.put(CATEGORY_PERSONALITY, 24);
        CATEGORY_SIZES.put(CATEGORY_INTELLIGENCE, 24);
        CATEGORY_SIZES.put(CATEGORY_HEALTH, 24);
        CATEGORY_SIZES.put(CATEGORY_SOCIAL, 24);
    }
    
    // 特征定义类
    public static class Feature {
        private int id;
        private String name;
        private String description;
        private String category;
        private int startPosition;
        private int endPosition;
        
        public Feature(int id, String name, String description, String category, int startPosition) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.category = category;
            this.startPosition = startPosition;
            this.endPosition = startPosition + 1;
        }
        
        // Getters and setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public int getStartPosition() { return startPosition; }
        public void setStartPosition(int startPosition) { this.startPosition = startPosition; }
        public int getEndPosition() { return endPosition; }
        public void setEndPosition(int endPosition) { this.endPosition = endPosition; }
    }
    
    // 所有特征列表
    public static final List<Feature> ALL_FEATURES = new ArrayList<>();
    
    static {
        // 基础属性 (32个特征)
        int baseStart = CATEGORY_START_POSITIONS.get(CATEGORY_BASIC);
        ALL_FEATURES.add(new Feature(0, "年龄", "数字人的年龄", CATEGORY_BASIC, baseStart + 0 * 2));
        ALL_FEATURES.add(new Feature(1, "性别", "数字人的性别", CATEGORY_BASIC, baseStart + 1 * 2));
        ALL_FEATURES.add(new Feature(2, "身高", "数字人的身高", CATEGORY_BASIC, baseStart + 2 * 2));
        ALL_FEATURES.add(new Feature(3, "体重", "数字人的体重", CATEGORY_BASIC, baseStart + 3 * 2));
        ALL_FEATURES.add(new Feature(4, "外貌吸引力", "数字人的外貌吸引力", CATEGORY_BASIC, baseStart + 4 * 2));
        ALL_FEATURES.add(new Feature(5, "体力", "数字人的体力水平", CATEGORY_BASIC, baseStart + 5 * 2));
        ALL_FEATURES.add(new Feature(6, "耐力", "数字人的耐力水平", CATEGORY_BASIC, baseStart + 6 * 2));
        ALL_FEATURES.add(new Feature(7, "速度", "数字人的速度", CATEGORY_BASIC, baseStart + 7 * 2));
        ALL_FEATURES.add(new Feature(8, "敏捷性", "数字人的敏捷性", CATEGORY_BASIC, baseStart + 8 * 2));
        ALL_FEATURES.add(new Feature(9, "协调性", "数字人的协调性", CATEGORY_BASIC, baseStart + 9 * 2));
        ALL_FEATURES.add(new Feature(10, "平衡感", "数字人的平衡感", CATEGORY_BASIC, baseStart + 10 * 2));
        ALL_FEATURES.add(new Feature(11, "反应速度", "数字人的反应速度", CATEGORY_BASIC, baseStart + 11 * 2));
        ALL_FEATURES.add(new Feature(12, "力量", "数字人的力量", CATEGORY_BASIC, baseStart + 12 * 2));
        ALL_FEATURES.add(new Feature(13, "柔韧性", "数字人的柔韧性", CATEGORY_BASIC, baseStart + 13 * 2));
        ALL_FEATURES.add(new Feature(14, "免疫力", "数字人的免疫力", CATEGORY_BASIC, baseStart + 14 * 2));
        ALL_FEATURES.add(new Feature(15, "恢复能力", "数字人的恢复能力", CATEGORY_BASIC, baseStart + 15 * 2));
        ALL_FEATURES.add(new Feature(16, "寿命潜力", "数字人的寿命潜力", CATEGORY_BASIC, baseStart + 16 * 2));
        ALL_FEATURES.add(new Feature(17, "生育能力", "数字人的生育能力", CATEGORY_BASIC, baseStart + 17 * 2));
        ALL_FEATURES.add(new Feature(18, "遗传健康", "数字人的遗传健康状况", CATEGORY_BASIC, baseStart + 18 * 2));
        ALL_FEATURES.add(new Feature(19, "代谢率", "数字人的代谢率", CATEGORY_BASIC, baseStart + 19 * 2));
        ALL_FEATURES.add(new Feature(20, "睡眠质量", "数字人的睡眠质量", CATEGORY_BASIC, baseStart + 20 * 2));
        ALL_FEATURES.add(new Feature(21, "饮食习惯", "数字人的饮食习惯", CATEGORY_BASIC, baseStart + 21 * 2));
        ALL_FEATURES.add(new Feature(22, "运动习惯", "数字人的运动习惯", CATEGORY_BASIC, baseStart + 22 * 2));
        ALL_FEATURES.add(new Feature(23, "压力水平", "数字人的压力水平", CATEGORY_BASIC, baseStart + 23 * 2));
        ALL_FEATURES.add(new Feature(24, "激素水平", "数字人的激素水平", CATEGORY_BASIC, baseStart + 24 * 2));
        ALL_FEATURES.add(new Feature(25, "感官灵敏度", "数字人的感官灵敏度", CATEGORY_BASIC, baseStart + 25 * 2));
        ALL_FEATURES.add(new Feature(26, "身体协调性", "数字人的身体协调性", CATEGORY_BASIC, baseStart + 26 * 2));
        ALL_FEATURES.add(new Feature(27, "平衡能力", "数字人的平衡能力", CATEGORY_BASIC, baseStart + 27 * 2));
        ALL_FEATURES.add(new Feature(28, "灵活性", "数字人的灵活性", CATEGORY_BASIC, baseStart + 28 * 2));
        ALL_FEATURES.add(new Feature(29, "耐力水平", "数字人的耐力水平", CATEGORY_BASIC, baseStart + 29 * 2));
        ALL_FEATURES.add(new Feature(30, "爆发力", "数字人的爆发力", CATEGORY_BASIC, baseStart + 30 * 2));
        ALL_FEATURES.add(new Feature(31, "整体健康", "数字人的整体健康状况", CATEGORY_BASIC, baseStart + 31 * 2));
        
        // 性格特质 (24个特征)
        int personalityStart = CATEGORY_START_POSITIONS.get(CATEGORY_PERSONALITY);
        ALL_FEATURES.add(new Feature(32, "外向性", "数字人的外向程度", CATEGORY_PERSONALITY, personalityStart + 0 * 2));
        ALL_FEATURES.add(new Feature(33, "神经质", "数字人的神经质程度", CATEGORY_PERSONALITY, personalityStart + 1 * 2));
        ALL_FEATURES.add(new Feature(34, "开放性", "数字人的开放性程度", CATEGORY_PERSONALITY, personalityStart + 2 * 2));
        ALL_FEATURES.add(new Feature(35, "宜人性", "数字人的宜人性程度", CATEGORY_PERSONALITY, personalityStart + 3 * 2));
        ALL_FEATURES.add(new Feature(36, "尽责性", "数字人的尽责性程度", CATEGORY_PERSONALITY, personalityStart + 4 * 2));
        ALL_FEATURES.add(new Feature(37, "冒险精神", "数字人的冒险精神", CATEGORY_PERSONALITY, personalityStart + 5 * 2));
        ALL_FEATURES.add(new Feature(38, "好奇心", "数字人的好奇心", CATEGORY_PERSONALITY, personalityStart + 6 * 2));
        ALL_FEATURES.add(new Feature(39, "创造力", "数字人的创造力", CATEGORY_PERSONALITY, personalityStart + 7 * 2));
        ALL_FEATURES.add(new Feature(40, "自信心", "数字人的自信心", CATEGORY_PERSONALITY, personalityStart + 8 * 2));
        ALL_FEATURES.add(new Feature(41, "同理心", "数字人的同理心", CATEGORY_PERSONALITY, personalityStart + 9 * 2));
        ALL_FEATURES.add(new Feature(42, "情绪稳定性", "数字人的情绪稳定性", CATEGORY_PERSONALITY, personalityStart + 10 * 2));
        ALL_FEATURES.add(new Feature(43, "耐心", "数字人的耐心程度", CATEGORY_PERSONALITY, personalityStart + 11 * 2));
        ALL_FEATURES.add(new Feature(44, "毅力", "数字人的毅力", CATEGORY_PERSONALITY, personalityStart + 12 * 2));
        ALL_FEATURES.add(new Feature(45, "决断力", "数字人的决断力", CATEGORY_PERSONALITY, personalityStart + 13 * 2));
        ALL_FEATURES.add(new Feature(46, "适应性", "数字人的适应性", CATEGORY_PERSONALITY, personalityStart + 14 * 2));
        ALL_FEATURES.add(new Feature(47, "社交能力", "数字人的社交能力", CATEGORY_PERSONALITY, personalityStart + 15 * 2));
        ALL_FEATURES.add(new Feature(48, "领导能力", "数字人的领导能力", CATEGORY_PERSONALITY, personalityStart + 16 * 2));
        ALL_FEATURES.add(new Feature(49, "团队合作", "数字人的团队合作能力", CATEGORY_PERSONALITY, personalityStart + 17 * 2));
        ALL_FEATURES.add(new Feature(50, "沟通能力", "数字人的沟通能力", CATEGORY_PERSONALITY, personalityStart + 18 * 2));
        ALL_FEATURES.add(new Feature(51, "倾听能力", "数字人的倾听能力", CATEGORY_PERSONALITY, personalityStart + 19 * 2));
        ALL_FEATURES.add(new Feature(52, "表达能力", "数字人的表达能力", CATEGORY_PERSONALITY, personalityStart + 20 * 2));
        ALL_FEATURES.add(new Feature(53, "说服力", "数字人的说服力", CATEGORY_PERSONALITY, personalityStart + 21 * 2));
        ALL_FEATURES.add(new Feature(54, "幽默感", "数字人的幽默感", CATEGORY_PERSONALITY, personalityStart + 22 * 2));
        ALL_FEATURES.add(new Feature(55, "情商", "数字人的情商", CATEGORY_PERSONALITY, personalityStart + 23 * 2));
        
        // 智力能力 (24个特征)
        int intelligenceStart = CATEGORY_START_POSITIONS.get(CATEGORY_INTELLIGENCE);
        ALL_FEATURES.add(new Feature(56, "智商", "数字人的智商", CATEGORY_INTELLIGENCE, intelligenceStart + 0 * 2));
        ALL_FEATURES.add(new Feature(57, "记忆力", "数字人的记忆力", CATEGORY_INTELLIGENCE, intelligenceStart + 1 * 2));
        ALL_FEATURES.add(new Feature(58, "注意力", "数字人的注意力", CATEGORY_INTELLIGENCE, intelligenceStart + 2 * 2));
        ALL_FEATURES.add(new Feature(59, "逻辑思维", "数字人的逻辑思维能力", CATEGORY_INTELLIGENCE, intelligenceStart + 3 * 2));
        ALL_FEATURES.add(new Feature(60, "抽象思维", "数字人的抽象思维能力", CATEGORY_INTELLIGENCE, intelligenceStart + 4 * 2));
        ALL_FEATURES.add(new Feature(61, "创造性思维", "数字人的创造性思维能力", CATEGORY_INTELLIGENCE, intelligenceStart + 5 * 2));
        ALL_FEATURES.add(new Feature(62, "问题解决能力", "数字人的问题解决能力", CATEGORY_INTELLIGENCE, intelligenceStart + 6 * 2));
        ALL_FEATURES.add(new Feature(63, "学习能力", "数字人的学习能力", CATEGORY_INTELLIGENCE, intelligenceStart + 7 * 2));
        ALL_FEATURES.add(new Feature(64, "语言能力", "数字人的语言能力", CATEGORY_INTELLIGENCE, intelligenceStart + 8 * 2));
        ALL_FEATURES.add(new Feature(65, "数学能力", "数字人的数学能力", CATEGORY_INTELLIGENCE, intelligenceStart + 9 * 2));
        ALL_FEATURES.add(new Feature(66, "空间能力", "数字人的空间能力", CATEGORY_INTELLIGENCE, intelligenceStart + 10 * 2));
        ALL_FEATURES.add(new Feature(67, "音乐能力", "数字人的音乐能力", CATEGORY_INTELLIGENCE, intelligenceStart + 11 * 2));
        ALL_FEATURES.add(new Feature(68, "艺术能力", "数字人的艺术能力", CATEGORY_INTELLIGENCE, intelligenceStart + 12 * 2));
        ALL_FEATURES.add(new Feature(69, "运动能力", "数字人的运动能力", CATEGORY_INTELLIGENCE, intelligenceStart + 13 * 2));
        ALL_FEATURES.add(new Feature(70, "实践能力", "数字人的实践能力", CATEGORY_INTELLIGENCE, intelligenceStart + 14 * 2));
        ALL_FEATURES.add(new Feature(71, "分析能力", "数字人的分析能力", CATEGORY_INTELLIGENCE, intelligenceStart + 15 * 2));
        ALL_FEATURES.add(new Feature(72, "综合能力", "数字人的综合能力", CATEGORY_INTELLIGENCE, intelligenceStart + 16 * 2));
        ALL_FEATURES.add(new Feature(73, "推理能力", "数字人的推理能力", CATEGORY_INTELLIGENCE, intelligenceStart + 17 * 2));
        ALL_FEATURES.add(new Feature(74, "判断能力", "数字人的判断能力", CATEGORY_INTELLIGENCE, intelligenceStart + 18 * 2));
        ALL_FEATURES.add(new Feature(75, "决策能力", "数字人的决策能力", CATEGORY_INTELLIGENCE, intelligenceStart + 19 * 2));
        ALL_FEATURES.add(new Feature(76, "规划能力", "数字人的规划能力", CATEGORY_INTELLIGENCE, intelligenceStart + 20 * 2));
        ALL_FEATURES.add(new Feature(77, "执行能力", "数字人的执行能力", CATEGORY_INTELLIGENCE, intelligenceStart + 21 * 2));
        ALL_FEATURES.add(new Feature(78, "创新能力", "数字人的创新能力", CATEGORY_INTELLIGENCE, intelligenceStart + 22 * 2));
        ALL_FEATURES.add(new Feature(79, "整体智力", "数字人的整体智力水平", CATEGORY_INTELLIGENCE, intelligenceStart + 23 * 2));
        
        // 健康状况 (24个特征)
        int healthStart = CATEGORY_START_POSITIONS.get(CATEGORY_HEALTH);
        ALL_FEATURES.add(new Feature(80, "心脏健康", "数字人的心脏健康状况", CATEGORY_HEALTH, healthStart + 0 * 2));
        ALL_FEATURES.add(new Feature(81, "肺部健康", "数字人的肺部健康状况", CATEGORY_HEALTH, healthStart + 1 * 2));
        ALL_FEATURES.add(new Feature(82, "肝脏健康", "数字人的肝脏健康状况", CATEGORY_HEALTH, healthStart + 2 * 2));
        ALL_FEATURES.add(new Feature(83, "肾脏健康", "数字人的肾脏健康状况", CATEGORY_HEALTH, healthStart + 3 * 2));
        ALL_FEATURES.add(new Feature(84, "消化系统健康", "数字人的消化系统健康状况", CATEGORY_HEALTH, healthStart + 4 * 2));
        ALL_FEATURES.add(new Feature(85, "免疫系统健康", "数字人的免疫系统健康状况", CATEGORY_HEALTH, healthStart + 5 * 2));
        ALL_FEATURES.add(new Feature(86, "神经系统健康", "数字人的神经系统健康状况", CATEGORY_HEALTH, healthStart + 6 * 2));
        ALL_FEATURES.add(new Feature(87, "内分泌系统健康", "数字人的内分泌系统健康状况", CATEGORY_HEALTH, healthStart + 7 * 2));
        ALL_FEATURES.add(new Feature(88, "骨骼健康", "数字人的骨骼健康状况", CATEGORY_HEALTH, healthStart + 8 * 2));
        ALL_FEATURES.add(new Feature(89, "肌肉健康", "数字人的肌肉健康状况", CATEGORY_HEALTH, healthStart + 9 * 2));
        ALL_FEATURES.add(new Feature(90, "皮肤健康", "数字人的皮肤健康状况", CATEGORY_HEALTH, healthStart + 10 * 2));
        ALL_FEATURES.add(new Feature(91, "视力健康", "数字人的视力健康状况", CATEGORY_HEALTH, healthStart + 11 * 2));
        ALL_FEATURES.add(new Feature(92, "听力健康", "数字人的听力健康状况", CATEGORY_HEALTH, healthStart + 12 * 2));
        ALL_FEATURES.add(new Feature(93, "口腔健康", "数字人的口腔健康状况", CATEGORY_HEALTH, healthStart + 13 * 2));
        ALL_FEATURES.add(new Feature(94, "心理健康", "数字人的心理健康状况", CATEGORY_HEALTH, healthStart + 14 * 2));
        ALL_FEATURES.add(new Feature(95, "精神健康", "数字人的精神健康状况", CATEGORY_HEALTH, healthStart + 15 * 2));
        ALL_FEATURES.add(new Feature(96, "睡眠健康", "数字人的睡眠健康状况", CATEGORY_HEALTH, healthStart + 16 * 2));
        ALL_FEATURES.add(new Feature(97, "营养健康", "数字人的营养健康状况", CATEGORY_HEALTH, healthStart + 17 * 2));
        ALL_FEATURES.add(new Feature(98, "运动健康", "数字人的运动健康状况", CATEGORY_HEALTH, healthStart + 18 * 2));
        ALL_FEATURES.add(new Feature(99, "压力管理", "数字人的压力管理能力", CATEGORY_HEALTH, healthStart + 19 * 2));
        ALL_FEATURES.add(new Feature(100, "疾病抵抗力", "数字人的疾病抵抗力", CATEGORY_HEALTH, healthStart + 20 * 2));
        ALL_FEATURES.add(new Feature(101, "康复能力", "数字人的康复能力", CATEGORY_HEALTH, healthStart + 21 * 2));
        ALL_FEATURES.add(new Feature(102, "整体健康", "数字人的整体健康状况", CATEGORY_HEALTH, healthStart + 22 * 2));
        ALL_FEATURES.add(new Feature(103, "健康意识", "数字人的健康意识", CATEGORY_HEALTH, healthStart + 23 * 2));
        
        // 社会属性 (24个特征)
        int socialStart = CATEGORY_START_POSITIONS.get(CATEGORY_SOCIAL);
        ALL_FEATURES.add(new Feature(104, "社会地位", "数字人的社会地位", CATEGORY_SOCIAL, socialStart + 0 * 2));
        ALL_FEATURES.add(new Feature(105, "经济状况", "数字人的经济状况", CATEGORY_SOCIAL, socialStart + 1 * 2));
        ALL_FEATURES.add(new Feature(106, "教育水平", "数字人的教育水平", CATEGORY_SOCIAL, socialStart + 2 * 2));
        ALL_FEATURES.add(new Feature(107, "职业地位", "数字人的职业地位", CATEGORY_SOCIAL, socialStart + 3 * 2));
        ALL_FEATURES.add(new Feature(108, "社交网络", "数字人的社交网络", CATEGORY_SOCIAL, socialStart + 4 * 2));
        ALL_FEATURES.add(new Feature(109, "社会影响力", "数字人的社会影响力", CATEGORY_SOCIAL, socialStart + 5 * 2));
        ALL_FEATURES.add(new Feature(110, "领导力", "数字人的领导力", CATEGORY_SOCIAL, socialStart + 6 * 2));
        ALL_FEATURES.add(new Feature(111, "团队合作", "数字人的团队合作能力", CATEGORY_SOCIAL, socialStart + 7 * 2));
        ALL_FEATURES.add(new Feature(112, "沟通能力", "数字人的沟通能力", CATEGORY_SOCIAL, socialStart + 8 * 2));
        ALL_FEATURES.add(new Feature(113, "人际关系", "数字人的人际关系", CATEGORY_SOCIAL, socialStart + 9 * 2));
        ALL_FEATURES.add(new Feature(114, "家庭关系", "数字人的家庭关系", CATEGORY_SOCIAL, socialStart + 10 * 2));
        ALL_FEATURES.add(new Feature(115, "社会适应能力", "数字人的社会适应能力", CATEGORY_SOCIAL, socialStart + 11 * 2));
        ALL_FEATURES.add(new Feature(116, "文化认同", "数字人的文化认同", CATEGORY_SOCIAL, socialStart + 12 * 2));
        ALL_FEATURES.add(new Feature(117, "价值观", "数字人的价值观", CATEGORY_SOCIAL, socialStart + 13 * 2));
        ALL_FEATURES.add(new Feature(118, "信仰", "数字人的信仰", CATEGORY_SOCIAL, socialStart + 14 * 2));
        ALL_FEATURES.add(new Feature(119, "道德观念", "数字人的道德观念", CATEGORY_SOCIAL, socialStart + 15 * 2));
        ALL_FEATURES.add(new Feature(120, "法律意识", "数字人的法律意识", CATEGORY_SOCIAL, socialStart + 16 * 2));
        ALL_FEATURES.add(new Feature(121, "公民意识", "数字人的公民意识", CATEGORY_SOCIAL, socialStart + 17 * 2));
        ALL_FEATURES.add(new Feature(122, "环保意识", "数字人的环保意识", CATEGORY_SOCIAL, socialStart + 18 * 2));
        ALL_FEATURES.add(new Feature(123, "社会责任", "数字人的社会责任", CATEGORY_SOCIAL, socialStart + 19 * 2));
        ALL_FEATURES.add(new Feature(124, "社会参与", "数字人的社会参与度", CATEGORY_SOCIAL, socialStart + 20 * 2));
        ALL_FEATURES.add(new Feature(125, "社会贡献", "数字人的社会贡献", CATEGORY_SOCIAL, socialStart + 21 * 2));
        ALL_FEATURES.add(new Feature(126, "社会评价", "数字人的社会评价", CATEGORY_SOCIAL, socialStart + 22 * 2));
        ALL_FEATURES.add(new Feature(127, "整体社会属性", "数字人的整体社会属性", CATEGORY_SOCIAL, socialStart + 23 * 2));
    }
    
    // 根据特征ID获取特征
    public static Feature getFeatureById(int id) {
        for (Feature feature : ALL_FEATURES) {
            if (feature.getId() == id) {
                return feature;
            }
        }
        return null;
    }
    
    // 根据位置获取特征
    public static Feature getFeatureByPosition(int position) {
        for (Feature feature : ALL_FEATURES) {
            if (feature.getStartPosition() <= position && position <= feature.getEndPosition()) {
                return feature;
            }
        }
        return null;
    }
    
    // 获取指定分类的所有特征
    public static List<Feature> getFeaturesByCategory(String category) {
        List<Feature> features = new ArrayList<>();
        for (Feature feature : ALL_FEATURES) {
            if (feature.getCategory().equals(category)) {
                features.add(feature);
            }
        }
        return features;
    }
    
    // 获取所有特征分类
    public static List<String> getAllCategories() {
        return new ArrayList<>(CATEGORY_START_POSITIONS.keySet());
    }
    
    // 验证特征ID是否有效
    public static boolean isValidFeatureId(int id) {
        return id >= 0 && id < ALL_FEATURES.size();
    }
    
    // 验证位置是否有效
    public static boolean isValidPosition(int position) {
        return position >= 0 && position < 256;
    }
}