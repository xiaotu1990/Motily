package com.motily.timeline;

import com.motily.human.HumanService;
import com.motily.society.SocietyService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDateTime;

@ApplicationScoped
public class TimelineService {
    @Inject
    TimeStepper timeStepper;
    
    @Inject
    HumanService humanService;
    
    @Inject
    SocietyService societyService;
    
    public Timeline createTimeline(int startYear) {
        Timeline timeline = new Timeline();
        timeline.currentYear = startYear;
        timeline.stepCount = 0;
        timeline.status = 1; // 运行状态
        timeline.createdAt = LocalDateTime.now();
        timeline.updatedAt = LocalDateTime.now();
        timeline.persist();
        return timeline;
    }
    
    public Timeline getTimelineById(Long id) {
        return Timeline.findById(id);
    }
    
    public void stepForward(Timeline timeline) {
        if (timeline.status != 1) {
            return;
        }
        
        // 推进时间
        int currentYear = timeline.currentYear;
        
        // 老化所有人类
        humanService.ageHumans(currentYear);
        
        // 演化社会
        societyService.evolveSociety(currentYear);
        
        // 更新时间轴
        timeline.currentYear++;
        timeline.stepCount++;
        timeline.updatedAt = LocalDateTime.now();
        timeline.persist();
    }
    
    public void pauseTimeline(Timeline timeline) {
        timeline.status = 0;
        timeline.updatedAt = LocalDateTime.now();
        timeline.persist();
    }
    
    public void resumeTimeline(Timeline timeline) {
        timeline.status = 1;
        timeline.updatedAt = LocalDateTime.now();
        timeline.persist();
    }
    
    public void stopTimeline(Timeline timeline) {
        timeline.status = 2;
        timeline.updatedAt = LocalDateTime.now();
        timeline.persist();
    }
    
    public void runSimulation(Timeline timeline, int years) {
        for (int i = 0; i < years; i++) {
            stepForward(timeline);
        }
    }
}
