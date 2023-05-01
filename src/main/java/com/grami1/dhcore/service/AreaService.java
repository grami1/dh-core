package com.grami1.dhcore.service;

import com.grami1.dhcore.domain.model.Area;
import com.grami1.dhcore.domain.model.User;
import com.grami1.dhcore.domain.repository.AreaRepository;
import com.grami1.dhcore.domain.repository.UserRepository;
import com.grami1.dhcore.service.dto.AreaDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AreaService {

    private final AreaRepository areaRepository;
    private final UserRepository userRepository;
    private final ErrorHandler errorHandler;

    @Transactional
    public Mono<AreaDto> createArea(long userId, String areaName) {
        try {
            Optional<User> user = userRepository.findById(userId);
            if (user.isEmpty()) {
                return Mono.error(new EntityNotFoundException("User is not found: " + userId));
            }
            Area area = new Area(areaName);
            user.get().addArea(area);

            Area savedArea = areaRepository.save(area);
            return Mono.just(new AreaDto(savedArea.getId(), savedArea.getName()));
        } catch (Exception e) {
            return Mono.error(errorHandler.handleAreaError(e, "Failed to add area " + areaName + " to user " + userId));
        }
    }

    @Transactional
    public Mono<List<AreaDto>> getAreas(String username) {
        try {
            Optional<User> user = userRepository.findByName(username);
            if (user.isEmpty()) {
                return Mono.error(new EntityNotFoundException("User is not found: " + username));
            }
            List<AreaDto> areas = user.get().getAreas().stream()
                    .map(area -> new AreaDto(area.getId(), area.getName()))
                    .toList();
            return Mono.just(areas);
        } catch (Exception e) {
            return Mono.error(errorHandler.handleAreaError(e, "Failed to get areas by username " + username));
        }
    }

    public Mono<AreaDto> getArea(Long areaId) {
        try {
            return areaRepository.findById(areaId)
                    .map(area -> Mono.just(new AreaDto(area.getId(), area.getName())))
                    .orElse(Mono.error(new EntityNotFoundException("Area is not found by areaId " + areaId)));
        } catch (Exception e) {
            return Mono.error(errorHandler.handleAreaError(e, "Failed to get area by areaId " + areaId));
        }
    }
}
