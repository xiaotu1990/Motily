package com.motily.society;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import java.time.LocalDateTime;

@Entity
public class SocialIndicator extends PanacheEntity {
    @Column(name = "year", nullable = false) 
    public int year;
    
    @Column(name = "class_distribution", nullable = false, columnDefinition = "JSON") 
    public String classDistribution;
    
    @Column(name = "occupation_structure", nullable = false, columnDefinition = "JSON") 
    public String occupationStructure;
    
    @Column(name = "wealth_distribution", nullable = false, columnDefinition = "JSON") 
    public String wealthDistribution;
    
    @Column(name = "created_at", nullable = false) 
    public LocalDateTime createdAt;
    
    @Override
    public String toString() {
        return "{" +
            "\"id\": " + id +
            ", \"year\": " + year +
            ", \"classDistribution\": " + (classDistribution != null ? classDistribution : "null") +
            ", \"occupationStructure\": " + (occupationStructure != null ? occupationStructure : "null") +
            ", \"wealthDistribution\": " + (wealthDistribution != null ? wealthDistribution : "null") +
            ", \"createdAt\": \"" + createdAt + "\"" +
            "}";
    }
}
