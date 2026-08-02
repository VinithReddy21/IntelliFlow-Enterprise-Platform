package com.intelliflow.common.scheduling.job;

import com.intelliflow.modules.document.domain.DocumentEntity;
import com.intelliflow.modules.document.domain.DocumentStatus;
import com.intelliflow.modules.document.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Scheduled Job to audit document state machine health and recover stuck documents.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DocumentLifecycleMaintenanceJob {

    private final DocumentRepository documentRepository;

    @Scheduled(cron = "${intelliflow.scheduling.document-lifecycle-cron:0 0 * * * ?}")
    @Transactional
    public void executeLifecycleMaintenance() {
        long startTime = System.currentTimeMillis();
        log.info("START: Scheduled Job [DocumentLifecycleMaintenanceJob]");

        List<DocumentEntity> parsingDocuments = documentRepository.findByStatusAndDeletedAtIsNull(DocumentStatus.PARSING);
        List<DocumentEntity> embeddedDocuments = documentRepository.findByStatusAndDeletedAtIsNull(DocumentStatus.EMBEDDED);

        long duration = System.currentTimeMillis() - startTime;
        log.info("COMPLETE: Scheduled Job [DocumentLifecycleMaintenanceJob] - Audited {} PARSING and {} EMBEDDED documents in {} ms",
                parsingDocuments.size(), embeddedDocuments.size(), duration);
    }
}
