package com.grami1.dhcore.domain.repository;

import com.grami1.dhcore.domain.model.Event;
import com.grami1.dhcore.exception.EventException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class EventRepository {

    private static final String TABLE_NAME = "dh-events";
    private static final String KEY_NAME = "sensorId";
    private static final String ALIAS_NAME = "#" + KEY_NAME;
    private static final String ALIAS_VALUE = KEY_NAME + "Value";

    private final DynamoDbClient dynamoDbClient;

    public Flux<Event> getEventsBySensorId(String sensorId, int limit) {
        Map<String, String> attributeNames = Map.of(ALIAS_NAME, KEY_NAME);

        Map<String, AttributeValue> attributeValues = Map.of(":" + ALIAS_VALUE, AttributeValue.fromS(sensorId));

        QueryRequest queryRequest = QueryRequest.builder()
                .tableName(TABLE_NAME)
                .keyConditionExpression(ALIAS_NAME + " = :" + ALIAS_VALUE)
                .expressionAttributeNames(attributeNames)
                .expressionAttributeValues(attributeValues)
                .scanIndexForward(false)
                .limit(limit)
                .build();

        return Mono.fromCallable(() -> dynamoDbClient.query(queryRequest))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(response -> Flux.fromIterable(response.items()))
                .map(this::convert)
                .doOnError(error -> log.error("Failed to get events from DynamoDB: {}", error.getMessage()))
                .onErrorMap(error -> new EventException("Failed to get events from DB"));

    }

    private Event convert(Map<String, AttributeValue> attributes) {
        return new Event(
                attributes.get(KEY_NAME).s(),
                attributes.get("temperature").s(),
                attributes.get("humidity").s(),
                attributes.get("timestamp").s()
        );
    }
}