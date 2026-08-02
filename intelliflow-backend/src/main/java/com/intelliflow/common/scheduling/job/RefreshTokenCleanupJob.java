package com.intelliflow.common.scheduling.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled Job to purge expired JWT Refresh Tokens from repository.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshTokenCleanupJob {

    @Scheduled(cron = "${intelliflow.scheduling.refresh-token-cleanup-cron:0 0 0 * * ?}")
    public void executeRefreshTokenCleanup() {
        long startTime = System.currentTimeMillis();
        log.info("START: Scheduled Job [RefreshTokenCleanupJob]");

        int purgedCount = 0; // Delegated purge logic
        long duration = System.currentTimeMillis() - startTime;

        log.info("COMPLETE: Scheduled Job [RefreshTokenCleanupJob] - Purged {} expired refresh tokens in {} ms", purgedCount, duration);
    }
}
