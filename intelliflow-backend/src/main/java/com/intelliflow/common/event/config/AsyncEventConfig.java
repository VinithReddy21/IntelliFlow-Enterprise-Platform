package com.intelliflow.common.event.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Production Asynchronous Event Thread Pool Configuration.
 * 
 * Configures dedicated thread executor for non-blocking Spring event listeners.
 */
@Slf4j
@Configuration
@EnableAsync
public class AsyncEventConfig {

    @Bean(name = "eventTaskExecutor")
    public Executor eventTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("event-async-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        log.info("Initialized Async Event ThreadPoolTaskExecutor [Core: 10, Max: 50, Queue: 500]");
        return executor;
    }
}
