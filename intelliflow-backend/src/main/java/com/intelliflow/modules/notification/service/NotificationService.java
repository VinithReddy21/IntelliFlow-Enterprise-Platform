package com.intelliflow.modules.notification.service;

import com.intelliflow.modules.notification.domain.NotificationType;

import java.util.Map;
import java.util.UUID;

/**
 * Unified Core Notification Orchestration Service Contract.
 * 
 * Orchestrates multi-channel notification dispatches (Email, In-App, STOMP WebSocket).
 */
public interface NotificationService {

    void notifyUser(UUID recipientId, String title, String message, NotificationType type);

    void notifyUserWithEmail(UUID recipientId, String email, String subject, String templateName, Map<String, Object> variables);
}
