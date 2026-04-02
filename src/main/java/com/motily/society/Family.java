package com.motily.society;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import com.motily.human.Human;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.ConstraintMode;
import java.time.LocalDateTime;

@Entity
public class Family extends PanacheEntity {
    @Column(name = "name", nullable = false, length = 50) 
    public String name;
    
    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "founder_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    public Human founder;
    
    @Column(name = "total_wealth", nullable = false) 
    public double totalWealth;
    
    @Column(name = "social_influence", nullable = false) 
    public int socialInfluence;
    
    @Column(name = "created_at", nullable = false) 
    public LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false) 
    public LocalDateTime updatedAt;
}