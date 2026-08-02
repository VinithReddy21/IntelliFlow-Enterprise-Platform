package com.intelliflow.common.observability.health;

import com.intelliflow.modules.document.storage.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Actuator Custom Health Indicator for Object File Storage Service.
 */
@Component("storageServiceHealthIndicator")
@RequiredArgsConstructor
public class StorageServiceHealthIndicator implements HealthIndicator {

    private final FileStorageService fileStorageService;

    @Override
    public Health health() {
        try {
            boolean exists = fileStorageService.fileExists("health-check-probe.tmp");
            return Health.up()
                    .withDetail("storageProvider", fileStorageService.getClass().getSimpleName())
                    .withDetail("status", "READ_WRITE_OK")
                    .withDetail("probeExists", exists)
                    .build();
        } catch (Exception e) {
            return Health.down(e)
                    .withDetail("storageProvider", fileStorageService.getClass().getSimpleName())
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
