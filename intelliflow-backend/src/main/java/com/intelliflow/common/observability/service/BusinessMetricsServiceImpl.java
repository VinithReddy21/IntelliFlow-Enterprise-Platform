package com.intelliflow.common.observability.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Micrometer Implementation of BusinessMetricsService.
 */
@Slf4j
@Service
public class BusinessMetricsServiceImpl implements BusinessMetricsService {

    private final Counter userRegisteredCounter;
    private final Counter taskCreatedCounter;
    private final Counter documentUploadedCounter;
    private final Counter ragQueryCounter;
    private final Timer ragQueryTimer;

    public BusinessMetricsServiceImpl(MeterRegistry registry) {
        this.userRegisteredCounter = Counter.builder("intelliflow.users.registered")
                .description("Total number of successfully registered user accounts")
                .register(registry);

        this.taskCreatedCounter = Counter.builder("intelliflow.tasks.created")
                .description("Total number of created tasks")
                .register(registry);

        this.documentUploadedCounter = Counter.builder("intelliflow.documents.uploaded")
                .description("Total number of uploaded enterprise documents")
                .register(registry);

        this.ragQueryCounter = Counter.builder("intelliflow.rag.queries.count")
                .description("Total number of RAG knowledge engine queries executed")
                .register(registry);

        this.ragQueryTimer = Timer.builder("intelliflow.rag.query.latency")
                .description("Latency distribution of RAG knowledge search executions")
                .register(registry);
    }

    @Override
    public void recordUserRegistration() {
        userRegisteredCounter.increment();
        log.debug("Incremented metric: intelliflow.users.registered");
    }

    @Override
    public void recordTaskCreation() {
        taskCreatedCounter.increment();
        log.debug("Incremented metric: intelliflow.tasks.created");
    }

    @Override
    public void recordDocumentUpload() {
        documentUploadedCounter.increment();
        log.debug("Incremented metric: intelliflow.documents.uploaded");
    }

    @Override
    public void recordRagQuery(long durationMillis) {
        ragQueryCounter.increment();
        ragQueryTimer.record(durationMillis, TimeUnit.MILLISECONDS);
        log.debug("Recorded metric: intelliflow.rag.query.latency = {} ms", durationMillis);
    }
}
