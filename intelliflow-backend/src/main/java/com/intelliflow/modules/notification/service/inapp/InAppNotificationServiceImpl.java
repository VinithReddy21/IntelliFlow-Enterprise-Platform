package com.intelliflow.modules.notification.service.inapp;

import com.intelliflow.common.exception.ResourceNotFoundException;
import com.intelliflow.modules.notification.domain.NotificationEntity;
import com.intelliflow.modules.notification.domain.NotificationStatus;
import com.intelliflow.modules.notification.domain.NotificationType;
import com.intelliflow.modules.notification.repository.NotificationRepository;
import com.intelliflow.modules.user.domain.UserEntity;
import com.intelliflow.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * In-App & STOMP WebSocket Implementation of InAppNotificationService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InAppNotificationServiceImpl implements InAppNotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    public NotificationEntity sendInAppNotification(UUID recipientId, String title, String message, NotificationType type, String targetUrl) {
        UserEntity recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", recipientId));

        NotificationEntity notification = NotificationEntity.builder()
                .recipient(recipient)
                .title(title)
                .message(message)
                .type(type)
                .status(NotificationStatus.UNREAD)
                .targetUrl(targetUrl)
                .build();

        NotificationEntity saved = notificationRepository.save(notification);
        log.info("Persisted in-app Notification ID: {} for recipient ID: {}", saved.getId(), recipientId);

        // Push real-time STOMP WebSocket frame to user-specific destination channel
        try {
            messagingTemplate.convertAndSendToUser(
                    recipientId.toString(),
                    "/queue/notifications",
                    Map.of(
                            "id", saved.getId().toString(),
                            "title", saved.getTitle(),
                            "message", saved.getMessage(),
                            "type", saved.getType().name(),
                            "targetUrl", saved.getTargetUrl() != null ? saved.getTargetUrl() : ""
                    )
            );
            log.info("Pushed STOMP WebSocket notification frame to user destination: /user/{}/queue/notifications", recipientId);
        } catch (Exception e) {
            log.warn("Failed to push STOMP WebSocket frame to user ID: {}", recipientId, e);
        }

        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationEntity> getUserNotifications(UUID recipientId, Pageable pageable) {
        return notificationRepository.findByRecipient_IdOrderByCreatedAtDesc(recipientId, pageable);
    }

    @Override
    @Transactional
    public void markAsRead(UUID notificationId) {
        NotificationEntity notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));

        notification.setStatus(NotificationStatus.READ);
        notification.setReadAt(Instant.now());
        notificationRepository.save(notification);
        log.info("Marked Notification ID: {} as READ", notificationId);
    }

    @Override
    @Transactional
    public void markAllAsRead(UUID recipientId) {
        int count = notificationRepository.markAllAsReadForUser(recipientId);
        log.info("Marked {} notifications as READ for User ID: {}", count, recipientId);
    }
}
