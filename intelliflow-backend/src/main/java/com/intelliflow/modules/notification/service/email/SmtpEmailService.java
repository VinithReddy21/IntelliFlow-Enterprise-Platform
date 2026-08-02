package com.intelliflow.modules.notification.service.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * SMTP / Provider Implementation of EmailService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SmtpEmailService implements EmailService {

    private final EmailTemplateEngine emailTemplateEngine;

    @Override
    public void sendEmail(String recipientEmail, String subject, String bodyHtml) {
        log.info("DISPATCHING EMAIL: To: {}, Subject: '{}' (Length: {} chars)", recipientEmail, subject, bodyHtml.length());
    }

    @Override
    public void sendTemplatedEmail(String recipientEmail, String subject, String templateName, Map<String, Object> templateVariables) {
        String bodyHtml = emailTemplateEngine.renderTemplate(templateName, templateVariables);
        sendEmail(recipientEmail, subject, bodyHtml);
    }
}
