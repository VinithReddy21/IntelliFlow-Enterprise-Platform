package com.intelliflow.modules.notification.service;

import com.intelliflow.modules.notification.domain.NotificationType;
import com.intelliflow.modules.notification.service.email.EmailService;
import com.intelliflow.modules.notification.service.inapp.InAppNotificationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private EmailService emailService;

    @Mock
    private InAppNotificationService inAppNotificationService;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Test
    @DisplayName("notifyUser - Should delegate in-app notification dispatch")
    void notifyUser_Success() {
        UUID userId = UUID.randomUUID();

        notificationService.notifyUser(userId, "Title", "Message", NotificationType.IN_APP);

        verify(inAppNotificationService).sendInAppNotification(eq(userId), eq("Title"), eq("Message"), eq(NotificationType.IN_APP), any());
    }

    @Test
    @DisplayName("notifyUserWithEmail - Should dispatch both in-app and rendered email notifications")
    void notifyUserWithEmail_Success() {
        UUID userId = UUID.randomUUID();

        notificationService.notifyUserWithEmail(userId, "test@intelliflow.com", "Welcome", "WELCOME_EMAIL", Map.of("name", "Test User"));

        verify(inAppNotificationService).sendInAppNotification(eq(userId), eq("Welcome"), anyString(), eq(NotificationType.EMAIL), any());
        verify(emailService).sendTemplatedEmail(eq("test@intelliflow.com"), eq("Welcome"), eq("WELCOME_EMAIL"), anyMap());
    }
}
