package com.grami1.dhcore.controller;

import com.grami1.dhcore.domain.model.Event;
import com.grami1.dhcore.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @Operation(summary = "Get events")
    @GetMapping
    public Mono<ResponseEntity<List<Event>>> getAreas(@RequestParam String sensorId,
                                                      @RequestParam int limit) {
        log.info("Get events sensorId: {}", sensorId);

        return eventService.getEvents(sensorId, limit)
                .map(ResponseEntity.ok()::body);
    }
}
