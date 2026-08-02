package com.intelliflow.modules.notification.service.email;

import java.util.Map;

/**
 * Email Template Rendering Engine Contract.
 * 
 * Renders HTML email bodies from template strings and dynamic variables.
 */
public interface EmailTemplateEngine {

    String renderTemplate(String templateName, Map<String, Object> variables);
}
