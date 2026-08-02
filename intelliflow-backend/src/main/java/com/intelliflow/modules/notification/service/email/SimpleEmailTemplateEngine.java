package com.intelliflow.modules.notification.service.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Simple Implementation of EmailTemplateEngine.
 * 
 * Performs variable interpolation over HTML template layouts.
 */
@Slf4j
@Service
public class SimpleEmailTemplateEngine implements EmailTemplateEngine {

    @Override
    public String renderTemplate(String templateName, Map<String, Object> variables) {
        log.info("Rendering HTML email template: {}", templateName);

        String templateContent = switch (templateName) {
            case "WELCOME_EMAIL" -> """
                    <html>
                    <body>
                        <h2>Welcome to IntelliFlow, ${name}!</h2>
                        <p>Your account was successfully registered.</p>
                    </body>
                    </html>
                    """;
            case "TASK_ASSIGNED" -> """
                    <html>
                    <body>
                        <h2>New Task Assigned: ${taskTitle}</h2>
                        <p>Hi ${assigneeName}, you have been assigned to a task.</p>
                    </body>
                    </html>
                    """;
            default -> "<html><body><p>${content}</p></body></html>";
        };

        if (variables != null) {
            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                String key = "${" + entry.getKey() + "}";
                String value = entry.getValue() != null ? entry.getValue().toString() : "";
                templateContent = templateContent.replace(key, value);
            }
        }

        return templateContent;
    }
}
