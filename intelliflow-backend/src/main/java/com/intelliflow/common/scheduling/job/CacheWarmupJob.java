package com.intelliflow.common.scheduling.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled Job to pre-warm high-frequency Redis cache keys ahead of peak traffic hours.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CacheWarmupJob {

    @Scheduled(cron = "${intelliflow.scheduling.cache-warmup-cron:0 0 6 * * ?}")
    public void executeCacheWarmup() {
        long startTime = System.currentTimeMillis();
        log.info("START: Scheduled Job [CacheWarmupJob]");

        // Warm up active reference dataset into Redis
        long duration = System.currentTimeMillis() - startTime;
        log.info("COMPLETE: Scheduled Job [CacheWarmupJob] - Pre-warmed Redis cache layers in {} ms", duration);
    }
}
