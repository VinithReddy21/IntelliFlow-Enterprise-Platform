package com.intelliflow.common.scheduling.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * Enterprise Production Scheduling Configuration.
 * 
 * Enables Spring @Scheduled execution with dedicated thread scheduler.
 */
@Slf4j
@Configuration
@EnableScheduling
public class SchedulingConfig {

    @Bean(name = "taskScheduler")
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5);
        scheduler.setThreadNamePrefix("scheduled-job-");
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(30);
        scheduler.initialize();
        log.info("Initialized ThreadPoolTaskScheduler pool size: 5, prefix: 'scheduled-job-'");
        return scheduler;
    }
}
