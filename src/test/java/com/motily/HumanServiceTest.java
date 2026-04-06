package com.motily;

import com.motily.human.Human;
import com.motily.human.HumanService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class HumanServiceTest {

    @Inject
    HumanService humanService;

    @Test
    @Transactional
    public void testCreateHuman() {
        Human human = humanService.createHuman("TestPerson", 1, 2000, null, null);
        assertNotNull(human);
        assertNotNull(human.id);
        assertEquals("TestPerson", human.name);
        assertEquals(1, human.gender);
        assertEquals(2000, human.birthYear);
    }

    @Test
    @Transactional
    public void testListHumans() {
        // First create some humans
        humanService.createHuman("Person1", 0, 2000, null, null);
        humanService.createHuman("Person2", 1, 2000, null, null);

        List<Human> humans = humanService.listHumans(0, 10);
        assertNotNull(humans);
        assertTrue(humans.size() >= 2);
    }

    @Test
    @Transactional
    public void testGetHumanById() {
        Human created = humanService.createHuman("TestById", 1, 2000, null, null);
        Human found = humanService.getHumanById(created.id);
        assertNotNull(found);
        assertEquals(created.id, found.id);
        assertEquals("TestById", found.name);
    }

    @Test
    @Transactional
    public void testCountHumans() {
        long initialCount = humanService.countHumans();
        humanService.createHuman("CountTest", 1, 2000, null, null);
        long newCount = humanService.countHumans();
        assertEquals(initialCount + 1, newCount);
    }
}
