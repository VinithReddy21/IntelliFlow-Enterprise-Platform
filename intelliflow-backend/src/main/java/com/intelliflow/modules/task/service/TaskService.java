package com.intelliflow.modules.task.service;

import com.intelliflow.modules.task.domain.TaskEntity;
import com.intelliflow.modules.task.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.UUID;

/**
 * Business Service Interface Contract for Task Management operations.
 */
public interface TaskService {

    /**
     * Creates a new task and writes an initial activity log entry.
     */
    TaskResponseDto createTask(UUID creatorId, CreateTaskRequestDto request);

    /**
     * Retrieves task details including subtasks and comment counts.
     */
    TaskDetailResponseDto getTaskById(UUID taskId);

    /**
     * Retrieves active task entity by UUID.
     */
    TaskEntity getActiveEntityById(UUID taskId);

    /**
     * Searches and paginates tasks dynamically using specifications.
     */
    Page<TaskResponseDto> getTasks(Specification<TaskEntity> spec, Pageable pageable);

    /**
     * Updates task attribute fields.
     */
    TaskResponseDto updateTask(UUID taskId, UpdateTaskRequestDto request);

    /**
     * Updates task status enforcing state machine rules and dependency checks.
     */
    TaskResponseDto updateTaskStatus(UUID taskId, UpdateTaskStatusRequestDto request);

    /**
     * Soft-deletes a task, updates status to ARCHIVED, and logs deletion.
     */
    void softDeleteTask(UUID taskId);

    /**
     * Adds a comment thread entry to a task.
     */
    TaskCommentResponseDto addComment(UUID taskId, UUID authorId, CreateTaskCommentRequestDto request);

    /**
     * Paginate comments for a task.
     */
    Page<TaskCommentResponseDto> getTaskComments(UUID taskId, Pageable pageable);

    /**
     * Adds a directed dependency link after validating cycle protection.
     */
    void addDependency(TaskDependencyRequestDto request);

    /**
     * Removes a directed dependency link.
     */
    void removeDependency(UUID blockingTaskId, UUID dependentTaskId);

    /**
     * Retrieves activity audit logs for a task.
     */
    List<TaskActivityLogResponseDto> getTaskActivityLogs(UUID taskId);
}
