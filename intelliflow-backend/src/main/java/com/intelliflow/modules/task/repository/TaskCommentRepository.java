package com.intelliflow.modules.task.repository;

import com.intelliflow.modules.task.domain.TaskCommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository interface for TaskCommentEntity.
 */
@Repository
public interface TaskCommentRepository extends JpaRepository<TaskCommentEntity, UUID> {

    /**
     * Finds active non-deleted task comment by UUID.
     */
    Optional<TaskCommentEntity> findByIdAndDeletedAtIsNull(UUID id);

    /**
     * Finds all active comments for a task navigating task.id ordered newest first with pagination.
     */
    Page<TaskCommentEntity> findByTask_IdAndDeletedAtIsNullOrderByCreatedAtDesc(UUID taskId, Pageable pageable);

    /**
     * Counts active non-deleted comments for a task navigating task.id.
     */
    long countByTask_IdAndDeletedAtIsNull(UUID taskId);

    /**
     * Soft-deletes a comment by setting deletedAt timestamp.
     */
    @Modifying
    @Query("UPDATE TaskCommentEntity c SET c.deletedAt = CURRENT_TIMESTAMP WHERE c.id = :id AND c.deletedAt IS NULL")
    int softDeleteComment(@Param("id") UUID id);
}
