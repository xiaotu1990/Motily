package com.motily.human;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;
import java.time.LocalDateTime;

@Entity
public class HumanExperience extends PanacheEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "human_id", nullable = false)
    public Human human;
    
    @Column(name = "event_type", nullable = false, length = 50)
    public String eventType; // 如：education, career, marriage, health, etc.
    
    @Column(name = "event_year", nullable = false)
    public int eventYear;
    
    @Column(name = "description", columnDefinition = "TEXT")
    public String description;
    
    @Column(name = "impact_level", nullable = false)
    public int impactLevel; // 1-低，2-中，3-高
    
    @Column(name = "created_at", nullable = false)
    public LocalDateTime createdAt;
    
    @Override
    public String toString() {
        return "{" +
            "\"id\": " + id +
            ", \"eventType\": \"" + eventType + "\"" +
            ", \"eventYear\": " + eventYear +
            ", \"description\": \"" + description + "\"" +
            ", \"impactLevel\": " + impactLevel +
            ", \"createdAt\": \"" + createdAt + "\"" +
            "}";
    }
}