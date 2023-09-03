package com.grami1.dhcore.service;

import com.grami1.dhcore.domain.model.Event;
import com.grami1.dhcore.domain.repository.EventRepository;
import com.grami1.dhcore.exception.EventException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    EventRepository eventRepository;

    @InjectMocks
    EventService eventService;

    @Test
    void when_successful_response_from_db_then_return_flux_with_events() {

        Event event1 = new Event("dht22", "23.3", "56", "2023-09-02T16:18:35.230336");
        Event event2 = new Event("dht22", "24.3", "66", "2023-09-02T16:08:35.230336");

        when(eventRepository.getEventsBySensorId("dht22", 2)).thenReturn(Flux.just(event1, event2));

        Mono<List<Event>> events = eventService.getEvents("dht22", 2);

        StepVerifier.create(events)
                .expectNext(List.of(event1, event2))
                .verifyComplete();
    }

    @Test
    void when_failed_to_get_events_from_db_then_return_flux_with_error() {

        when(eventRepository.getEventsBySensorId("dht22", 2))
                .thenReturn(Flux.error(new EventException("Failed to get events from DB")));

        Mono<List<Event>> events = eventService.getEvents("dht22", 2);

        StepVerifier.create(events)
                .verifyError(EventException.class);
    }
}