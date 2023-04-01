package com.grami1.dhcore.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CurrentWeather(
        @JsonProperty("last_updated")
        String lastUpdated,
        @JsonProperty("temp_c")
        Double tempC,
        Condition condition,
        @JsonProperty("wind_mph")
        Double wind,
        Integer humidity
) {}
