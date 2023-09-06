package com.grami1.dhcore.service;

import com.grami1.dhcore.domain.model.User;
import com.grami1.dhcore.domain.repository.UserRepository;
import com.grami1.dhcore.exception.UserException;
import com.grami1.dhcore.service.dto.UserDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public Mono<UserDto> createUser(String username) {
        User user = new User(username);

        return Mono.fromCallable(() -> userRepository.save(user))
                .subscribeOn(Schedulers.boundedElastic())
                .map(savedUser -> new UserDto(savedUser.getId(), savedUser.getName()))
                .doOnError(error ->
                        log.error("Failed to save user with username {} because of: {}", username, error.getMessage()))
                .onErrorMap(error -> new UserException("Failed to save user"));
    }

    @Transactional(readOnly = true)
    public Mono<UserDto> getUser(String username) {
        return Mono.fromCallable(() -> userRepository.findByName(username))
                .subscribeOn(Schedulers.boundedElastic())
                .doOnError(error ->
                        log.error("Failed to get user by username {} because of: {}", username, error.getMessage()))
                .onErrorMap(error -> new UserException("Failed to get user"))
                .flatMap(userOptional -> userOptional
                        .map(user -> Mono.just(new UserDto(user.getId(), user.getName())))
                        .orElse(Mono.error(new EntityNotFoundException("User is not found: " + username))));
    }
}
