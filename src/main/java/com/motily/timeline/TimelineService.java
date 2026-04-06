package com.motily.timeline;

import com.motily.engine.DemographyEngine;
import com.motily.engine.EconomicEngine;
import com.motily.engine.MarriageEngine;
import com.motily.engine.MobilityEngine;
import com.motily.human.HumanService;
import com.motily.society.SocietyService;
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

        demographyEngine.processWeeklyDemography(currentYear, currentWeek, rng);
        marriageEngine.processWeeklyMarriages(currentYear, currentWeek);
        mobilityEngine.processWeeklyMobility(currentYear);
        economicEngine.processWeeklyEconomy(currentYear, currentWeek);

        if (currentWeek == 1) {
            societyService.evolveSociety(currentYear, "normal");
        }

        managedTimeline.currentYear = currentYear;
        managedTimeline.currentWeek = currentWeek;
        managedTimeline.stepCount++;
        managedTimeline.updatedAt = LocalDateTime.now();
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
        for (int i = 0; i < years; i++) {
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
        mobilityEngine.processWeeklyMobility(currentYear);
        economicEngine.processWeeklyEconomy(currentYear, currentWeek);

        if (currentWeek == 1) {
            societyService.evolveSociety(currentYear, theme);
        }

        managedTimeline.currentYear = currentYear;
        managedTimeline.currentWeek = currentWeek;
        managedTimeline.stepCount++;
        managedTimeline.updatedAt = LocalDateTime.now();
    }
}
