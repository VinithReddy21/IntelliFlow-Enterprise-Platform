package com.intelliflow.modules.notification.service;

import com.intelliflow.modules.notification.domain.NotificationType;
import com.intelliflow.modules.notification.service.email.EmailService;
import com.intelliflow.modules.notification.service.inapp.InAppNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

/**
 * Unified Core Notification Service Implementation.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final EmailService emailService;
    private final InAppNotificationService inAppNotificationService;

    @Override
    public void notifyUser(UUID recipientId, String title, String message, NotificationType type) {
        log.info("Orchestrating notification dispatch to User ID: {} via channel {}", recipientId, type);
        inAppNotificationService.sendInAppNotification(recipientId, title, message, type, null);
    }

    @Override
    public void notifyUserWithEmail(UUID recipientId, String email, String subject, String templateName, Map<String, Object> variables) {
        log.info("Orchestrating dual-channel (Email + In-App) notification dispatch to User ID: {}", recipientId);
        inAppNotificationService.sendInAppNotification(recipientId, subject, "You have a new email notification", NotificationType.EMAIL, null);
        emailService.sendTemplatedEmail(email, subject, templateName, variables);
    }
}
