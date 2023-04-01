package com.grami1.dhcore.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grami1.dhcore.controller.dto.ErrorResponse;
import com.grami1.dhcore.service.dto.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@AutoConfigureWireMock(port = 8084)
class WeatherControllerIntegrationTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    void when_get_weather_request_and_successful_response_then_return_response() {

        stubFor(get(urlEqualTo("/?key=key&q=Madrid&aqi=no"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("weather.json")));


        Location location = new Location("Madrid", "Madrid", "Spain");
        CurrentWeather currentWeather = new CurrentWeather(
                "2023-04-01 20:30",
                16.0,
                new Condition("Partly cloudy", "//cdn.weatherapi.com/weather/64x64/night/116.png"),
                21.7,
                45
        );
        WeatherResponse expectedResponse = new WeatherResponse(location, currentWeather);

        webTestClient.get()
                .uri("/api/v1/weather?city={city}", "Madrid")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(WeatherResponse.class)
                .isEqualTo(expectedResponse);
    }

    @Test
    void when_get_weather_request_and_error_response_then_return_error() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        WeatherError weatherError = new WeatherError(new WeatherErrorDetails("403", "Error message"));
        stubFor(get(urlEqualTo("/?key=key&q=Madrid&aqi=no"))
                .willReturn(aResponse()
                        .withStatus(403)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(weatherError))));

        ErrorResponse expectedResponse = new ErrorResponse("Failed request to WeatherApi");

        webTestClient.get()
                .uri("/api/v1/weather?city={city}", "Madrid")
                .exchange()
                .expectStatus()
                .is5xxServerError()
                .expectBody(ErrorResponse.class)
                .isEqualTo(expectedResponse);
    }
}