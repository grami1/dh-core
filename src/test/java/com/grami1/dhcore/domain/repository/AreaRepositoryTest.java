package com.grami1.dhcore.domain.repository;

import com.grami1.dhcore.domain.model.Area;
import com.grami1.dhcore.domain.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class AreaRepositoryTest {

    @Autowired
    AreaRepository areaRepository;

    @Autowired
    UserRepository userRepository;

    @Container
    private static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER = new PostgreSQLContainer<>("postgres:15.2");

    @AfterEach
    void tearDown() {
        areaRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void when_areas_exist_then_find_all_by_user() {
        User user = new User("testUser");
        User savedUser = userRepository.save(user);

        Area area1 = new Area("firstArea");
        area1.setUser(savedUser);
        Area area2 = new Area("secondArea");
        area2.setUser(savedUser);

        areaRepository.saveAll(List.of(area1, area2));

        List<Area> areas = areaRepository.findAllByUserId(savedUser.getId());
        assertEquals(2, areas.size());
        assertEquals("firstArea", areas.get(0).getName());
        assertEquals("secondArea", areas.get(1).getName());
    }

    @Test
    void when_area_does_not_exist_then_return_empty_list() {
        User user = new User("testUser");
        User savedUser = userRepository.save(user);

        List<Area> areas = areaRepository.findAllByUserId(savedUser.getId());
        assertTrue(areas.isEmpty());
    }

    @Test
    void when_user_does_not_exist_then_return_empty_list() {
        List<Area> areas = areaRepository.findAllByUserId(100L);
        assertTrue(areas.isEmpty());
    }
}