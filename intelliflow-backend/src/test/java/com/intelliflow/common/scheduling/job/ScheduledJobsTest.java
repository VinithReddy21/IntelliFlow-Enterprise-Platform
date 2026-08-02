package com.intelliflow.common.scheduling.job;

import com.intelliflow.modules.document.domain.DocumentStatus;
import com.intelliflow.modules.document.repository.DocumentRepository;
import com.intelliflow.modules.notification.domain.NotificationStatus;
import com.intelliflow.modules.notification.repository.NotificationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScheduledJobsTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private DocumentRepository documentRepository;

    @InjectMocks
    private RefreshTokenCleanupJob refreshTokenCleanupJob;

    @InjectMocks
    private NotificationCleanupJob notificationCleanupJob;

    @InjectMocks
    private SoftDeletedDocumentCleanupJob softDeletedDocumentCleanupJob;

    @InjectMocks
    private ExpiredVerificationTokenCleanupJob expiredVerificationTokenCleanupJob;

    @InjectMocks
    private DocumentLifecycleMaintenanceJob documentLifecycleMaintenanceJob;

    @InjectMocks
    private CacheWarmupJob cacheWarmupJob;

    @Test
    @DisplayName("executeNotificationCleanup - Should invoke repository delete for read notifications")
    void executeNotificationCleanup_Success() {
        when(notificationRepository.deleteOlderThanAndStatus(any(Instant.class), eq(NotificationStatus.READ))).thenReturn(5);

        notificationCleanupJob.executeNotificationCleanup();

        verify(notificationRepository).deleteOlderThanAndStatus(any(Instant.class), eq(NotificationStatus.READ));
    }

    @Test
    @DisplayName("executeSoftDeletedDocumentCleanup - Should invoke repository delete for soft-deleted documents")
    void executeSoftDeletedDocumentCleanup_Success() {
        when(documentRepository.deleteSoftDeletedOlderThan(any(Instant.class))).thenReturn(2);

        softDeletedDocumentCleanupJob.executeSoftDeletedDocumentCleanup();

        verify(documentRepository).deleteSoftDeletedOlderThan(any(Instant.class));
    }

    @Test
    @DisplayName("executeLifecycleMaintenance - Should query parsing and embedded documents")
    void executeLifecycleMaintenance_Success() {
        when(documentRepository.findByStatusAndDeletedAtIsNull(DocumentStatus.PARSING)).thenReturn(Collections.emptyList());
        when(documentRepository.findByStatusAndDeletedAtIsNull(DocumentStatus.EMBEDDED)).thenReturn(Collections.emptyList());

        documentLifecycleMaintenanceJob.executeLifecycleMaintenance();

        verify(documentRepository).findByStatusAndDeletedAtIsNull(DocumentStatus.PARSING);
        verify(documentRepository).findByStatusAndDeletedAtIsNull(DocumentStatus.EMBEDDED);
    }

    @Test
    @DisplayName("executeRefreshTokenCleanup - Should run without exceptions")
    void executeRefreshTokenCleanup_Success() {
        refreshTokenCleanupJob.executeRefreshTokenCleanup();
        expiredVerificationTokenCleanupJob.executeVerificationTokenCleanup();
        cacheWarmupJob.executeCacheWarmup();
    }
}
