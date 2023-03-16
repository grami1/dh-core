package com.grami1.dhcore.controller;

import com.grami1.dhcore.controller.dto.StateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class StateController {

    @GetMapping("/state")
    public Mono<StateResponse> getState() {
        log.info("Getting state");

        return Mono.just(new StateResponse(22.0));
    }

}
