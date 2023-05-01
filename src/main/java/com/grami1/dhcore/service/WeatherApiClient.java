package com.grami1.dhcore.service;

import com.grami1.dhcore.config.WeatherApiProperties;
import com.grami1.dhcore.service.dto.WeatherDto;
import com.grami1.dhcore.service.dto.WeatherResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class WeatherApiClient {

    private static final String WEATHER_API_URL_PARAMS = "?key={key}&q={city}&aqi=no";

    private final WebClient webClient;
    private final ErrorHandler errorHandler;
    private final WeatherApiProperties properties;

    public Mono<WeatherDto> getWeather(String city) {
        return webClient.get()
                .uri(properties.baseUrl() + WEATHER_API_URL_PARAMS, properties.key(), city)
                .retrieve()
                .onStatus(HttpStatusCode::isError, errorHandler::handleWeatherApiError)
                .bodyToMono(WeatherResponse.class)
                .map(weatherResponse -> new WeatherDto(
                        weatherResponse.current().condition(),
                        weatherResponse.current().tempC(),
                        weatherResponse.current().wind(),
                        weatherResponse.current().humidity()
                ));
    }
}
