package com.grami1.dhcore.service;

import com.grami1.dhcore.domain.model.Area;
import com.grami1.dhcore.domain.model.User;
import com.grami1.dhcore.domain.repository.AreaRepository;
import com.grami1.dhcore.domain.repository.UserRepository;
import com.grami1.dhcore.exception.AreaException;
import com.grami1.dhcore.service.dto.AreaDto;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AreaServiceTest {

    private static final String AREA_NAME = "myArea";
    private static final String USERNAME = "john@doe.com";

    @Mock
    AreaRepository areaRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ErrorHandler errorHandler;

    @InjectMocks
    AreaService areaService;

    @Nested
    class CreateArea {
        @Test
        void when_user_exists_then_return_created_area() {
            List<Area> areas = new ArrayList<>();
            User user = new User(1L, "testUser", areas);
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(areaRepository.save(any(Area.class))).thenReturn(new Area(1L, AREA_NAME, user));

            Mono<AreaDto> actual = areaService.createArea(1L, AREA_NAME);

            StepVerifier.create(actual)
                    .expectNextMatches(area -> AREA_NAME.equals(area.areaName()) && area.areaId() == 1L)
                    .verifyComplete();

            verifyNoInteractions(errorHandler);
        }

        @Test
        void when_user_does_not_exist_then_return_not_found_error() {
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            Mono<AreaDto> actual = areaService.createArea(1L, AREA_NAME);

            String errorMessage = "User is not found: 1";

            StepVerifier.create(actual)
                    .verifyErrorMatches(error -> error instanceof EntityNotFoundException &&
                            errorMessage.equals(error.getMessage()));

            verifyNoInteractions(errorHandler);
            verifyNoInteractions(areaRepository);
        }

        @Test
        void when_failed_to_create_area_then_return_error() {
            List<Area> areas = new ArrayList<>();
            User user = new User(1L, "testUser", areas);
            String errorMessage = "Failed to add area myArea to user 1";

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(areaRepository.save(any(Area.class))).thenThrow(HibernateException.class);
            when(errorHandler.handleAreaError(any(Throwable.class), anyString())).thenReturn(new AreaException(errorMessage));

            Mono<AreaDto> actual = areaService.createArea(1L, AREA_NAME);

            StepVerifier.create(actual)
                    .verifyErrorMatches(error -> error instanceof AreaException &&
                            errorMessage.equals(error.getMessage()));

        }
    }

    @Nested
    class GetArea {
        @Test
        void when_area_exists_then_return_area_dto() {
            when(areaRepository.findById(1L)).thenReturn(Optional.of(new Area(1L, AREA_NAME, new User())));

            Mono<AreaDto> actual = areaService.getArea(1L);

            StepVerifier.create(actual)
                    .expectNextMatches(area -> AREA_NAME.equals(area.areaName()) && area.areaId() == 1L)
                    .verifyComplete();

            verifyNoInteractions(errorHandler);
        }

        @Test
        void when_area_does_not_exist_then_return_not_found_error() {
            when(areaRepository.findById(1L)).thenReturn(Optional.empty());

            Mono<AreaDto> actual = areaService.getArea(1L);

            String errorMessage = "Area is not found by areaId 1";

            StepVerifier.create(actual)
                    .verifyErrorMatches(error -> error instanceof EntityNotFoundException &&
                            errorMessage.equals(error.getMessage()));

            verifyNoInteractions(errorHandler);
        }

        @Test
        void when_failed_to_get_area_then_return_error() {
            String errorMessage = "Failed to get area by areaId 1";

            when(areaRepository.findById(1L)).thenThrow(HibernateException.class);
            when(errorHandler.handleAreaError(any(Throwable.class), anyString())).thenReturn(new AreaException(errorMessage));

            Mono<AreaDto> actual = areaService.getArea(1L);

            StepVerifier.create(actual)
                    .verifyErrorMatches(error -> error instanceof AreaException &&
                            errorMessage.equals(error.getMessage()));
        }
    }

    @Nested
    class GetAreas {
        @Test
        void when_areas_exist_then_return_list_of_areas() {
            Area area = new Area(1L, AREA_NAME, new User());
            User user = new User(1L, "testUser", List.of(area));
            when(userRepository.findByName(USERNAME)).thenReturn(Optional.of(user));

            Mono<List<AreaDto>> actual = areaService.getAreas(USERNAME);

            StepVerifier.create(actual)
                    .expectNextMatches(areas -> AREA_NAME.equals(areas.get(0).areaName()) && areas.get(0).areaId() == 1L)
                    .verifyComplete();

            verifyNoInteractions(errorHandler);
        }

        @Test
        void when_areas_do_not_exist_then_return_empty_list() {
            User user = new User(1L, "testUser", Collections.emptyList());
            when(userRepository.findByName(USERNAME)).thenReturn(Optional.of(user));

            Mono<List<AreaDto>> actual = areaService.getAreas(USERNAME);

            StepVerifier.create(actual)
                    .expectNextMatches(List::isEmpty)
                    .verifyComplete();

            verifyNoInteractions(errorHandler);
        }

        @Test
        void when_user_does_not_exist_then_return_error() {
            String errorMessage = "User is not found: " + USERNAME;

            when(userRepository.findByName(USERNAME)).thenReturn(Optional.empty());

            Mono<List<AreaDto>> actual = areaService.getAreas(USERNAME);

            StepVerifier.create(actual)
                    .verifyErrorMatches(error -> error instanceof EntityNotFoundException &&
                            errorMessage.equals(error.getMessage()));
        }

        @Test
        void when_failed_to_get_areas_then_return_error() {
            String errorMessage = "Failed to get areas by username " + USERNAME;

            when(userRepository.findByName(USERNAME)).thenThrow(HibernateException.class);
            when(errorHandler.handleAreaError(any(Throwable.class), anyString())).thenReturn(new AreaException(errorMessage));

            Mono<List<AreaDto>> actual = areaService.getAreas(USERNAME);

            StepVerifier.create(actual)
                    .verifyErrorMatches(error -> error instanceof AreaException &&
                            errorMessage.equals(error.getMessage()));
        }
    }
}