package com.motily.occupation;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@ApplicationScoped
public class OccupationRegistry {

    public record Occupation(
            String name,
            int socialClass,
            double minIncome,
            double maxIncome,
            String skillRequirement,
            int weight
    ) {}

    private final List<Occupation> allOccupations = new ArrayList<>();
    private final List<Occupation> lowerClassOccupations = new ArrayList<>();
    private final List<Occupation> middleClassOccupations = new ArrayList<>();
    private final List<Occupation> upperClassOccupations = new ArrayList<>();

    @PostConstruct
    void init() {
        registerLowerClassOccupations();
        registerMiddleClassOccupations();
        registerUpperClassOccupations();
        allOccupations.addAll(lowerClassOccupations);
        allOccupations.addAll(middleClassOccupations);
        allOccupations.addAll(upperClassOccupations);
    }

    @Deprecated
    public Occupation getRandomOccupation(int socialClass, Random rng) {
        return switch (socialClass) {
            case 1 -> lowerClassOccupations.get(rng.nextInt(lowerClassOccupations.size()));
            case 2 -> middleClassOccupations.get(rng.nextInt(middleClassOccupations.size()));
            case 3 -> upperClassOccupations.get(rng.nextInt(upperClassOccupations.size()));
            default -> throw new IllegalArgumentException("Invalid social class: " + socialClass + ". Must be 1, 2, or 3.");
        };
    }

    public Occupation getWeightedRandomOccupation(int socialClass, Random rng) {
        List<Occupation> pool = getOccupationPool(socialClass);
        int totalWeight = pool.stream().mapToInt(Occupation::weight).sum();
        double randomValue = rng.nextDouble() * totalWeight;
        double cumulative = 0.0;
        for (Occupation occ : pool) {
            cumulative += occ.weight();
            if (randomValue <= cumulative) return occ;
        }
        return pool.get(pool.size() - 1);
    }

    public List<Occupation> getOccupationPool(int socialClass) {
        return switch (socialClass) {
            case 1 -> Collections.unmodifiableList(lowerClassOccupations);
            case 2 -> Collections.unmodifiableList(middleClassOccupations);
            case 3 -> Collections.unmodifiableList(upperClassOccupations);
            default -> throw new IllegalArgumentException("Invalid social class: " + socialClass + ". Must be 1, 2, or 3.");
        };
    }

    public List<Occupation> getAllOccupations() {
        return Collections.unmodifiableList(allOccupations);
    }

    private void addLower(String name, double minIncome, double maxIncome, String skillRequirement, int weight) {
        lowerClassOccupations.add(new Occupation(name, 1, minIncome, maxIncome, skillRequirement, weight));
    }

    private void addMiddle(String name, double minIncome, double maxIncome, String skillRequirement, int weight) {
        middleClassOccupations.add(new Occupation(name, 2, minIncome, maxIncome, skillRequirement, weight));
    }

    private void addUpper(String name, double minIncome, double maxIncome, String skillRequirement, int weight) {
        upperClassOccupations.add(new Occupation(name, 3, minIncome, maxIncome, skillRequirement, weight));
    }

