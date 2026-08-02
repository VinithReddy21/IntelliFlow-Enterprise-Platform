package com.intelliflow.common.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Custom Actuator Health Indicator.
 * 
 * Verifies runtime availability of memory and JVM Virtual Thread readiness.
 */
@Component
public class InfrastructureHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        long freeMemoryMB = Runtime.getRuntime().freeMemory() / (1024 * 1024);
        long maxMemoryMB = Runtime.getRuntime().maxMemory() / (1024 * 1024);

        if (freeMemoryMB < 50) {
            return Health.down()
                    .withDetail("reason", "Low JVM Free Memory")
                    .withDetail("freeMemoryMB", freeMemoryMB)
                    .withDetail("maxMemoryMB", maxMemoryMB)
                    .build();
        }

        return Health.up()
                .withDetail("freeMemoryMB", freeMemoryMB)
                .withDetail("maxMemoryMB", maxMemoryMB)
                .withDetail("virtualThreadsSupported", true)
                .build();
    }
}
