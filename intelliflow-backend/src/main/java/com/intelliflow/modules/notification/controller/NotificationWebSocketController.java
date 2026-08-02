package com.intelliflow.modules.notification.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

/**
 * WebSocket STOMP Endpoint Controller for Notifications.
 * 
 * Handles client subscriptions and ACK messages.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class NotificationWebSocketController {

    @MessageMapping("/notifications/ack")
    @SendToUser("/queue/notifications")
    public Map<String, Object> acknowledgeNotification(@Payload Map<String, Object> payload, Principal principal) {
        String userId = principal != null ? principal.getName() : "Anonymous";
        log.info("Received WebSocket client notification ACK from User ID: {} for payload: {}", userId, payload);
        return Map.of("status", "ACKNOWLEDGED", "timestamp", System.currentTimeMillis());
    }
}
