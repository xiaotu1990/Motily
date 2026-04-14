package com.motily.human;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class HumanService {
    @Inject
    HumanLifecycle humanLifecycle;
    
    @PersistenceContext
    EntityManager entityManager;
    
    @Transactional
    public Human createHuman(String name, int gender, int birthYear, Human father, Human mother) {
        Human human = humanLifecycle.createHuman(name, gender, birthYear, father, mother);
        human.persist();
        return human;
    }
    
    @Transactional
    public Human createHuman(String name, int gender, int birthYear, double wealth, Human father, Human mother) {
        Human human = humanLifecycle.createHuman(name, gender, birthYear, father, mother);
        human.wealth = wealth;
        human.persist();
        return human;
    }
    
    public List<Human> listHumans(int page, int size) {
        return Human.findAll().page(page, size).list();
    }
    
    public Human getHumanById(Long id) {
        return Human.findById(id);
    }
    
    @Transactional
    public void updateHuman(Human human) {
        // 先查找现有的实体
        Human existingHuman = Human.findById(human.id);
        if (existingHuman != null) {
            // 更新属性
            existingHuman.dnsCode = human.dnsCode != null ? human.dnsCode : existingHuman.dnsCode;
            existingHuman.name = human.name != null ? human.name : existingHuman.name;
            existingHuman.gender = human.gender;
            existingHuman.birthYear = human.birthYear;
            existingHuman.deathYear = human.deathYear;
            existingHuman.father = human.father;
            existingHuman.mother = human.mother;
            existingHuman.wealth = human.wealth;
            existingHuman.socialClass = human.socialClass;
            existingHuman.occupation = human.occupation != null ? human.occupation : existingHuman.occupation;
            existingHuman.personality = human.personality != null ? human.personality : existingHuman.personality;
            existingHuman.talent = human.talent != null ? human.talent : existingHuman.talent;
            existingHuman.belief = human.belief != null ? human.belief : existingHuman.belief;
            existingHuman.updatedAt = java.time.LocalDateTime.now();
            // 保存更新
            existingHuman.persist();
        }
    }
    
    @Transactional
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

    @Transactional
    public boolean deleteHuman(Long id) {
        Human human = Human.findById(id);
        if (human == null) return false;
        human.delete();
        return true;
    }

    @Transactional
    public int deleteHumans(List<Long> ids) {
        int deleted = 0;
        for (Long id : ids) {
            Human human = Human.findById(id);
            if (human != null) {
                human.delete();
                deleted++;
            }
        }
        return deleted;
    }
}
