package com.grami1.dhcore.service;

import com.grami1.dhcore.domain.model.User;
import com.grami1.dhcore.domain.repository.UserRepository;
import com.grami1.dhcore.exception.UserException;
import com.grami1.dhcore.service.dto.UserDto;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.HibernateException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final String USERNAME = "testUser";

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    @Nested
    class CreateUser {

        @Test
        void when_create_user_then_return_created_user() {

            when(userRepository.save(any(User.class))).thenReturn(new User(1L, USERNAME, List.of()));

            Mono<UserDto> actual = userService.createUser(USERNAME);

            StepVerifier.create(actual)
                    .expectNext(new UserDto(1L, USERNAME))
                    .verifyComplete();
        }

        @Test
        void when_failed_to_create_user_then_return_error() {
            when(userRepository.save(any(User.class))).thenThrow(HibernateException.class);

            Mono<UserDto> actual = userService.createUser(USERNAME);

            StepVerifier.create(actual)
                    .expectError(UserException.class)
                    .verify();
        }
    }

    @Nested
    class GetUser {

        @Test
        void when_user_exists_then_return_user_dto() {
            when(userRepository.findByName(USERNAME))
                    .thenReturn(Optional.of(new User(1L, USERNAME, Collections.emptyList())));

            Mono<UserDto> actual = userService.getUser(USERNAME);

            StepVerifier.create(actual)
                    .expectNext(new UserDto(1L, USERNAME))
                    .verifyComplete();

        }

        @Test
        void when_user_does_not_exist_then_return_not_found_error() {
            when(userRepository.findByName(USERNAME)).thenReturn(Optional.empty());

            Mono<UserDto> actual = userService.getUser(USERNAME);

            StepVerifier.create(actual)
                    .expectError(EntityNotFoundException.class)
                    .verify();
        }

        @Test
        void when_failed_to_get_user_then_return_user_error() {
            when(userRepository.findByName(USERNAME)).thenThrow(HibernateException.class);

            Mono<UserDto> actual = userService.getUser(USERNAME);

            StepVerifier.create(actual)
                    .expectError(UserException.class)
                    .verify();
        }
    }
}