package com.grami1.dhcore.controller;

import com.grami1.dhcore.service.WeatherApiClient;
import com.grami1.dhcore.service.dto.WeatherDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherApiClient weatherApiClient;

    @GetMapping
    public Mono<WeatherDto> getWeather(@RequestParam String city) {
        log.info("Getting weather for {}", city);

        return weatherApiClient.getWeather(city);
    }

}
