package com.motily.society;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import java.time.LocalDateTime;

@Entity
public class SocialEvent extends PanacheEntity {
    @Column(name = "event_type", nullable = false, length = 50) 
    public String eventType;
    
    @Column(name = "event_year", nullable = false) 
    public int eventYear;
    
    @Column(name = "description", nullable = false, columnDefinition = "TEXT") 
    public String description;
    
    @Column(name = "influence_score", nullable = false) 
    public int influenceScore;
    
    @Column(name = "created_at", nullable = false) 
    public LocalDateTime createdAt;
}
