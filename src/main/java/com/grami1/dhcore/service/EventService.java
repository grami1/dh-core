package com.grami1.dhcore.service;

import com.grami1.dhcore.domain.model.Event;
import com.grami1.dhcore.domain.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public Mono<List<Event>> getEvents(String sensorId, int limit) {
        return eventRepository.getEventsBySensorId(sensorId, limit)
                .collectList()
                .doOnSuccess(list -> log.info("Successfully retrieved events from DB"));
    }
}
