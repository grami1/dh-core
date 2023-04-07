package com.grami1.dhcore.controller;

import com.grami1.dhcore.controller.dto.AreaRequestBody;
import com.grami1.dhcore.controller.dto.ErrorResponse;
import com.grami1.dhcore.exception.AreaException;
import com.grami1.dhcore.service.AreaService;
import com.grami1.dhcore.service.dto.AreaDto;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = AreaController.class)
@AutoConfigureWebTestClient
class AreaControllerTest {

    private static final String AREAS_URI = "/api/v1/areas";
    private static final String AREA_NAME = "myArea";

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private AreaService areaService;

    @Nested
    class CreateArea {
        @Test
        void when_area_is_created_then_return_ok() {
            when(areaService.createArea(1L, AREA_NAME)).thenReturn(Mono.just(new AreaDto(1L, AREA_NAME)));

            webTestClient.post()
                    .uri(AREAS_URI)
                    .bodyValue(new AreaRequestBody(1L, AREA_NAME))
                    .exchange()
                    .expectStatus()
                    .isCreated()
                    .expectBody(AreaDto.class)
                    .consumeWith(result -> {
                        AreaDto actualArea = result.getResponseBody();
                        assertEquals(AREA_NAME, actualArea.areaName());
                        assertEquals(1L, actualArea.areaId());
                    });
        }

        @Test
        void when_failed_to_create_area_then_return_error() {
            when(areaService.createArea(1L, AREA_NAME)).
                    thenReturn(Mono.error(new AreaException("Failed to add area " + AREA_NAME + " to user 1")));

            webTestClient.post()
                    .uri(AREAS_URI)
                    .bodyValue(new AreaRequestBody(1L, AREA_NAME))
                    .exchange()
                    .expectStatus()
                    .is5xxServerError()
                    .expectBody(ErrorResponse.class)
                    .isEqualTo(new ErrorResponse("Something went wrong"));
        }
    }

    @Nested
    class GetArea {
        @Test
        void when_area_exists_then_return_area() {
            when(areaService.getArea(1L)).thenReturn(Mono.just(new AreaDto(1L, AREA_NAME)));

            webTestClient.get()
                    .uri(AREAS_URI + "/{areaId}", 1L)
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBody(AreaDto.class)
                    .consumeWith(result -> {
                        AreaDto actualArea = result.getResponseBody();
                        assertEquals(AREA_NAME, actualArea.areaName());
                        assertEquals(1L, actualArea.areaId());
                    });
        }

        @Test
        void when_area_does_not_exist_then_return_not_found_error() {
            when(areaService.getArea(1L)).thenReturn(Mono.error(new EntityNotFoundException("Area is not found by areaId: " + 1L)));

            webTestClient.get()
                    .uri(AREAS_URI + "/{areaId}", 1L)
                    .exchange()
                    .expectStatus()
                    .isNotFound()
                    .expectBody(ErrorResponse.class)
                    .isEqualTo(new ErrorResponse("Area is not found by areaId: 1"));
        }

        @Test
        void when_failed_to_get_area_then_return_error() {
            when(areaService.getArea(1L)).thenReturn(Mono.error(new AreaException("Failed to get area by areaId  " + 1L)));

            webTestClient.get()
                    .uri(AREAS_URI + "/{areaId}", 1L)
                    .exchange()
                    .expectStatus()
                    .is5xxServerError()
                    .expectBody(ErrorResponse.class)
                    .isEqualTo(new ErrorResponse("Something went wrong"));
        }
    }
}