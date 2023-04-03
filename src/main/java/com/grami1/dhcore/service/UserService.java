package com.grami1.dhcore.service;

import com.grami1.dhcore.domain.model.User;
import com.grami1.dhcore.domain.repository.UserRepository;
import com.grami1.dhcore.service.dto.UserDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ErrorHandler errorHandler;

    public Mono<User> createUser(String username) {
        User user = new User();
        user.setName(username);

        try {
            return Mono.just(userRepository.save(user));
        } catch (Exception e) {
            return Mono.error(errorHandler.handleUserError(e, "Failed to save user " + username));
        }
    }

    public Mono<UserDto> getUser(String username) {
        try {
            return userRepository.findByName(username)
                    .map(user -> Mono.just(new UserDto(user.getName())))
                    .orElse(Mono.error(new EntityNotFoundException("User is not found: " + username)));
        } catch (Exception e) {
            return Mono.error(errorHandler.handleUserError(e, "Failed to get user " + username));
        }
    }
}
