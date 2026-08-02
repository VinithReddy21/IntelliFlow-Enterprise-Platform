package com.intelliflow.modules.task.repository;

import com.intelliflow.modules.task.domain.TaskActivityLogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA Repository interface for TaskActivityLogEntity audit logs.
 */
@Repository
public interface TaskActivityLogRepository extends JpaRepository<TaskActivityLogEntity, UUID> {

    /**
     * Finds activity log records for a specific task navigating task.id ordered by timestamp descending.
     */
    Page<TaskActivityLogEntity> findByTask_IdOrderByCreatedAtDesc(UUID taskId, Pageable pageable);

    /**
     * Retrieves the top 50 recent audit activity log entries for a task navigating task.id.
     */
    List<TaskActivityLogEntity> findTop50ByTask_IdOrderByCreatedAtDesc(UUID taskId);
}
