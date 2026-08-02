package com.intelliflow.common.scheduling.job;

import com.intelliflow.modules.document.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Scheduled Job to permanently hard-delete soft-deleted documents older than 90 days.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SoftDeletedDocumentCleanupJob {

    private final DocumentRepository documentRepository;

    @Scheduled(cron = "${intelliflow.scheduling.soft-deleted-document-cleanup-cron:0 0 3 1 * ?}")
    @Transactional
    public void executeSoftDeletedDocumentCleanup() {
        long startTime = System.currentTimeMillis();
        log.info("START: Scheduled Job [SoftDeletedDocumentCleanupJob]");

        Instant threshold = Instant.now().minus(90, ChronoUnit.DAYS);
        int purgedCount = documentRepository.deleteSoftDeletedOlderThan(threshold);

        long duration = System.currentTimeMillis() - startTime;
        log.info("COMPLETE: Scheduled Job [SoftDeletedDocumentCleanupJob] - Permanently purged {} soft-deleted documents older than 90 days in {} ms", purgedCount, duration);
    }
}
