package com.intelliflow.modules.notification.service.inapp;

import com.intelliflow.modules.notification.domain.NotificationEntity;
import com.intelliflow.modules.notification.domain.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * In-App & Real-Time WebSocket Notification Service Contract.
 */
public interface InAppNotificationService {

    NotificationEntity sendInAppNotification(UUID recipientId, String title, String message, NotificationType type, String targetUrl);

    Page<NotificationEntity> getUserNotifications(UUID recipientId, Pageable pageable);

    void markAsRead(UUID notificationId);

    void markAllAsRead(UUID recipientId);
}
