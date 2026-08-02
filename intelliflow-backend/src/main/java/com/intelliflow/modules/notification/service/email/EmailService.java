package com.intelliflow.modules.notification.service.email;

import java.util.Map;

/**
 * Provider-Agnostic Email Service Contract.
 * 
 * Abstract contract supporting SMTP, SendGrid, AWS SES, and Azure Communication Services.
 */
public interface EmailService {

    void sendEmail(String recipientEmail, String subject, String bodyHtml);

    void sendTemplatedEmail(String recipientEmail, String subject, String templateName, Map<String, Object> templateVariables);
}
