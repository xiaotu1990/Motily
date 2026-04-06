package com.motily.human;

import com.motily.dns.DnsService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.util.Random;
import com.fasterxml.jackson.databind.ObjectMapper;

@ApplicationScoped
public class HumanLifecycle {
    @Inject
    DnsService dnsService;
    
    private Random random = new Random();
    private ObjectMapper objectMapper = new ObjectMapper();
    
    public Human createHuman(String name, int gender, int birthYear, Human father, Human mother) {
        Human human = new Human();
        human.name = name;
        human.gender = gender;
        human.birthYear = birthYear;
        human.father = father;
        human.mother = mother;
        
        // 生成DNS编码
        if (father != null && mother != null) {
            human.dnsCode = dnsService.generateDnsFromParents(father.dnsCode, mother.dnsCode);
        } else {
            human.dnsCode = dnsService.generateDns();
        }
        
        // 解析DNS编码，设置初始属性
        var attributes = dnsService.parseDns(human.dnsCode);
        try {
            human.personality = objectMapper.writeValueAsString(attributes.get("personality"));
            human.talent = objectMapper.writeValueAsString(attributes.get("talent"));
            human.belief = objectMapper.writeValueAsString(attributes.get("belief"));
        } catch (Exception e) {
            // 处理序列化异常，使用默认值
            human.personality = "{}";
            human.talent = "{}";
            human.belief = "{}";
        }
        
        // 设置初始财富
        if (father != null && mother != null) {
            human.wealth = (father.wealth + mother.wealth) * 0.2;
        } else {
            human.wealth = 10000;
        }
        
        // 设置初始社会阶层
        human.socialClass = 2; // 默认中层
        
        // 设置创建和更新时间
        human.createdAt = LocalDateTime.now();
        human.updatedAt = LocalDateTime.now();
        
        return human;
    }
    
    public void ageHuman(Human human, int currentYear) {
        int age = currentYear - human.birthYear;
        
        // 根据年龄更新属性
        if (age < 0) {
            return;
        } else if (age <= 18) {
            // 成长阶段
            human.wealth += 1000;
        } else if (age <= 60) {
            // 工作阶段
            // 根据天赋和职业计算收入
            double income = calculateIncome(human);
            human.wealth += income;
        } else if (age <= 100) {
            // 退休阶段
            human.wealth -= 5000;
        } else {
            // 死亡
            human.deathYear = currentYear;
        }
        
        // 更新社会阶层
        updateSocialClass(human);
        
        // 更新更新时间
        human.updatedAt = LocalDateTime.now();
    }
    
    private double calculateIncome(Human human) {
        // 根据天赋和职业计算收入
        double baseIncome = 5000;
        
        // 解析天赋
        var talent = dnsService.parseDns(human.dnsCode).get("talent");
        // 简单计算，实际应该更复杂
        double talentMultiplier = 1.0 + random.nextDouble();
        
        return baseIncome * talentMultiplier;
    }
    
    private void updateSocialClass(Human human) {
        // 根据财富更新社会阶层
        if (human.wealth < 100000) {
            human.socialClass = 1; // 底层
        } else if (human.wealth < 1000000) {
            human.socialClass = 2; // 中层
        } else {
            human.socialClass = 3; // 上层
        }
    }
    
    public boolean isAlive(Human human, int currentYear) {
        return human.deathYear == null || currentYear < human.deathYear;
    }
}
