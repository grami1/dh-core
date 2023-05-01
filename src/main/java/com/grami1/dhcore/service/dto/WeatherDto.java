package com.grami1.dhcore.service.dto;

public record WeatherDto(
        Condition condition,
        Double temperature,
        Double wind,
        Integer humidity
) {}
