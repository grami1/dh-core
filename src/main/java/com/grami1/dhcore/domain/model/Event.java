package com.grami1.dhcore.domain.model;

public record Event(
        String sensorId,
        String temperature,
        String humidity,
        String timestamp
) {
}
