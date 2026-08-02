package com.intelliflow.common.performance.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Production Async Thread Pool Optimization.
 * 
 * Separates CPU-bound (vector math, text parsing) and IO-bound (database, object store) executors.
 */
@Slf4j
@Configuration
public class AsyncThreadPoolConfig {

    @Bean(name = "cpuTaskExecutor")
    public Executor cpuTaskExecutor() {
        int processors = Runtime.getRuntime().availableProcessors();
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(processors);
        executor.setMaxPoolSize(processors * 2);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("cpu-worker-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        log.info("Initialized CPU-bound TaskExecutor [Core: {}, Max: {}]", processors, processors * 2);
        return executor;
    }

    @Bean(name = "ioTaskExecutor")
    public Executor ioTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(20);
        executor.setMaxPoolSize(100);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("io-worker-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        log.info("Initialized IO-bound TaskExecutor [Core: 20, Max: 100, Queue: 1000]");
        return executor;
    }
}
