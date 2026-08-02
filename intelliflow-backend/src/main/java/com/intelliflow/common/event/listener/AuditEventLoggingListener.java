package com.intelliflow.common.event.listener;

import com.intelliflow.common.event.DomainEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Audit Event Listener.
 * 
 * Intercepts domain events AFTER transaction commit and writes non-blocking structured audit logs.
 */
@Slf4j
@Component
public class AuditEventLoggingListener {

    @Async("eventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onDomainEvent(DomainEvent event) {
        log.info("AUDIT LOG: [Type: {}, Event ID: {}, OccurredAt: {}]",
                event.getEventType(), event.getEventId(), event.getOccurredOn());
    }
}
