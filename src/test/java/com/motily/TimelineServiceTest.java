package com.motily;

import com.motily.timeline.Timeline;
import com.motily.timeline.TimelineService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class TimelineServiceTest {

    @Inject
    TimelineService timelineService;

    @Test
    @Transactional
    public void testCreateTimeline() {
        Timeline timeline = timelineService.createTimeline(2000);
        assertNotNull(timeline);
        assertNotNull(timeline.id);
        assertEquals(2000, timeline.currentYear);
        assertEquals(0, timeline.stepCount);
        assertEquals(1, timeline.status); // Running status
    }

    @Test
    @Transactional
    public void testGetTimelineById() {
        Timeline created = timelineService.createTimeline(2000);
        Timeline found = timelineService.getTimelineById(created.id);
        assertNotNull(found);
        assertEquals(created.id, found.id);
    }

    @Test
    @Transactional
    public void testPauseAndResumeTimeline() {
        Timeline timeline = timelineService.createTimeline(2000);
        
        // Test pause
        timelineService.pauseTimeline(timeline);
        assertEquals(0, timeline.status); // Paused status
        
        // Test resume
        timelineService.resumeTimeline(timeline);
        assertEquals(1, timeline.status); // Running status
    }

    @Test
    @Transactional
    public void testStopTimeline() {
        Timeline timeline = timelineService.createTimeline(2000);
        timelineService.stopTimeline(timeline);
        assertEquals(2, timeline.status); // Stopped status
    }

    @Test
    @Transactional
    public void testStepForward() {
        Timeline timeline = timelineService.createTimeline(2000);
        int initialYear = timeline.currentYear;
        int initialStepCount = timeline.stepCount;
        
        timelineService.stepForward(timeline);
        
        assertEquals(initialYear + 1, timeline.currentYear);
        assertEquals(initialStepCount + 1, timeline.stepCount);
    }
}
