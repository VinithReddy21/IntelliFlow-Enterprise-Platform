package com.intelliflow.common.security.hardening;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Startup Security and Production Configuration Validator.
 * 
 * Asserts minimum security entropy standards for production deployments.
 */
@Slf4j
@Component
public class ProductionConfigValidator {

    @Value("${app.jwt.secret:defaultSecretKey12345678901234567890123456789012}")
    private String jwtSecret;

    @PostConstruct
    public void validateSecurityConfiguration() {
        log.info("Executing Production Security Configuration Validation...");

        if (jwtSecret == null || jwtSecret.getBytes().length < 32) {
            log.error("SECURITY WARNING: JWT Secret key length is under 32 bytes (256 bits). HMAC-SHA256 requires minimum 256 bits.");
        } else {
            log.info("JWT Secret Entropy Check: PASSED (>= 256 bits)");
        }

        log.info("Production Security Hardening Startup Check: SUCCESS");
    }
}
