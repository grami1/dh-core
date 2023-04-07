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
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    ErrorHandler errorHandler;

    @InjectMocks
    UserService userService;

    @Nested
    class CreateUser {
        @Test
        void when_create_user_then_return_created_user() {
            String username = "testUser";
            when(userRepository.save(any(User.class))).thenReturn(new User(1L, username, Collections.emptyList()));

            Mono<UserDto> actual = userService.createUser(username);

            StepVerifier.create(actual)
                    .expectNextMatches(user -> username.equals(user.userName()) && user.userId() == 1L)
                    .verifyComplete();

            verifyNoInteractions(errorHandler);
        }

        @Test
        void when_failed_to_create_user_then_return_error() {
            String username = "testUser";
            String errorMessage = "Failed to save user testUser";
            when(userRepository.save(any(User.class))).thenThrow(HibernateException.class);
            when(errorHandler.handleUserError(any(Throwable.class), anyString())).thenReturn(new UserException(errorMessage));

            Mono<UserDto> actual = userService.createUser(username);

            StepVerifier.create(actual)
                    .verifyErrorMatches(error -> error instanceof UserException &&
                            errorMessage.equals(error.getMessage()));
        }
    }

    @Nested
    class GetUser {
        @Test
        void when_user_exists_then_return_user_dto() {
            String username = "testUser";
            when(userRepository.findByName(username))
                    .thenReturn(Optional.of(new User(1L, username, Collections.emptyList())));

            Mono<UserDto> actual = userService.getUser(username);

            StepVerifier.create(actual)
                    .expectNextMatches(user -> username.equals(user.userName()) && user.userId() == 1L)
                    .verifyComplete();

            verifyNoInteractions(errorHandler);
        }

        @Test
        void when_user_does_not_exist_then_return_error() {
            String username = "testUser";
            String errorMessage = "User is not found: testUser";
            when(userRepository.findByName(username)).thenReturn(Optional.empty());

            Mono<UserDto> actual = userService.getUser(username);

            StepVerifier.create(actual)
                    .verifyErrorMatches(error -> error instanceof EntityNotFoundException &&
                            errorMessage.equals(error.getMessage()));
        }

        @Test
        void when_failed_to_get_user_then_return_error() {
            String username = "testUser";
            String errorMessage = "Failed to get user testUser";
            when(userRepository.findByName(username)).thenThrow(HibernateException.class);
            when(errorHandler.handleUserError(any(Throwable.class), anyString())).thenReturn(new UserException(errorMessage));

            Mono<UserDto> actual = userService.getUser(username);

            StepVerifier.create(actual)
                    .verifyErrorMatches(error -> error instanceof UserException &&
                            errorMessage.equals(error.getMessage()));
        }
    }
}