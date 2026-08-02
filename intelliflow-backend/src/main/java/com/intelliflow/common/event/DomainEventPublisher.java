package com.intelliflow.common.event;

/**
 * Domain Event Publisher Contract.
 * 
 * Abstract contract decoupling business services from Spring's ApplicationEventPublisher,
 * enabling transparent migration to distributed event buses (Kafka, RabbitMQ, AWS SNS/SQS).
 */
public interface DomainEventPublisher {

    void publish(DomainEvent event);
}
