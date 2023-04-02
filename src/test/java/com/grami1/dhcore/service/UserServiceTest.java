package com.grami1.dhcore.service;

import com.grami1.dhcore.domain.model.User;
import com.grami1.dhcore.domain.repository.UserRepository;
import com.grami1.dhcore.exception.UserException;
import org.hibernate.HibernateException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    ErrorHandler errorHandler;

    @InjectMocks
    UserService userService;

    @Test
    void when_create_user_then_return_created_user() {
        String username = "testUser";
        when(userRepository.save(any(User.class))).thenReturn(new User(1L, username, Collections.emptyList()));

        Mono<User> actual = userService.createUser(username);

        StepVerifier.create(actual)
                .expectNextMatches(user -> username.equals(user.getName()))
                .verifyComplete();

        verifyNoInteractions(errorHandler);
    }

    @Test
    void when_failed_to_create_user_then_return_error() {
        String username = "testUser";
        String errorMessage = "Failed to save user testUser";
        when(userRepository.save(any(User.class))).thenThrow(HibernateException.class);
        when(errorHandler.handleUserError(any(Throwable.class), anyString())).thenReturn(new UserException(errorMessage));

        Mono<User> actual = userService.createUser(username);

        StepVerifier.create(actual)
                .verifyErrorMatches(error -> errorMessage.equals(error.getMessage()));
    }
}