package com.grami1.dhcore.controller;

import com.grami1.dhcore.controller.dto.UserRequestBody;
import com.grami1.dhcore.domain.model.User;
import com.grami1.dhcore.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public Mono<ResponseEntity<User>> createUser(@RequestBody UserRequestBody requestBody) {
        String username = requestBody.username();
        log.info("Create user: {}",username );

        return userService.createUser(username)
                .map(user -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(user));
    }
}
