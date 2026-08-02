package com.intelliflow.modules.task.repository;

import com.intelliflow.modules.task.domain.TaskDependencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA Repository interface for TaskDependencyEntity (DAG Dependency Graph).
 */
@Repository
public interface TaskDependencyRepository extends JpaRepository<TaskDependencyEntity, UUID> {

    /**
     * Finds all tasks that block a given dependent task navigating dependentTask.id.
     */
    List<TaskDependencyEntity> findByDependentTask_Id(UUID dependentTaskId);

    /**
     * Finds all tasks that depend on a given blocking task navigating blockingTask.id.
     */
    List<TaskDependencyEntity> findByBlockingTask_Id(UUID blockingTaskId);

    /**
     * Checks if a dependency link already exists between two tasks navigating entity IDs.
     */
    boolean existsByBlockingTask_IdAndDependentTask_Id(UUID blockingTaskId, UUID dependentTaskId);

    /**
     * Removes a dependency link between two tasks navigating entity IDs.
     */
    void deleteByBlockingTask_IdAndDependentTask_Id(UUID blockingTaskId, UUID dependentTaskId);
}
