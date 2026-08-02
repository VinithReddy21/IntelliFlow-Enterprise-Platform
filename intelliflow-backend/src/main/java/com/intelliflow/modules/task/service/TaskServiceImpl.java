package com.intelliflow.modules.task.service;

import com.intelliflow.common.config.cache.CacheNames;
import com.intelliflow.common.exception.ResourceNotFoundException;
import com.intelliflow.modules.task.domain.*;
import com.intelliflow.modules.task.dto.*;
import com.intelliflow.modules.task.repository.*;
import com.intelliflow.modules.user.domain.UserEntity;
import com.intelliflow.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Enterprise Production Implementation of TaskService domain operations.
 * 
 * Enforces task state machine transitions, dependency cycle detection, blocking task validation,
 * automatic activity logging, and Redis caching eviction.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskCommentRepository taskCommentRepository;
    private final TaskActivityLogRepository taskActivityLogRepository;
    private final TaskDependencyRepository taskDependencyRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public TaskResponseDto createTask(UUID creatorId, CreateTaskRequestDto request) {
        UserEntity creator = userRepository.findByIdAndDeletedAtIsNull(creatorId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", creatorId));

        TaskEntity parentTask = null;
        if (request.getParentTaskId() != null) {
            parentTask = getActiveEntityById(request.getParentTaskId());
        }

        TaskEntity task = TaskEntity.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(TaskStatus.BACKLOG)
                .priority(request.getPriority() != null ? request.getPriority() : TaskPriority.MEDIUM)
                .creator(creator)
                .parentTask(parentTask)
                .dueDate(request.getDueDate())
                .estimatedHours(request.getEstimatedHours())
                .build();

        TaskEntity savedTask = taskRepository.save(task);

        logActivity(savedTask, creator, "TASK_CREATED", null, "Created task: " + savedTask.getTitle());
        log.info("Successfully created task ID: {} by creator ID: {}", savedTask.getId(), creatorId);

        return TaskResponseDto.fromEntity(savedTask);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheNames.TASKS, key = "#taskId")
    public TaskDetailResponseDto getTaskById(UUID taskId) {
        TaskEntity task = getActiveEntityById(taskId);

        List<TaskEntity> subtasks = taskRepository.findByParentTask_IdAndDeletedAtIsNull(taskId);
        List<TaskResponseDto> subtaskDtos = subtasks.stream()
                .map(TaskResponseDto::fromEntity)
                .collect(Collectors.toList());

        long commentCount = taskCommentRepository.countByTask_IdAndDeletedAtIsNull(taskId);

        return TaskDetailResponseDto.fromEntity(task, subtaskDtos, commentCount);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskEntity getActiveEntityById(UUID taskId) {
        return taskRepository.findByIdAndDeletedAtIsNull(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskResponseDto> getTasks(Specification<TaskEntity> spec, Pageable pageable) {
        Specification<TaskEntity> activeSpec = (root, query, cb) -> cb.isNull(root.get("deletedAt"));
        Specification<TaskEntity> combinedSpec = spec != null ? spec.and(activeSpec) : activeSpec;

        return taskRepository.findAll(combinedSpec, pageable)
                .map(TaskResponseDto::fromEntity);
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheNames.TASKS, key = "#taskId")
    public TaskResponseDto updateTask(UUID taskId, UpdateTaskRequestDto request) {
        TaskEntity task = getActiveEntityById(taskId);

        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }
        if (request.getEstimatedHours() != null) {
            task.setEstimatedHours(request.getEstimatedHours());
        }

        TaskEntity updatedTask = taskRepository.save(task);
        logActivity(updatedTask, updatedTask.getCreator(), "TASK_UPDATED", null, "Updated task metadata");
        return TaskResponseDto.fromEntity(updatedTask);
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheNames.TASKS, key = "#taskId")
    public TaskResponseDto updateTaskStatus(UUID taskId, UpdateTaskStatusRequestDto request) {
        TaskEntity task = getActiveEntityById(taskId);
        TaskStatus currentStatus = task.getStatus();
        TaskStatus targetStatus = request.getStatus();

        if (currentStatus == targetStatus) {
            return TaskResponseDto.fromEntity(task);
        }

        validateStatusTransition(currentStatus, targetStatus);

        if (targetStatus == TaskStatus.IN_PROGRESS || targetStatus == TaskStatus.COMPLETED) {
            validateNoUnresolvedBlockingDependencies(taskId);
        }

        task.setStatus(targetStatus);
        TaskEntity updatedTask = taskRepository.save(task);

        logActivity(updatedTask, updatedTask.getCreator(), "STATUS_CHANGED", currentStatus.name(), targetStatus.name());
        log.info("Updated task ID: {} status from {} to {}", taskId, currentStatus, targetStatus);

        return TaskResponseDto.fromEntity(updatedTask);
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheNames.TASKS, key = "#taskId")
    public void softDeleteTask(UUID taskId) {
        TaskEntity task = getActiveEntityById(taskId);
        taskRepository.softDeleteTask(taskId);
        logActivity(task, task.getCreator(), "TASK_DELETED", task.getStatus().name(), "SOFT_DELETED");
        log.info("Successfully soft-deleted task ID: {}", taskId);
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheNames.TASKS, key = "#taskId")
    public TaskCommentResponseDto addComment(UUID taskId, UUID authorId, CreateTaskCommentRequestDto request) {
        TaskEntity task = getActiveEntityById(taskId);
        UserEntity author = userRepository.findByIdAndDeletedAtIsNull(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", authorId));

        TaskCommentEntity comment = TaskCommentEntity.builder()
                .task(task)
                .author(author)
                .content(request.getContent())
                .build();

        TaskCommentEntity savedComment = taskCommentRepository.save(comment);
        logActivity(task, author, "COMMENT_ADDED", null, "Added comment ID: " + savedComment.getId());

        return TaskCommentResponseDto.fromEntity(savedComment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskCommentResponseDto> getTaskComments(UUID taskId, Pageable pageable) {
        return taskCommentRepository.findByTask_IdAndDeletedAtIsNullOrderByCreatedAtDesc(taskId, pageable)
                .map(TaskCommentResponseDto::fromEntity);
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheNames.TASKS, key = "#request.dependentTaskId")
    public void addDependency(TaskDependencyRequestDto request) {
        UUID blockingId = request.getBlockingTaskId();
        UUID dependentId = request.getDependentTaskId();

        if (blockingId.equals(dependentId)) {
            throw new IllegalArgumentException("Task cannot depend on itself");
        }

        TaskEntity blockingTask = getActiveEntityById(blockingId);
        TaskEntity dependentTask = getActiveEntityById(dependentId);

        if (taskDependencyRepository.existsByBlockingTask_IdAndDependentTask_Id(blockingId, dependentId)) {
            throw new IllegalStateException("Dependency relationship already exists");
        }

        detectDependencyCycle(blockingId, dependentId);

        TaskDependencyEntity dependency = TaskDependencyEntity.builder()
                .blockingTask(blockingTask)
                .dependentTask(dependentTask)
                .build();

        taskDependencyRepository.save(dependency);
        logActivity(dependentTask, dependentTask.getCreator(), "DEPENDENCY_ADDED", null, "Added blocking task: " + blockingTask.getTitle());
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheNames.TASKS, key = "#dependentTaskId")
    public void removeDependency(UUID blockingTaskId, UUID dependentTaskId) {
        if (!taskDependencyRepository.existsByBlockingTask_IdAndDependentTask_Id(blockingTaskId, dependentTaskId)) {
            throw new ResourceNotFoundException("TaskDependency", "blockingTaskId", blockingTaskId);
        }

        taskDependencyRepository.deleteByBlockingTask_IdAndDependentTask_Id(blockingTaskId, dependentTaskId);
        log.info("Removed dependency: Task ID {} no longer blocked by Task ID {}", dependentTaskId, blockingTaskId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskActivityLogResponseDto> getTaskActivityLogs(UUID taskId) {
        return taskActivityLogRepository.findTop50ByTask_IdOrderByCreatedAtDesc(taskId)
                .stream()
                .map(TaskActivityLogResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    private void validateStatusTransition(TaskStatus current, TaskStatus target) {
        boolean valid = switch (current) {
            case BACKLOG -> target == TaskStatus.TODO || target == TaskStatus.ARCHIVED;
            case TODO -> target == TaskStatus.IN_PROGRESS || target == TaskStatus.BLOCKED || target == TaskStatus.ARCHIVED || target == TaskStatus.BACKLOG;
            case IN_PROGRESS -> target == TaskStatus.IN_REVIEW || target == TaskStatus.BLOCKED || target == TaskStatus.TODO || target == TaskStatus.ARCHIVED;
            case IN_REVIEW -> target == TaskStatus.COMPLETED || target == TaskStatus.IN_PROGRESS || target == TaskStatus.ARCHIVED;
            case BLOCKED -> target == TaskStatus.IN_PROGRESS || target == TaskStatus.TODO || target == TaskStatus.ARCHIVED;
            case COMPLETED, ARCHIVED -> target == TaskStatus.IN_PROGRESS;
        };

        if (!valid) {
            throw new IllegalStateException(String.format("Invalid status transition from %s to %s", current, target));
        }
    }

    private void validateNoUnresolvedBlockingDependencies(UUID taskId) {
        List<TaskDependencyEntity> dependencies = taskDependencyRepository.findByDependentTask_Id(taskId);
        for (TaskDependencyEntity dep : dependencies) {
            if (dep.getBlockingTask().getStatus() != TaskStatus.COMPLETED) {
                throw new IllegalStateException(String.format("Cannot transition task because blocking task '%s' (ID: %s) is in status %s",
                        dep.getBlockingTask().getTitle(), dep.getBlockingTask().getId(), dep.getBlockingTask().getStatus()));
            }
        }
    }

    private void detectDependencyCycle(UUID blockingTaskId, UUID dependentTaskId) {
        Set<UUID> visited = new HashSet<>();
        Queue<UUID> queue = new LinkedList<>();
        queue.add(blockingTaskId);

        while (!queue.isEmpty()) {
            UUID current = queue.poll();
            if (current.equals(dependentTaskId)) {
                throw new IllegalArgumentException("Circular dependency detected! Adding this dependency would create a cycle.");
            }
            visited.add(current);

            List<TaskDependencyEntity> parents = taskDependencyRepository.findByDependentTask_Id(current);
            for (TaskDependencyEntity parentDep : parents) {
                UUID nextBlockingId = parentDep.getBlockingTask().getId();
                if (!visited.contains(nextBlockingId)) {
                    queue.add(nextBlockingId);
                }
            }
        }
    }

    private void logActivity(TaskEntity task, UserEntity user, String activityType, String oldValue, String newValue) {
        TaskActivityLogEntity logEntry = TaskActivityLogEntity.builder()
                .task(task)
                .actor(user)
                .action(activityType)
                .oldValue(oldValue)
                .newValue(newValue)
                .build();
        taskActivityLogRepository.save(logEntry);
    }
}
