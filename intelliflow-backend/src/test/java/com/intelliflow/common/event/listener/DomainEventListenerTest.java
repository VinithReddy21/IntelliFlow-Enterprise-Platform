package com.intelliflow.common.event.listener;

import com.intelliflow.common.config.cache.CacheNames;
import com.intelliflow.modules.notification.service.NotificationService;
import com.intelliflow.modules.task.domain.TaskStatus;
import com.intelliflow.modules.task.event.TaskStatusChangedEvent;
import com.intelliflow.modules.user.event.UserCreatedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DomainEventListenerTest {

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache taskCache;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private CacheInvalidationEventListener cacheInvalidationEventListener;

    @InjectMocks
    private NotificationEventListener notificationEventListener;

    @InjectMocks
    private AuditEventLoggingListener auditEventLoggingListener;

    @Test
    @DisplayName("handleTaskStatusChanged - Should evict Redis task cache on TaskStatusChangedEvent")
    void handleTaskStatusChanged_EvictsCache() {
        UUID taskId = UUID.randomUUID();
        TaskStatusChangedEvent event = new TaskStatusChangedEvent(taskId, TaskStatus.TODO, TaskStatus.IN_PROGRESS);

        when(cacheManager.getCache(CacheNames.TASKS)).thenReturn(taskCache);

        cacheInvalidationEventListener.handleTaskStatusChanged(event);

        verify(taskCache).evict(taskId);
    }

    @Test
    @DisplayName("handleUserCreated - Should process notification dispatch without exceptions")
    void handleUserCreated_LogsDispatch() {
        UserCreatedEvent event = new UserCreatedEvent(UUID.randomUUID(), "jane@intelliflow.com", "Jane", "Doe");

        notificationEventListener.handleUserCreated(event);

        verify(notificationService).notifyUserWithEmail(eq(event.getUserId()), eq("jane@intelliflow.com"), anyString(), eq("WELCOME_EMAIL"), anyMap());
        auditEventLoggingListener.onDomainEvent(event);
    }
}
