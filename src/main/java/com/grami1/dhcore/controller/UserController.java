package com.grami1.dhcore.controller;

import com.grami1.dhcore.controller.dto.UserRequestBody;
import com.grami1.dhcore.service.UserService;
import com.grami1.dhcore.service.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public Mono<ResponseEntity<UserDto>> createUser(@RequestBody UserRequestBody requestBody) {
        String username = requestBody.username();
        log.info("Create user: {}", username);

        return userService.createUser(username)
                .map(user -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(user));
    }

    @GetMapping("/{username}")
    public Mono<ResponseEntity<UserDto>> getUser(@PathVariable String username) {
        log.info("Get user: {}", username);

        return userService.getUser(username)
                .map(user -> ResponseEntity
                        .ok()
                        .body(user));
    }
}
