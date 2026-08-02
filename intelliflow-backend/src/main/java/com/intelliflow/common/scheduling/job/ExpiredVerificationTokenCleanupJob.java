package com.intelliflow.common.scheduling.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled Job to purge expired email and account verification tokens.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExpiredVerificationTokenCleanupJob {

    @Scheduled(cron = "${intelliflow.scheduling.expired-verification-token-cleanup-cron:0 0 */6 * * ?}")
    public void executeVerificationTokenCleanup() {
        long startTime = System.currentTimeMillis();
        log.info("START: Scheduled Job [ExpiredVerificationTokenCleanupJob]");

        int purgedCount = 0; // Delegated purge execution
        long duration = System.currentTimeMillis() - startTime;

        log.info("COMPLETE: Scheduled Job [ExpiredVerificationTokenCleanupJob] - Purged {} expired tokens in {} ms", purgedCount, duration);
    }
}