    private void registerLowerClassOccupations() {
        addLower("外卖骑手", 38000, 75000, "电动车驾驶与时间管理", 15);
        addLower("流水线操作员/工厂普工", 28000, 48000, "设备操作规程掌握", 12);
        addLower("餐厅服务员", 25000, 45000, "服务礼仪与沟通技巧", 10);
        addLower("快递员", 35000, 70000, "配送路线规划与客户服务", 9);
        addLower("建筑工人", 34000, 68000, "钢筋绑扎/砌筑抹灰/木工油漆等施工技能", 8);
        addLower("农民", 15000, 45000, "水稻小麦蔬菜水果种植及畜牧养殖技术", 7);
        addLower("零售导购", 26000, 50000, "产品知识推销话术+收银系统操作", 6);
        addLower("保安", 25000, 48000, "安保巡逻+门禁管理与应急处置", 5);
        addLower("客运司机", 42000, 85000, "驾驶执照+导航软件熟练+安全驾驶意识", 5);
        addLower("家政服务人员", 26000, 70000, "家务料理+婴幼儿照护+产妇护理专业培训", 5);
        addLower("仓储物流员", 25000, 48000, "出入库登记+物资盘点+物流分拣扫码设备操作", 5);
        addLower("清洁工", 20000, 42000, "环卫作业标准执行+垃圾车操作与分类知识", 4);
        addLower("个体商贩", 20000, 55000, "进货渠道与成本控制+讨价还价技巧", 4);
        addLower("维修技工", 32000, 68000, "水电安装维修+物业综合维修（水电气暖）", 4);
        addLower("餐饮后厨", 20000, 55000, "大锅菜烹饪+面点制作+配菜刀工+餐具清洗消毒", 4);
        addLower("护理人员", 28000, 55000, "老年护理常识+基础护理技能与责任心", 3);
        addLower("美容美发助理", 24000, 48000, "洗发吹风+皮肤基础知识与辅助操作", 2);
        addLower("废品回收员", 21000, 45000, "废品分类鉴别与价格行情", 1);
        addLower("洗车工", 24000, 42000, "洗车设备操作与细节清洁", 1);
        addLower("搬运工", 30000, 55000, "装卸搬运技巧与安全意识", 1);
        addLower("绿化工人", 25000, 45000, "植物养护与园林工具使用", 1);
        addLower("网吧网管", 27000, 46000, "计算机维护与网络故障排查", 1);
        addLower("矿工普工", 45000, 80000, "井下安全操作规程", 1);
    }

    private void registerMiddleClassOccupations() {
        addMiddle("小学教师", 60000, 120000, "教师资格证+教育学心理学背景", 8);
        addMiddle("注册护士", 55000, 110000, "执业护士证+临床护理经验", 7);
        addMiddle("软件工程师(IT)", 85000, 280000, "Java/前端/后端开发框架+数据库设计能力", 6);
        addMiddle("银行柜员", 58000, 100000, "金融从业资格+柜面业务熟练度", 6);
        addMiddle("会计", 60000, 130000, "CPA/中级会计师+财务报表编制", 5);
        addMiddle("初中教师", 65000, 130000, "学科专业知识+教学设计能力", 5);
        addMiddle("高中教师", 70000, 150000, "高考备考指导+班级管理经验", 4);
        addMiddle("公务员科员", 65000, 120000, "国考省考通过+公文写作能力", 4);
        addMiddle("销售代表", 60000, 250000, "CRM系统+顾问式销售方法+团队谈判技巧", 4);
        addMiddle("房产中介", 70000, 200000, "房产交易流程+客户资源积累", 3);
        addMiddle("保险经纪人", 70000, 200000, "保险代理人资格+客户开发能力", 3);
        addMiddle("人力资源专员", 58000, 120000, "HR六大模块+劳动法规理解", 3);
        addMiddle("行政执法人员", 62000, 115000, "执法资格证+法律法规熟悉度", 3);
        addMiddle("税务专员", 68000, 135000, "税务师资格+税法政策解读", 2);
        addMiddle("设计师", 60000, 160000, "Adobe/Figma/Sketch+品牌视觉/用户体验设计", 2);
        addMiddle("项目经理", 90000, 220000, "PMP认证+跨部门协调能力", 2);
        addMiddle("法务专员", 65000, 160000, "法律职业资格+合同审核合规风险管控", 2);
        addMiddle("高校教师", 90000, 180000, "博士学位+科研论文发表记录+实训指导能力", 2);
        addMiddle("审计师", 80000, 180000, "注册会计师+内控审计方法论", 2);
        addMiddle("旅游服务人员", 55000, 150000, "导游证+行程定制策划+客运服务规范", 2);
        addMiddle("媒体从业者", 58000, 160000, "新闻采编/摄影/翻译/编辑+双语转换精准度", 2);
        addMiddle("健身心理咨询", 60000, 150000, "心理咨询师资格/健身教练国职证书+共情倾听", 1);
        addMiddle("医技人员", 58000, 130000, "药剂师/理疗师/医学检验师资质+仪器分析能力", 2);
        addMiddle("技术工程师", 75000, 350000, "运维/测试/DBA/算法工程+Linux容器化部署", 2);
        addMiddle("金融分析师", 65000, 300000, "证券分析师/信贷审核员+CFA财务建模风险评估", 1);
        addMiddle("设计师(其他)", 65000, 170000, "室内设计CAD制图+工业设计3D建模+空间规划", 1);
        addMiddle("兽医", 70000, 160000, "执业兽医师+动物诊疗经验", 1);
        addMiddle("网络主播", 80000, 500000, "内容创作能力+粉丝运营策略", 2);
        addMiddle("电商运营", 65000, 180000, "数据分析+平台规则深度理解", 1);
    }

