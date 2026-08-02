package com.intelliflow.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Spring Data JPA Infrastructure Configuration.
 * 
 * Enables automatic population of @CreatedDate and @LastModifiedDate fields
 * on entity persistence operations.
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}
