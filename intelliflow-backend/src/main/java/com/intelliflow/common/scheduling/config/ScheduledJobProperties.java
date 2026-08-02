package com.intelliflow.common.scheduling.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Externalized Cron Expression Configuration Properties.
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "intelliflow.scheduling")
public class ScheduledJobProperties {

    /**
     * Refresh Token Cleanup Cron (Default: Every midnight at 00:00:00)
     */
    private String refreshTokenCleanupCron = "0 0 0 * * ?";

    /**
     * Read Notification Cleanup Cron (Default: Every Sunday at 02:00:00 AM)
     */
    private String notificationCleanupCron = "0 0 2 ? * SUN";

    /**
     * Soft-deleted Document Purge Cron (Default: 1st of every month at 03:00:00 AM)
     */
    private String softDeletedDocumentCleanupCron = "0 0 3 1 * ?";

    /**
     * Expired Verification Token Cleanup Cron (Default: Every 6 hours)
     */
    private String expiredVerificationTokenCleanupCron = "0 0 */6 * * ?";

    /**
     * Document Lifecycle Maintenance Cron (Default: Every hour)
     */
    private String documentLifecycleCron = "0 0 * * * ?";

    /**
     * Cache Warmup Cron (Default: Every morning at 06:00:00 AM)
     */
    private String cacheWarmupCron = "0 0 6 * * ?";
}
