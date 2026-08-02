package com.intelliflow.modules.task.controller;

import com.intelliflow.common.response.ApiResponse;
import com.intelliflow.modules.task.domain.TaskEntity;
import com.intelliflow.modules.task.dto.*;
import com.intelliflow.modules.task.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST API Controller for Enterprise Task Management operations.
 * 
 * Provides HTTP endpoints for task CRUD, finite state machine transitions,
 * bulk updates, comment threads, DAG dependency links, and activity logs.
 */
@Validated
@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<TaskResponseDto>> createTask(
            @Valid @RequestBody CreateTaskRequestDto request,
            Authentication authentication) {
        UUID currentUserId = (UUID) authentication.getPrincipal();
        TaskResponseDto response = taskService.createTask(currentUserId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Task created successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<TaskDetailResponseDto>> getTaskById(@PathVariable UUID id) {
        TaskDetailResponseDto response = taskService.getTaskById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<TaskResponseDto>>> getTasks(
            Specification<TaskEntity> spec,
            Pageable pageable) {
        Page<TaskResponseDto> response = taskService.getTasks(spec, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<TaskResponseDto>> updateTask(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTaskRequestDto request) {
        TaskResponseDto response = taskService.updateTask(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Task updated successfully"));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<TaskResponseDto>> updateTaskStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTaskStatusRequestDto request) {
        TaskResponseDto response = taskService.updateTaskStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Task status updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> softDeleteTask(@PathVariable UUID id) {
        taskService.softDeleteTask(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Task soft-deleted successfully"));
    }

    @PatchMapping("/bulk-status")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> bulkUpdateStatus(
            @Valid @RequestBody BulkTaskStatusRequestDto request) {
        for (UUID taskId : request.getTaskIds()) {
            taskService.updateTaskStatus(taskId, new UpdateTaskStatusRequestDto(request.getStatus(), null));
        }
        return ResponseEntity.ok(ApiResponse.success(null, "Bulk task statuses updated successfully"));
    }

    @PostMapping("/{id}/comments")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<TaskCommentResponseDto>> addComment(
            @PathVariable UUID id,
            @Valid @RequestBody CreateTaskCommentRequestDto request,
            Authentication authentication) {
        UUID currentUserId = (UUID) authentication.getPrincipal();
        TaskCommentResponseDto response = taskService.addComment(id, currentUserId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Comment added successfully"));
    }

    @GetMapping("/{id}/comments")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<TaskCommentResponseDto>>> getTaskComments(
            @PathVariable UUID id,
            Pageable pageable) {
        Page<TaskCommentResponseDto> response = taskService.getTaskComments(id, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/dependencies")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> addDependency(
            @Valid @RequestBody TaskDependencyRequestDto request) {
        taskService.addDependency(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(null, "Dependency added successfully"));
    }

    @DeleteMapping("/dependencies")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> removeDependency(
            @RequestParam UUID blockingTaskId,
            @RequestParam UUID dependentTaskId) {
        taskService.removeDependency(blockingTaskId, dependentTaskId);
        return ResponseEntity.ok(ApiResponse.success(null, "Dependency removed successfully"));
    }

    @GetMapping("/{id}/activity-logs")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<TaskActivityLogResponseDto>>> getTaskActivityLogs(
            @PathVariable UUID id) {
        List<TaskActivityLogResponseDto> response = taskService.getTaskActivityLogs(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