    private void registerUpperClassOccupations() {
        addUpper("企业高管", 500000, 100000000, "CEO/CTO/CFO/COO战略决策力+资本运作经验+行业影响力", 10);
        addUpper("高级医生", 400000, 3000000, "主任医师职称+顶级外科手术技艺+疑难重症诊治", 6);
        addUpper("教授研究员", 250000, 1000000, "博导资格+国家级科研项目主持+核心期刊高水平论文", 5);
        addUpper("财务高管", 400000, 2500000, "四大会计师事务所合伙人级别+投融资并购经验", 4);
        addUpper("投资金融专家", 500000, 8000000, "投资银行家/基金经理/风险投资人/私募股权+CFA三级", 5);
        addUpper("高级合伙人律师", 800000, 5000000, "顶级律所合伙+重大案件代理经验", 3);
        addUpper("科技创业者", 200000, 100000000, "产品技术壁垒+融资轮次推进能力+规模化复制", 3);
        addUpper("政府高级官员", 200000, 500000, "司局级职务+公共政策制定+治理专业度", 2);
        addUpper("企业总裁", 500000, 10000000, "大型国企高管/跨国公司中国区总裁+战略方向把控", 2);
        addUpper("文艺界知名人士", 300000, 100000000, "知名艺术家/导演/演员/音乐制作人+市场认可度", 3);
        addUpper("地产开发商", 1000000, 50000000, "土地储备+项目开发全链条操盘+资金运作能力", 2);
        addUpper("咨询公司合伙人", 800000, 4000000, "MBB级别+企业战略转型咨询案例", 1);
        addUpper("畅销书作家", 200000, 3000000, "版权版税收入+IP改编价值", 1);
        addUpper("职业运动员", 500000, 50000000, "顶级联赛合同+商业代言收入", 1);
        addUpper("专利律师", 500000, 2000000, "知识产权诉讼+技术理解力", 1);
        addUpper("税务筹划专家", 400000, 1800000, "跨国税务架构+合法节税方案", 1);
        addUpper("精算师", 450000, 1500000, "FIA/FSA精算师资格+风险定价模型", 1);
        addUpper("首席架构师", 700000, 2500000, "分布式系统设计+技术选型决策", 1);
        addUpper("AI科学家", 600000, 3000000, "顶级会议论文+工业界AI落地成果", 1);
        addUpper("区块链专家", 500000, 2500000, "去中心化协议设计+DeFi/NFT生态", 1);
        addUpper("院士级科学家", 300000, 1500000, "院士头衔+国家重点实验室主任", 1);
        addUpper("独立董事", 300000, 1500000, "行业权威声誉+公司治理专业度", 1);
        addUpper("游戏制作人", 600000, 4000000, "爆款游戏产品+全球发行运营", 1);
    }
}
