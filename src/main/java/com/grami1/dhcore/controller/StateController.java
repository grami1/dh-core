package com.grami1.dhcore.controller;

import com.grami1.dhcore.controller.dto.StateResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class StateController {

    @Operation(summary = "Get sensor state")
    @GetMapping("/state")
    public Mono<StateResponse> getState() {
        log.info("Getting state");

        // TODO: implement
        return Mono.just(new StateResponse(22.0));
    }

}
