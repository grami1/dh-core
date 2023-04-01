package com.grami1.dhcore.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "weather")
public record WeatherApiProperties(
        String baseUrl,
        String key
) {}
