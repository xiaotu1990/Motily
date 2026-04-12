package com.motily.timeline;

import com.motily.engine.DemographyEngine;
import com.motily.engine.EconomicEngine;
import com.motily.engine.MarriageEngine;
import com.motily.engine.MobilityEngine;
import com.motily.education.EducationEngine;
import com.motily.health.HealthEngine;
import com.motily.culture.BeliefEngine;
import com.motily.economy.InvestmentEngine;
import com.motily.economy.IndustryEngine;
import com.motily.society.SocialNetworkEngine;
import com.motily.simulation.PolicyEngine;
import com.motily.region.RegionService;
import com.motily.human.HumanService;
import com.motily.human.MemoryService;
import com.motily.society.SocietyService;
import com.motily.cache.StatsCacheService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Random;

@ApplicationScoped
public class TimelineService {
    private final Random rng = new Random();

    @Inject
    TimeStepper timeStepper;

    @Inject
    HumanService humanService;

    @Inject
    SocietyService societyService;

    @Inject
    DemographyEngine demographyEngine;

    @Inject
    MarriageEngine marriageEngine;

    @Inject
    MobilityEngine mobilityEngine;

    @Inject
    EconomicEngine economicEngine;

    @Inject
    EducationEngine educationEngine;

    @Inject
    HealthEngine healthEngine;

    @Inject
    BeliefEngine beliefEngine;

    @Inject
    InvestmentEngine investmentEngine;

    @Inject
    IndustryEngine industryEngine;

    @Inject
    SocialNetworkEngine socialNetworkEngine;

    @Inject
    PolicyEngine policyEngine;

    @Inject
    RegionService regionService;
    
    @Inject
    MemoryService memoryService;
    
    @Inject
    StatsCacheService statsCacheService;
    
    @Transactional
    public Timeline createTimeline(int startYear) {
        Timeline timeline = new Timeline();
        timeline.currentYear = startYear;
        timeline.currentWeek = 1;
        timeline.stepCount = 0;
        timeline.status = 1;
        timeline.createdAt = LocalDateTime.now();
        timeline.updatedAt = LocalDateTime.now();
        timeline.persist();
        return timeline;
    }
    
    public Timeline getTimelineById(Long id) {
        return Timeline.findById(id);
    }
    
    @Transactional
    public void stepForward(Timeline timeline) {
        if (timeline.status != 1) {
            return;
        }

        Timeline managedTimeline = Timeline.findById(timeline.id);
        if (managedTimeline == null) {
            return;
        }

        int currentYear = managedTimeline.currentYear;
        int currentWeek = managedTimeline.currentWeek;

        currentWeek++;
        if (currentWeek > 52) {
            currentWeek = 1;
            currentYear++;
        }

        try {
            // 处理各种引擎逻辑
            System.out.println("开始处理引擎逻辑，当前时间: " + currentYear + "年" + currentWeek + "周");
            
            System.out.println("处理人口引擎...");
            demographyEngine.processWeeklyDemography(currentYear, currentWeek, rng);
            
            System.out.println("处理婚姻引擎...");
            marriageEngine.processWeeklyMarriages(currentYear, currentWeek);
            
            System.out.println("处理健康引擎...");
            healthEngine.processWeeklyHealth(currentYear, currentWeek, rng);
            
            System.out.println("处理教育引擎...");
            educationEngine.processWeeklyEducation(currentYear, currentWeek, rng);
            
            System.out.println("处理社交网络引擎...");
            socialNetworkEngine.processWeeklyNetwork(currentYear, currentWeek, rng);
            
            System.out.println("处理 mobility 引擎...");
            mobilityEngine.processWeeklyMobility(currentYear);
            
            System.out.println("处理工业引擎...");
            industryEngine.processWeeklyIndustry(currentYear, currentWeek, rng);
            
            System.out.println("处理投资引擎...");
            investmentEngine.processWeeklyInvestment(currentYear, currentWeek, rng);
            
            System.out.println("处理经济引擎...");
            economicEngine.processWeeklyEconomy(currentYear, currentWeek);
            
            System.out.println("处理区域服务...");
            regionService.updateRegionalFactors(rng);
            
            System.out.println("处理信仰引擎...");
            beliefEngine.processWeeklyBeliefs(currentYear, currentWeek, rng);
            
            System.out.println("处理政策引擎...");
            policyEngine.processWeeklyPolicy(currentYear, currentWeek, rng);

            if (currentWeek == 1) {
                System.out.println("处理社会演化...");
                societyService.evolveSociety(currentYear, "normal");
            }

            if (currentWeek % 13 == 0) {
                System.out.println("处理季度社会事件...");
                societyService.generateQuarterlyEvents(currentYear, currentWeek, rng);
            }

            // 暂时禁用记忆衰减处理，以提高性能
            // java.util.List<com.motily.human.Human> humans = com.motily.human.Human.findAll().list();
            // for (com.motily.human.Human human : humans) {
            //     memoryService.decayMemories(human);
            // }
            
            System.out.println("引擎逻辑处理完成");
        } catch (Exception e) {
            // 捕获异常，确保时间更新仍然能执行
            System.err.println("处理引擎逻辑时发生错误: " + e.getMessage());
            e.printStackTrace();
        }

        // 无论引擎逻辑是否成功，都更新时间
        managedTimeline.currentYear = currentYear;
        managedTimeline.currentWeek = currentWeek;
        managedTimeline.stepCount++;
        managedTimeline.updatedAt = LocalDateTime.now();
        
        // 清除统计数据缓存，确保前端获取到最新的数据
        statsCacheService.clearAllStats();
    }
    
