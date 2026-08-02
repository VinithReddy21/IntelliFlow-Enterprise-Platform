package com.intelliflow.common.event;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

/**
 * Base Abstract Class for all Domain Events in IntelliFlow.
 * 
 * Provides global metadata for event correlation, unique event ID, and UTC timestamp.
 * Designed to support zero-friction serialization for future RabbitMQ/Kafka messaging brokers.
 */
@Getter
public abstract class DomainEvent {

    private final UUID eventId;
    private final Instant occurredOn;
    private final String eventType;

    protected DomainEvent(String eventType) {
        this.eventId = UUID.randomUUID();
        this.occurredOn = Instant.now();
        this.eventType = eventType;
    }
}
