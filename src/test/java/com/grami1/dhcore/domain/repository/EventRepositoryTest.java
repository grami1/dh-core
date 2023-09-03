package com.grami1.dhcore.domain.repository;

import com.grami1.dhcore.domain.model.Event;
import com.grami1.dhcore.exception.EventException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventRepositoryTest {

    @Mock
    DynamoDbClient dynamoDbClient;

    @InjectMocks
    EventRepository eventRepository;

    @Test
    void when_events_exist_then_return_events() {

        List<Map<String, AttributeValue>> items = List.of(
                Map.of(
                        "sensorId", AttributeValue.builder().s("dht22").build(),
                        "humidity", AttributeValue.builder().s("56").build(),
                        "temperature", AttributeValue.builder().s("23.3").build(),
                        "timestamp", AttributeValue.builder().s("2023-09-02T16:18:35.230336").build()
                ),
                Map.of(
                        "sensorId", AttributeValue.builder().s("dht22").build(),
                        "humidity", AttributeValue.builder().s("66").build(),
                        "temperature", AttributeValue.builder().s("24.3").build(),
                        "timestamp", AttributeValue.builder().s("2023-09-02T16:08:35.230336").build()
                )
        );
        QueryResponse queryResponse = QueryResponse.builder()
                .items(items)
                .build();
        when(dynamoDbClient.query(any(QueryRequest.class))).thenReturn(queryResponse);

        Flux<Event> events = eventRepository.getEventsBySensorId("dht22", 2);

        Event event1 = new Event("dht22", "23.3", "56", "2023-09-02T16:18:35.230336");
        Event event2 = new Event("dht22", "24.3", "66", "2023-09-02T16:08:35.230336");

        StepVerifier.create(events)
                .expectNext(event1, event2)
                .verifyComplete();
    }

    @Test
    void when_events_do_not_exist_then_return_empty_flux() {

        QueryResponse queryResponse = QueryResponse.builder()
                .items(List.of())
                .build();
        when(dynamoDbClient.query(any(QueryRequest.class))).thenReturn(queryResponse);

        Flux<Event> events = eventRepository.getEventsBySensorId("dht22", 2);

        StepVerifier.create(events)
                .verifyComplete();
    }

    @Test
    void when_failed_to_get_events_then_return_error() {

        when(dynamoDbClient.query(any(QueryRequest.class))).thenThrow(DynamoDbException.builder().build());

        Flux<Event> events = eventRepository.getEventsBySensorId("dht22", 2);

        StepVerifier.create(events)
                .verifyError(EventException.class);
    }
}