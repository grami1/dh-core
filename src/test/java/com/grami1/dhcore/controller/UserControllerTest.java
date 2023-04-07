package com.grami1.dhcore.controller;

import com.grami1.dhcore.controller.dto.ErrorResponse;
import com.grami1.dhcore.controller.dto.UserRequestBody;
import com.grami1.dhcore.exception.UserException;
import com.grami1.dhcore.service.UserService;
import com.grami1.dhcore.service.dto.UserDto;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@WebFluxTest(controllers = UserController.class)
@AutoConfigureWebTestClient
class UserControllerTest {

    private static final String USER_URI = "/api/v1/users";
    private static final String USER_NAME = "testUser";

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserService userService;

    @Nested
    class CreateUser {
        @Test
        void when_user_created_then_return_ok() {
            when(userService.createUser(USER_NAME)).thenReturn(Mono.just(new UserDto(1L, USER_NAME)));

            webTestClient.post()
                    .uri(USER_URI)
                    .bodyValue(new UserRequestBody(USER_NAME))
                    .exchange()
                    .expectStatus()
                    .isCreated()
                    .expectBody(UserDto.class)
                    .consumeWith(result -> {
                        UserDto actualUser = result.getResponseBody();
                        assertEquals(USER_NAME, actualUser.userName());
                        assertEquals(1L, actualUser.userId());
                    });
        }

        @Test
        void when_failed_to_create_user_then_return_error() {
            when(userService.createUser(USER_NAME)).thenReturn(Mono.error(new UserException("Failed to save user " + USER_NAME)));

            webTestClient.post()
                    .uri(USER_URI)
                    .bodyValue(new UserRequestBody(USER_NAME))
                    .exchange()
                    .expectStatus()
                    .is5xxServerError()
                    .expectBody(ErrorResponse.class)
                    .isEqualTo(new ErrorResponse("Something went wrong"));
        }
    }

    @Nested
    class GetUser {
        @Test
        void when_user_exists_then_return_user() {
            when(userService.getUser(USER_NAME)).thenReturn(Mono.just(new UserDto(1L, USER_NAME)));

            webTestClient.get()
                    .uri(USER_URI + "/{username}", USER_NAME)
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBody(UserDto.class)
                    .consumeWith(result -> {
                        UserDto actualUser = result.getResponseBody();
                        assertEquals(USER_NAME, actualUser.userName());
                        assertEquals(1L, actualUser.userId());
                    });
        }

        @Test
        void when_user_does_not_exist_then_return_not_found_error() {
            when(userService.getUser(USER_NAME)).thenReturn(Mono.error(new EntityNotFoundException("User is not found: " + USER_NAME)));

            webTestClient.get()
                    .uri(USER_URI + "/{username}", USER_NAME)
                    .exchange()
                    .expectStatus()
                    .isNotFound()
                    .expectBody(ErrorResponse.class)
                    .isEqualTo(new ErrorResponse("User is not found: testUser"));
        }

        @Test
        void when_failed_to_get_user_then_return_error() {
            when(userService.getUser(USER_NAME)).thenReturn(Mono.error(new UserException("Failed to get user  " + USER_NAME)));

            webTestClient.get()
                    .uri(USER_URI + "/{username}", USER_NAME)
                    .exchange()
                    .expectStatus()
                    .is5xxServerError()
                    .expectBody(ErrorResponse.class)
                    .isEqualTo(new ErrorResponse("Something went wrong"));
        }
    }
}