package com.motily.society;

import com.motily.human.Human;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@ApplicationScoped
public class SocietyService {
    @Inject
    SocialEvolution socialEvolution;
    
    public Family createFamily(String name, Human founder) {
        Family family = new Family();
        family.name = name;
        family.founder = founder;
        family.totalWealth = founder.wealth;
        family.socialInfluence = 10;
        family.createdAt = LocalDateTime.now();
        family.updatedAt = LocalDateTime.now();
        family.persist();
        return family;
    }
    
    public List<Family> listFamilies() {
        return Family.findAll().list();
    }
    
    public Family getFamilyById(Long id) {
        return Family.findById(id);
    }
    
    public void updateFamily(Family family) {
        family.updatedAt = LocalDateTime.now();
        family.persist();
    }
    
    public List<SocialEvent> listSocialEvents(int year) {
        return SocialEvent.find("eventYear = ?1", year).list();
    }
    
    public SocialEvent getSocialEventById(Long id) {
        return SocialEvent.findById(id);
    }
    
    public List<SocialIndicator> listSocialIndicators(int startYear, int endYear) {
        return SocialIndicator.find("year >= ?1 and year <= ?2", startYear, endYear).list();
    }
    
    public SocialIndicator getSocialIndicatorByYear(int year) {
        return SocialIndicator.find("year = ?1", year).firstResult();
    }
    
    public void evolveSociety(int year, String theme) {
        socialEvolution.evolveSociety(year, theme);
    }

    public void generateQuarterlyEvents(int year, int week, Random rng) {
        socialEvolution.generateQuarterlyEvents(year, week, rng);
    }
    
    public List<SocialEvent> listSocialEventsByTimeline(Long timelineId) {
        return SocialEvent.find("ORDER BY createdAt DESC").range(0, 49).list();
    }
}
