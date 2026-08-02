package com.intelliflow.common.event;

import com.intelliflow.modules.user.event.UserCreatedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DomainEventPublisherTest {

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private SpringDomainEventPublisher domainEventPublisher;

    @Test
    @DisplayName("publish - Should delegate event publication to Spring ApplicationEventPublisher")
    void publish_Success() {
        UserCreatedEvent event = new UserCreatedEvent(UUID.randomUUID(), "john@intelliflow.com", "John", "Doe");

        domainEventPublisher.publish(event);

        verify(applicationEventPublisher).publishEvent(event);
    }
}
