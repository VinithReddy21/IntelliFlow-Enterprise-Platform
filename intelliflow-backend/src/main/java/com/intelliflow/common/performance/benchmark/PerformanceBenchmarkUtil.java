package com.intelliflow.common.performance.benchmark;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

import java.util.function.Supplier;

/**
 * Enterprise Performance Benchmarking Utility.
 */
@Slf4j
public final class PerformanceBenchmarkUtil {

    private PerformanceBenchmarkUtil() {}

    public static <T> T measureExecutionTime(String taskName, Supplier<T> supplier) {
        StopWatch stopWatch = new StopWatch(taskName);
        stopWatch.start(taskName);
        try {
            return supplier.get();
        } finally {
            stopWatch.stop();
            log.info("BENCHMARK: [{}] executed in {} ms", taskName, stopWatch.getTotalTimeMillis());
        }
    }
}
