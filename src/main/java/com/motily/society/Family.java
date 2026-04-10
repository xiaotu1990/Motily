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
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Entity
public class Family extends PanacheEntity {
    @Column(name = "name", nullable = false, length = 50) 
    public String name;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "founder_id", nullable = true, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    public Human founder;
    
    @Column(name = "total_wealth", nullable = false) 
    public double totalWealth;
    
    @Column(name = "social_influence", nullable = false) 
    public int socialInfluence;
    
    @Column(name = "generations", nullable = false) 
    public int generations;
    
    @Column(name = "family_size", nullable = false) 
    public int familySize;
    
    @Column(name = "created_at", nullable = false) 
    public LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false) 
    public LocalDateTime updatedAt;
    
    @Override
    public String toString() {
        return "{" +
            "\"id\": " + id +
            ", \"name\": \"" + (name != null ? name : "") + "\"" +
            ", \"totalWealth\": " + totalWealth +
            ", \"socialInfluence\": " + socialInfluence +
            ", \"generations\": " + generations +
            ", \"familySize\": " + familySize +
            ", \"createdAt\": \"" + createdAt + "\"" +
            ", \"updatedAt\": \"" + updatedAt + "\"" +
            "}";
    }
    
    @Transactional
    public void addFamilyMember(Human human) {
        familySize++;
        updatedAt = LocalDateTime.now();
        persist();
    }
    
    @Transactional
    public void removeFamilyMember() {
        familySize = Math.max(0, familySize - 1);
        updatedAt = LocalDateTime.now();
        persist();
    }
    
    @Transactional
    public void addWealth(double amount) {
        totalWealth += amount;
        updatedAt = LocalDateTime.now();
        persist();
    }
    
    @Transactional
    public void removeWealth(double amount) {
        totalWealth = Math.max(0, totalWealth - amount);
        updatedAt = LocalDateTime.now();
        persist();
    }
    
    @Transactional
    public void inheritWealth(Human heir, double percentage, Random rng) {
        double inheritanceAmount = totalWealth * percentage;
        if (inheritanceAmount > 0 && totalWealth >= inheritanceAmount) {
            heir.wealth += inheritanceAmount;
            totalWealth -= inheritanceAmount;
            updatedAt = LocalDateTime.now();
            heir.updatedAt = LocalDateTime.now();
            persist();
            heir.persist();
        }
    }
    
    public double calculateFamilySupport(Human member) {
        double baseSupport = totalWealth * 0.01;
        double relationshipFactor = 0.5 + Math.random() * 0.5;
        return baseSupport * relationshipFactor;
    }
    
    public int calculateSocialClassBonus() {
        int baseBonus = 0;
        if (totalWealth > 1000000) {
            baseBonus = 1;
        } else if (totalWealth > 500000) {
            baseBonus = 0;
        } else {
            baseBonus = -1;
        }
        return baseBonus;
    }
    
    public static Family findByFounder(Human founder) {
        return find("founder_id = ?1", founder.id).firstResult();
    }
    
    public static List<Family> findWealthyFamilies(double minWealth) {
        return find("total_wealth >= ?1", minWealth).list();
    }
    
    public static Family createFamily(String name, Human founder) {
        Family family = new Family();
        family.name = name;
        family.founder = founder;
        family.totalWealth = founder.wealth * 0.5;
        family.socialInfluence = 10;
        family.generations = 1;
        family.familySize = 1;
        family.createdAt = LocalDateTime.now();
        family.updatedAt = LocalDateTime.now();
        family.persist();
        
        // 从创始人财富中转移部分到家庭
        founder.wealth *= 0.5;
        founder.updatedAt = LocalDateTime.now();
        founder.persist();
        
        return family;
    }
}