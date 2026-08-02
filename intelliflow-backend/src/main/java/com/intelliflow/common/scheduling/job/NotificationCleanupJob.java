package com.intelliflow.common.scheduling.job;

import com.intelliflow.modules.notification.domain.NotificationStatus;
import com.intelliflow.modules.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Scheduled Job to purge read notifications older than 30 days.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationCleanupJob {

    private final NotificationRepository notificationRepository;

    @Scheduled(cron = "${intelliflow.scheduling.notification-cleanup-cron:0 0 2 ? * SUN}")
    @Transactional
    public void executeNotificationCleanup() {
        long startTime = System.currentTimeMillis();
        log.info("START: Scheduled Job [NotificationCleanupJob]");

        Instant threshold = Instant.now().minus(30, ChronoUnit.DAYS);
        int deletedCount = notificationRepository.deleteOlderThanAndStatus(threshold, NotificationStatus.READ);

        long duration = System.currentTimeMillis() - startTime;
        log.info("COMPLETE: Scheduled Job [NotificationCleanupJob] - Purged {} read notifications older than 30 days in {} ms", deletedCount, duration);
    }
}
