package com.motily.api;

import com.motily.society.SocialEvent;
import java.time.LocalDateTime;

public class EventDTO {
    public Long id;
    public String eventType;
    public int eventYear;
    public String description;
    public int influenceScore;
    public int probability;
    public LocalDateTime createdAt;
    
    public EventDTO(SocialEvent event) {
        this.id = event.id;
        this.eventType = event.eventType;
        this.eventYear = event.eventYear;
        this.description = event.description;
        this.influenceScore = event.influenceScore;
        this.probability = event.probability;
        this.createdAt = event.createdAt;
    }
}