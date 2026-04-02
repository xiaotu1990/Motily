package com.motily.human;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@ApplicationScoped
public class HumanService {
    @Inject
    HumanLifecycle humanLifecycle;
    
    @PersistenceContext
    EntityManager entityManager;
    
    public Human createHuman(String name, int gender, int birthYear, Human father, Human mother) {
        Human human = humanLifecycle.createHuman(name, gender, birthYear, father, mother);
        human.persist();
        return human;
    }
    
    public List<Human> listHumans(int page, int size) {
        return Human.findAll().page(page, size).list();
    }
    
    public Human getHumanById(Long id) {
        return Human.findById(id);
    }
    
    public void updateHuman(Human human) {
        human.persist();
    }
    
    public void ageHumans(int currentYear) {
        List<Human> humans = Human.findAll().list();
        for (Human human : humans) {
            if (humanLifecycle.isAlive(human, currentYear)) {
                humanLifecycle.ageHuman(human, currentYear);
                human.persist();
            }
        }
    }
    
    public long countHumans() {
        return Human.count();
    }
    
    public List<Human> getHumansByYear(int year) {
        return Human.find("birthYear <= ?1 and (deathYear is null or deathYear >= ?1)", year).list();
    }
}
