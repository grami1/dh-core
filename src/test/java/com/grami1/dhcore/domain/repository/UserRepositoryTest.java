package com.grami1.dhcore.domain.repository;

import com.grami1.dhcore.domain.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Container
    private static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER = new PostgreSQLContainer<>("postgres:15.2");

    @Test
    void when_user_exists_then_find_by_name() {
        User user = new User();
        user.setName("testUser");
        userRepository.save(user);

        Optional<User> actualUser = userRepository.findByName("testUser");
        assertEquals("testUser", actualUser.get().getName());
    }
}