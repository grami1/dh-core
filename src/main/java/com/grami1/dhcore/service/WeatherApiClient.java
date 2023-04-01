package com.grami1.dhcore.service;

import com.grami1.dhcore.service.dto.WeatherResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class WeatherApiClient {

    private static final String WEATHER_API_URL = "https://api.weatherapi.com/v1/current.json?key={key}&q={city}&aqi=no";

    @Value("${weather.key}")
    private String key;

    private final WebClient webClient;
    private final ErrorHandler errorHandler;

    public Mono<WeatherResponse> getWeather(String city) {
        return webClient.get()
                .uri(WEATHER_API_URL, key, city)
                .retrieve()
                .onStatus(HttpStatusCode::isError, errorHandler::handleWeatherApiError)
                .bodyToMono(WeatherResponse.class);
    }
}
