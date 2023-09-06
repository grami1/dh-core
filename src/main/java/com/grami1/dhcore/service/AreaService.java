package com.grami1.dhcore.service;

import com.grami1.dhcore.domain.model.Area;
import com.grami1.dhcore.domain.repository.AreaRepository;
import com.grami1.dhcore.domain.repository.UserRepository;
import com.grami1.dhcore.exception.AreaException;
import com.grami1.dhcore.service.dto.AreaDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AreaService {

    private final AreaRepository areaRepository;
    private final UserRepository userRepository;

    @Transactional
    public Mono<AreaDto> createArea(long userId, String areaName) {

        return Mono.fromCallable(() -> userRepository.findById(userId))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(userOptional -> userOptional
                        .map(Mono::just)
                        .orElse(Mono.error(new EntityNotFoundException("User is not found: " + userId))))
                .map(user -> {
                    Area area = new Area(areaName);
                    user.addArea(area);
                    return area;
                })
                .flatMap(area -> Mono.fromCallable(() -> areaRepository.save(area))
                        .subscribeOn(Schedulers.boundedElastic()))
                .map(savedArea -> new AreaDto(savedArea.getId(), savedArea.getName()))
                .doOnError(error ->
                        log.error("Failed to create area with name {} for user id {} because of: {}", areaName, userId, error.getMessage()))
                .onErrorMap(error -> new AreaException("Failed to create area"));
    }

    @Transactional
    public Mono<List<AreaDto>> getAreas(String username) {

        return Mono.fromCallable(() -> userRepository.findByName(username))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(userOptional -> userOptional
                        .map(Mono::just)
                        .orElse(Mono.error(new EntityNotFoundException("User is not found: " + username))))
                .flatMapMany(user -> Flux.fromIterable(user.getAreas()))
                .map(area -> new AreaDto(area.getId(), area.getName()))
                .collectList()
                .doOnError(error ->
                        log.error("Failed to get areas by username {} because of: {}", username, error.getMessage()))
                .onErrorMap(error -> new AreaException("Failed to get areas"));
    }

    public Mono<AreaDto> getArea(Long areaId) {
        return Mono.fromCallable(() -> areaRepository.findById(areaId))
                .subscribeOn(Schedulers.boundedElastic())
                .doOnError(error ->
                        log.error("Failed to get area by id {} because of: {}", areaId, error.getMessage()))
                .onErrorMap(error -> new AreaException("Failed to get area"))
                .flatMap(areaOptional -> areaOptional
                        .map(Mono::just)
                        .orElse(Mono.error(new EntityNotFoundException("Area is not found: " + areaId))))
                .map(area -> new AreaDto(area.getId(), area.getName()));

    }
}
