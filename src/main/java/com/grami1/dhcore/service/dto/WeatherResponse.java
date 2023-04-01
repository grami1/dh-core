package com.grami1.dhcore.service.dto;

public record WeatherResponse(
        Location location,
        CurrentWeather current
){}
