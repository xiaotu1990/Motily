package com.motily.timeline;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import java.time.LocalDateTime;

@Entity
public class Timeline extends PanacheEntity {
    @Column(name = "current_year", nullable = false) 
    public int currentYear;
    
    @Column(name = "step_count", nullable = false) 
    public int stepCount;
    
    @Column(name = "status", nullable = false) 
    public int status; // 0-暂停, 1-运行, 2-完成
    
    @Column(name = "created_at", nullable = false) 
    public LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false) 
    public LocalDateTime updatedAt;
}
