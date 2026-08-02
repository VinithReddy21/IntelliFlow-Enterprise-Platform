package com.intelliflow.common.event.listener;

import com.intelliflow.modules.notification.domain.NotificationType;
import com.intelliflow.modules.notification.service.NotificationService;
import com.intelliflow.modules.task.event.TaskAssignedEvent;
import com.intelliflow.modules.task.event.TaskStatusChangedEvent;
import com.intelliflow.modules.user.event.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

/**
 * Async Domain Event Notification Dispatcher.
 * 
 * Consumes Domain Events AFTER transaction commit and delegates to NotificationService.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;

    @Async("eventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserCreated(UserCreatedEvent event) {
        log.info("NOTIFICATION LISTENER: Triggering welcome notification for user ID: {} ({})", event.getUserId(), event.getEmail());
        notificationService.notifyUserWithEmail(
                event.getUserId(),
                event.getEmail(),
                "Welcome to IntelliFlow",
                "WELCOME_EMAIL",
                Map.of("name", event.getFirstName() + " " + event.getLastName())
        );
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTaskAssigned(TaskAssignedEvent event) {
        if (event.getNewAssigneeId() != null) {
            log.info("NOTIFICATION LISTENER: Triggering task assignment notification to User ID: {}", event.getNewAssigneeId());
            notificationService.notifyUser(
                    event.getNewAssigneeId(),
                    "New Task Assigned",
                    "You have been assigned to task ID: " + event.getTaskId(),
                    NotificationType.IN_APP
            );
        }
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTaskStatusChanged(TaskStatusChangedEvent event) {
        log.info("NOTIFICATION LISTENER: Task ID {} transitioned status from {} to {}",
                event.getTaskId(), event.getOldStatus(), event.getNewStatus());
    }
}