    @Transactional
    public void pauseTimeline(Timeline timeline) {
        // 重新获取实体，确保在持久化上下文中
        Timeline managedTimeline = Timeline.findById(timeline.id);
        if (managedTimeline != null) {
            managedTimeline.status = 0;
            managedTimeline.updatedAt = LocalDateTime.now();
        }
    }
    
    @Transactional
    public void resumeTimeline(Timeline timeline) {
        // 重新获取实体，确保在持久化上下文中
        Timeline managedTimeline = Timeline.findById(timeline.id);
        if (managedTimeline != null) {
            managedTimeline.status = 1;
            managedTimeline.updatedAt = LocalDateTime.now();
        }
    }
    
    @Transactional
    public void stopTimeline(Timeline timeline) {
        // 重新获取实体，确保在持久化上下文中
        Timeline managedTimeline = Timeline.findById(timeline.id);
        if (managedTimeline != null) {
            managedTimeline.status = 2;
            managedTimeline.updatedAt = LocalDateTime.now();
        }
    }
    
    public void runSimulation(Timeline timeline, int years, String theme) {
        int totalWeeks = years * 52;
        for (int i = 0; i < totalWeeks; i++) {
            stepForward(timeline, theme);
        }
    }
    
    @Transactional
    public void stepForward(Timeline timeline, String theme) {
        if (timeline.status != 1) {
            return;
        }

        Timeline managedTimeline = Timeline.findById(timeline.id);
        if (managedTimeline == null) {
            return;
        }

        int currentYear = managedTimeline.currentYear;
        int currentWeek = managedTimeline.currentWeek;

        currentWeek++;
        if (currentWeek > 52) {
            currentWeek = 1;
            currentYear++;
        }

        demographyEngine.processWeeklyDemography(currentYear, currentWeek, rng);
        marriageEngine.processWeeklyMarriages(currentYear, currentWeek);
        healthEngine.processWeeklyHealth(currentYear, currentWeek, rng);
        educationEngine.processWeeklyEducation(currentYear, currentWeek, rng);
        socialNetworkEngine.processWeeklyNetwork(currentYear, currentWeek, rng);
        mobilityEngine.processWeeklyMobility(currentYear);
        industryEngine.processWeeklyIndustry(currentYear, currentWeek, rng);
        investmentEngine.processWeeklyInvestment(currentYear, currentWeek, rng);
        economicEngine.processWeeklyEconomy(currentYear, currentWeek);
        regionService.updateRegionalFactors(rng);
        beliefEngine.processWeeklyBeliefs(currentYear, currentWeek, rng);
        policyEngine.processWeeklyPolicy(currentYear, currentWeek, rng);

        if (currentWeek == 1) {
            societyService.evolveSociety(currentYear, theme);
        }

        managedTimeline.currentYear = currentYear;
        managedTimeline.currentWeek = currentWeek;
        managedTimeline.stepCount++;
        managedTimeline.updatedAt = LocalDateTime.now();
    }
}
