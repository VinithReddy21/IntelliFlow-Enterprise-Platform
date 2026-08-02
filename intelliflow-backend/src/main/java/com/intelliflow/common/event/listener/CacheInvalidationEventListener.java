package com.intelliflow.common.event.listener;

import com.intelliflow.common.config.cache.CacheNames;
import com.intelliflow.modules.document.event.DocumentDeletedEvent;
import com.intelliflow.modules.task.event.TaskStatusChangedEvent;
import com.intelliflow.modules.user.event.UserStatusChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Cache Invalidation Event Listener.
 * 
 * Evicts Redis cache entries upon receiving Domain Events after database transaction commits.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CacheInvalidationEventListener {

    private final CacheManager cacheManager;

    @Async("eventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserStatusChanged(UserStatusChangedEvent event) {
        log.info("CACHE INVALIDATION: Evicting user cache for User ID: {}", event.getUserId());
        if (cacheManager.getCache(CacheNames.USERS) != null) {
            cacheManager.getCache(CacheNames.USERS).evict(event.getUserId());
        }
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTaskStatusChanged(TaskStatusChangedEvent event) {
        log.info("CACHE INVALIDATION: Evicting task cache for Task ID: {}", event.getTaskId());
        if (cacheManager.getCache(CacheNames.TASKS) != null) {
            cacheManager.getCache(CacheNames.TASKS).evict(event.getTaskId());
        }
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleDocumentDeleted(DocumentDeletedEvent event) {
        log.info("CACHE INVALIDATION: Evicting document cache for Document ID: {}", event.getDocumentId());
        if (cacheManager.getCache(CacheNames.DOCUMENTS) != null) {
            cacheManager.getCache(CacheNames.DOCUMENTS).evict(event.getDocumentId());
        }
    }
}
