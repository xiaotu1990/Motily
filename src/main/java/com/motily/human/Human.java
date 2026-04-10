package com.motily.human;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.ConstraintMode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;

@Entity
public class Human extends PanacheEntityBase {
    @Id
    public Long id = Math.abs(UUID.randomUUID().getMostSignificantBits());
    @Column(name = "dns_code", unique = true, length = 512) 
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
    @JsonIgnore
    public Human father;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mother_id", nullable = true, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @JsonIgnore
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
    
    @Column(name = "region_id", nullable = true) 
    public Integer regionId;
    
    @Column(name = "education_level", length = 20) 
    public String educationLevel = "小学";
    
    @Column(name = "health_status", length = 20) 
    public String healthStatus = "健康";
    
    @Column(name = "health_value") 
    public Integer healthValue = 100;
    
    @Column(name = "industry", length = 50) 
    public String industry;
    
    @Column(name = "network_size") 
    public Integer networkSize = 0;
    
    @Column(name = "marital_status", length = 20) 
    public String maritalStatus = "single";
    
    @Column(name = "spouse_id", nullable = true) 
    public Long spouseId;
    
    @Column(name = "pregnancy_weeks") 
    public Integer pregnancyWeeks = 0;
    
    @Column(name = "created_at", nullable = false) 
    public LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at", nullable = false) 
    public LocalDateTime updatedAt = LocalDateTime.now();
    
    @OneToMany(mappedBy = "human", cascade = CascadeType.ALL, fetch = jakarta.persistence.FetchType.LAZY)
    @JsonIgnore
    public java.util.List<com.motily.human.HumanExperience> experiences;
    
    @OneToMany(mappedBy = "human", cascade = CascadeType.ALL, fetch = jakarta.persistence.FetchType.LAZY)
    @JsonIgnore
    public java.util.List<com.motily.human.HumanMemory> memories;
    
    @Override
    public String toString() {
        return "{" +
            "\"id\": " + id +
            ", \"dnsCode\": \"" + (dnsCode != null ? dnsCode : "") + "\"" +
            ", \"name\": \"" + (name != null ? name : "") + "\"" +
            ", \"gender\": " + gender +
            ", \"birthYear\": " + birthYear +
            ", \"deathYear\": " + (deathYear != null ? deathYear : "null") +
            ", \"wealth\": " + wealth +
            ", \"socialClass\": " + socialClass +
            ", \"occupation\": \"" + (occupation != null ? occupation : "") + "\"" +
            ", \"personality\": \"" + (personality != null ? personality : "{}") + "\"" +
            ", \"talent\": \"" + (talent != null ? talent : "{}") + "\"" +
            ", \"belief\": \"" + (belief != null ? belief : "{}") + "\"" +
            ", \"regionId\": " + (regionId != null ? regionId : "null") +
            ", \"educationLevel\": \"" + (educationLevel != null ? educationLevel : "小学") + "\"" +
            ", \"healthStatus\": \"" + (healthStatus != null ? healthStatus : "健康") + "\"" +
            ", \"healthValue\": " + healthValue +
            ", \"industry\": \"" + (industry != null ? industry : "") + "\"" +
            ", \"networkSize\": " + networkSize +
            ", \"maritalStatus\": \"" + (maritalStatus != null ? maritalStatus : "single") + "\"" +
            ", \"spouseId\": " + (spouseId != null ? spouseId : "null") +
            ", \"pregnancyWeeks\": " + (pregnancyWeeks != null ? pregnancyWeeks : 0) +
            ", \"createdAt\": \"" + createdAt + "\"" +
            ", \"updatedAt\": \"" + updatedAt + "\"" +
            "}";
    }
}