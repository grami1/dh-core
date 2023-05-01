package com.grami1.dhcore.controller;

import com.grami1.dhcore.controller.dto.AreaRequestBody;
import com.grami1.dhcore.service.AreaService;
import com.grami1.dhcore.service.dto.AreaDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/areas")
@RequiredArgsConstructor
public class AreaController {

    private final AreaService areaService;

    @PostMapping
    public Mono<ResponseEntity<AreaDto>> createArea(@RequestBody AreaRequestBody requestBody) {
        String areaName = requestBody.areaName();
        log.info("Create area: {}", areaName);

        return areaService.createArea(requestBody.userId(), areaName)
                .map(area -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(area));
    }

    @GetMapping
    public Mono<ResponseEntity<List<AreaDto>>> getAreas(@RequestParam String username) {
        log.info("Get all areas by username: {}", username);

        return areaService.getAreas(username)
                .map(areas -> ResponseEntity
                        .ok()
                        .body(areas));
    }

    @GetMapping("/{areaId}")
    public Mono<ResponseEntity<AreaDto>> getArea(@PathVariable Long areaId) {
        log.info("Get area by areaId: {}", areaId);

        return areaService.getArea(areaId)
                .map(area -> ResponseEntity
                        .ok()
                        .body(area));
    }
}
