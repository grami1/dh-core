package com.grami1.dhcore.controller;

import com.grami1.dhcore.controller.dto.ErrorResponse;
import com.grami1.dhcore.domain.model.Event;
import com.grami1.dhcore.exception.EventException;
import com.grami1.dhcore.service.EventService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@WebFluxTest(controllers = EventController.class)
@AutoConfigureWebTestClient
class EventControllerTest {

    private static final String EVENTS_URI = "/api/v1/events";
    private static final String SENSOR_ID = "dht22";
    private static final int LIMIT = 10;

    @MockBean
    private EventService eventService;

    @Autowired
    private WebTestClient webTestClient;

    @Nested
    class GetEvents {

        @Test
        void when_events_exist_then_return_events() {

            Event event1 = new Event("dht22", "23.3", "56", "2023-09-02T16:18:35.230336");
            Event event2 = new Event("dht22", "24.3", "66", "2023-09-02T16:08:35.230336");

            when(eventService.getEvents(SENSOR_ID, LIMIT)).thenReturn(Mono.just(List.of(event1, event2)));

            webTestClient
                    .mutateWith(mockJwt())
                    .get()
                    .uri(EVENTS_URI + "?sensorId={sensorId}&limit={limit}", SENSOR_ID, LIMIT)
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBodyList(Event.class)
                    .consumeWith(result -> {
                        List<Event> events = result.getResponseBody();
                        assertEquals(2, events.size());
                        assertEquals("2023-09-02T16:18:35.230336", events.get(0).timestamp());
                        assertEquals("2023-09-02T16:08:35.230336", events.get(1).timestamp());
                    });
        }

        @Test
        void when_events_are_empty_then_return_empty_list() {
            when(eventService.getEvents(SENSOR_ID, LIMIT)).thenReturn(Mono.just(List.of()));

            webTestClient
                    .mutateWith(mockJwt())
                    .get()
                    .uri(EVENTS_URI + "?sensorId={sensorId}&limit={limit}", SENSOR_ID, LIMIT)
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBodyList(Event.class)
                    .consumeWith(result -> {
                        List<Event> events = result.getResponseBody();
                        assertEquals(0, events.size());
                    });
        }

        @Test
        void when_failed_to_get_events_then_return_error() {
            when(eventService.getEvents(SENSOR_ID, LIMIT)).thenReturn(Mono.error(new EventException("Failed to get events from DB")));

            webTestClient
                    .mutateWith(mockJwt())
                    .get()
                    .uri(EVENTS_URI + "?sensorId={sensorId}&limit={limit}", SENSOR_ID, LIMIT)
                    .exchange()
                    .expectStatus()
                    .is5xxServerError()
                    .expectBody(ErrorResponse.class)
                    .isEqualTo(new ErrorResponse("Failed to get events from DB"));

        }
    }
}