package com.intelliflow.common.observability.service;

import java.util.concurrent.TimeUnit;

/**
 * Enterprise Business Metrics Service Contract.
 * 
 * Records custom application counters, timers, and gauges using Micrometer.
 */
public interface BusinessMetricsService {

    void recordUserRegistration();

    void recordTaskCreation();

    void recordDocumentUpload();

    void recordRagQuery(long durationMillis);
}
