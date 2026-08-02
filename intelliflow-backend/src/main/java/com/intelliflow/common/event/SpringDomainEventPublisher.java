package com.intelliflow.common.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Spring Implementation of DomainEventPublisher.
 * 
 * Delegates event publishing to Spring's internal ApplicationEventPublisher.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SpringDomainEventPublisher implements DomainEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(DomainEvent event) {
        log.info("Publishing Domain Event: [Type: {}, Event ID: {}]", event.getEventType(), event.getEventId());
        applicationEventPublisher.publishEvent(event);
    }
}
