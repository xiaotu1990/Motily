package com.motily.human;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.ConstraintMode;
import java.time.LocalDateTime;

@Entity
public class Human extends PanacheEntity {
    @Column(name = "dns_code", unique = true, length = 256) 
    public String dnsCode;
    
    @Column(name = "name", nullable = false, length = 50) 
    public String name;
    
    @Column(name = "gender", nullable = false) 
    public int gender; // 0-女, 1-男
    
    @Column(name = "birth_year", nullable = false) 
    public int birthYear;
    
    @Column(name = "death_year") 
    public Integer deathYear;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "father_id", nullable = true, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    public Human father;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mother_id", nullable = true, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    public Human mother;
    
    @Column(name = "wealth", nullable = false) 
    public double wealth;
    
    @Column(name = "social_class", nullable = false) 
    public int socialClass; // 1-底层, 2-中层, 3-上层
    
    @Column(name = "occupation", length = 50) 
    public String occupation;
    
    @Column(name = "personality", nullable = false, columnDefinition = "JSON") 
    public String personality;
    
    @Column(name = "talent", nullable = false, columnDefinition = "JSON") 
    public String talent;
    
    @Column(name = "belief", nullable = false, columnDefinition = "JSON") 
    public String belief;
    
    @Column(name = "created_at", nullable = false) 
    public LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false) 
    public LocalDateTime updatedAt;
}