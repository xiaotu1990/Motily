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
import com.motily.human.Human;
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

        final int year = currentYear;
        final int week = currentWeek;

        System.out.println("开始处理引擎逻辑，当前时间: " + year + "年" + week + "周");

        runEngine("人口引擎", () -> demographyEngine.processWeeklyDemography(year, week, rng));
        runEngine("婚姻引擎", () -> marriageEngine.processWeeklyMarriages(year, week));
        runEngine("健康引擎", () -> healthEngine.processWeeklyHealth(year, week, rng));
        runEngine("教育引擎", () -> educationEngine.processWeeklyEducation(year, week, rng));
        runEngine("社交网络引擎", () -> socialNetworkEngine.processWeeklyNetwork(year, week, rng));
        runEngine("流动引擎", () -> mobilityEngine.processWeeklyMobility(year));
        runEngine("工业引擎", () -> industryEngine.processWeeklyIndustry(year, week, rng));
        runEngine("投资引擎", () -> investmentEngine.processWeeklyInvestment(year, week, rng));
        runEngine("经济引擎", () -> economicEngine.processWeeklyEconomy(year, week, rng));
        runEngine("区域服务", () -> regionService.updateRegionalFactors(rng));
        runEngine("信仰引擎", () -> beliefEngine.processWeeklyBeliefs(year, week, rng));
        runEngine("政策引擎", () -> policyEngine.processWeeklyPolicy(year, week, rng));

        if (week == 1) {
            runEngine("社会演化", () -> societyService.evolveSociety(year, "normal"));
        }

        if (week % 13 == 0) {
            runEngine("季度事件", () -> societyService.generateQuarterlyEvents(year, week, rng));
        }

        managedTimeline.currentYear = year;
        managedTimeline.currentWeek = week;
        managedTimeline.stepCount++;
        managedTimeline.updatedAt = LocalDateTime.now();

        statsCacheService.clearAllStats();

        System.out.println("引擎逻辑处理完成，存活人口: " + Human.count("deathYear is null"));
    }

    private void runEngine(String name, Runnable action) {
        try {
            action.run();
        } catch (Exception e) {
            System.err.println(name + "执行失败: " + e.getMessage());
            e.printStackTrace();
        }
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
        economicEngine.processWeeklyEconomy(currentYear, currentWeek, rng);
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
