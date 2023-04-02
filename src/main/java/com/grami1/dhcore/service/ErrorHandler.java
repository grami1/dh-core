package com.grami1.dhcore.service;

import com.grami1.dhcore.exception.UserException;
import com.grami1.dhcore.exception.WeatherException;
import com.grami1.dhcore.service.dto.WeatherError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class ErrorHandler {

    public Mono<Throwable> handleWeatherApiError(ClientResponse clientResponse) {
        return clientResponse.bodyToMono(WeatherError.class)
                .flatMap(errorResponse -> {
                    log.error("Failed request to WeatherApi: {}. Status code: {}",
                            errorResponse.error().message(), clientResponse.statusCode());
                    return Mono.error(new WeatherException("Failed request to WeatherApi"));
                });
    }

    public Throwable handleUserError(Throwable error, String message) {
        log.error(message + ": {}", error.getMessage());
        return new UserException(message);
    }
}
