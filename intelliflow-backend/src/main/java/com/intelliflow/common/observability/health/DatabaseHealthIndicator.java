package com.intelliflow.common.observability.health;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Actuator Custom Health Indicator for PostgreSQL Database & pgvector extension.
 */
@Component("databaseHealthIndicator")
@RequiredArgsConstructor
public class DatabaseHealthIndicator implements HealthIndicator {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Health health() {
        try {
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            if (result != null && result == 1) {
                return Health.up()
                        .withDetail("database", "PostgreSQL")
                        .withDetail("validationQuery", "SELECT 1 SUCCESS")
                        .build();
            }
            return Health.down().withDetail("database", "PostgreSQL").withDetail("error", "Validation query returned null").build();
        } catch (Exception e) {
            return Health.down(e).withDetail("database", "PostgreSQL").withDetail("error", e.getMessage()).build();
        }
    }
}
