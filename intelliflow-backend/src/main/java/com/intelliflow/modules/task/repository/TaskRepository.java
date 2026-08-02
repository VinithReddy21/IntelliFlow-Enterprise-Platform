package com.intelliflow.modules.task.repository;

import com.intelliflow.modules.task.domain.TaskEntity;
import com.intelliflow.modules.task.domain.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository interface for TaskEntity domain aggregate root.
 */
@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, UUID>, JpaSpecificationExecutor<TaskEntity> {

    /**
     * Finds active non-deleted task by UUID.
     */
    Optional<TaskEntity> findByIdAndDeletedAtIsNull(UUID id);

    /**
     * Finds active task with creator and assignee eager entity graph fetch (prevents N+1 queries).
     */
    @EntityGraph(attributePaths = {"creator", "assignee"})
    Optional<TaskEntity> findWithGraphByIdAndDeletedAtIsNull(UUID id);

    /**
     * Finds active tasks created by a specific user navigating creator.id property traversal.
     */
    Page<TaskEntity> findByCreator_IdAndDeletedAtIsNull(UUID creatorId, Pageable pageable);

    /**
     * Finds active tasks within a specific department with pagination and EntityGraph fetch.
     */
    @EntityGraph(attributePaths = {"creator", "assignee"})
    Page<TaskEntity> findByDepartmentIdAndDeletedAtIsNull(UUID departmentId, Pageable pageable);

    /**
     * Finds all active subtasks belonging to a parent task navigating parentTask.id.
     */
    List<TaskEntity> findByParentTask_IdAndDeletedAtIsNull(UUID parentTaskId);

    /**
     * Atomically updates task status in database.
     */
    @Modifying
    @Query("UPDATE TaskEntity t SET t.status = :status, t.updatedAt = CURRENT_TIMESTAMP WHERE t.id = :id AND t.deletedAt IS NULL")
    int updateTaskStatus(@Param("id") UUID id, @Param("status") TaskStatus status);

    /**
     * Soft-deletes a task by setting deletedAt timestamp and updating status to ARCHIVED.
     */
    @Modifying
    @Query("UPDATE TaskEntity t SET t.status = com.intelliflow.modules.task.domain.TaskStatus.ARCHIVED, t.deletedAt = CURRENT_TIMESTAMP, t.updatedAt = CURRENT_TIMESTAMP WHERE t.id = :id AND t.deletedAt IS NULL")
    int softDeleteTask(@Param("id") UUID id);

    /**
     * Checks if active task exists by ID.
     */
    boolean existsByIdAndDeletedAtIsNull(UUID id);
}
