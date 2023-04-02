package com.grami1.dhcore.controller;

import com.grami1.dhcore.controller.dto.ErrorResponse;
import com.grami1.dhcore.controller.dto.UserRequestBody;
import com.grami1.dhcore.domain.model.User;
import com.grami1.dhcore.exception.UserException;
import com.grami1.dhcore.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@WebFluxTest(controllers = UserController.class)
@AutoConfigureWebTestClient
class UserControllerTest {

    private static final String USER_URI = "/api/v1/users";

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserService userService;

    @Test
    void when_user_created_then_return_ok() {
        String username = "testUser";
        User user = new User(1L, username, Collections.emptyList());
        when(userService.createUser(username)).thenReturn(Mono.just(user));

        webTestClient.post()
                .uri(USER_URI)
                .bodyValue(new UserRequestBody(username))
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(User.class)
                .consumeWith(result -> {
                    User actualUser = result.getResponseBody();
                    assertEquals(username, actualUser.getName());
                });
    }

    @Test
    void when_failed_to_create_user_then_return_error() {
        String username = "testUser";
        when(userService.createUser(username)).thenThrow(UserException.class);

        webTestClient.post()
                .uri(USER_URI)
                .bodyValue(new UserRequestBody(username))
                .exchange()
                .expectStatus()
                .is5xxServerError()
                .expectBody(ErrorResponse.class)
                .isEqualTo(new ErrorResponse("Something went wrong"));
    }
}