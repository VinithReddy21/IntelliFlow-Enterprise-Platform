package com.intelliflow.modules.task.repository;

import com.intelliflow.modules.task.domain.TaskAttachmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA Repository interface for TaskAttachmentEntity (Polymorphic Storage).
 */
@Repository
public interface TaskAttachmentRepository extends JpaRepository<TaskAttachmentEntity, UUID> {

    /**
     * Finds all attachments linked to a specific entity (e.g. TASK, COMMENT, MEETING).
     */
    List<TaskAttachmentEntity> findByEntityTypeAndEntityId(String entityType, UUID entityId);

    /**
     * Counts attachments belonging to a specific entity.
     */
    long countByEntityTypeAndEntityId(String entityType, UUID entityId);
}
