package com.intelliflow.modules.task.service;

import com.intelliflow.common.exception.ResourceNotFoundException;
import com.intelliflow.modules.task.domain.*;
import com.intelliflow.modules.task.dto.*;
import com.intelliflow.modules.task.repository.*;
import com.intelliflow.modules.user.domain.RoleEnum;
import com.intelliflow.modules.user.domain.UserEntity;
import com.intelliflow.modules.user.domain.UserStatus;
import com.intelliflow.modules.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskCommentRepository taskCommentRepository;

    @Mock
    private TaskActivityLogRepository taskActivityLogRepository;

    @Mock
    private TaskDependencyRepository taskDependencyRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    private UserEntity creator;
    private TaskEntity activeTask;
    private UUID userId;
    private UUID taskId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        taskId = UUID.randomUUID();

        creator = UserEntity.builder()
                .id(userId)
                .email("creator@intelliflow.com")
                .firstName("Task")
                .lastName("Creator")
                .role(RoleEnum.ROLE_EMPLOYEE)
                .status(UserStatus.ACTIVE)
                .build();

        activeTask = TaskEntity.builder()
                .id(taskId)
                .title("Test Task")
                .description("Test Task Description")
                .status(TaskStatus.BACKLOG)
                .priority(TaskPriority.MEDIUM)
                .creator(creator)
                .build();
    }

    @Test
    @DisplayName("createTask - Should create task successfully and log activity")
    void createTask_Success() {
        CreateTaskRequestDto request = CreateTaskRequestDto.builder()
                .title("New Feature Task")
                .description("Build feature")
                .priority(TaskPriority.HIGH)
                .estimatedHours(new BigDecimal("10.5"))
                .build();

        when(userRepository.findByIdAndDeletedAtIsNull(userId)).thenReturn(Optional.of(creator));
        when(taskRepository.save(any(TaskEntity.class))).thenAnswer(invocation -> {
            TaskEntity entity = invocation.getArgument(0);
            entity.setId(taskId);
            return entity;
        });

        TaskResponseDto response = taskService.createTask(userId, request);

        assertNotNull(response);
        assertEquals("New Feature Task", response.getTitle());
        assertEquals(TaskPriority.HIGH, response.getPriority());
        verify(taskRepository, times(1)).save(any(TaskEntity.class));
        verify(taskActivityLogRepository, times(1)).save(any(TaskActivityLogEntity.class));
    }

    @Test
    @DisplayName("getTaskById - Should return task details with subtasks and comment count")
    void getTaskById_Success() {
        when(taskRepository.findByIdAndDeletedAtIsNull(taskId)).thenReturn(Optional.of(activeTask));
        when(taskRepository.findByParentTask_IdAndDeletedAtIsNull(taskId)).thenReturn(Collections.emptyList());
        when(taskCommentRepository.countByTask_IdAndDeletedAtIsNull(taskId)).thenReturn(3L);

        TaskDetailResponseDto response = taskService.getTaskById(taskId);

        assertNotNull(response);
        assertEquals("Test Task", response.getTask().getTitle());
        assertEquals(3L, response.getCommentCount());
    }

    @Test
    @DisplayName("updateTaskStatus - Should transition status validly and set startedAt")
    void updateTaskStatus_ValidTransition() {
        UpdateTaskStatusRequestDto request = new UpdateTaskStatusRequestDto(TaskStatus.TODO, null);
        when(taskRepository.findByIdAndDeletedAtIsNull(taskId)).thenReturn(Optional.of(activeTask));
        when(taskRepository.save(any(TaskEntity.class))).thenReturn(activeTask);

        TaskResponseDto response = taskService.updateTaskStatus(taskId, request);

        assertNotNull(response);
        assertEquals(TaskStatus.TODO, activeTask.getStatus());
        verify(taskActivityLogRepository, times(1)).save(any(TaskActivityLogEntity.class));
    }

    @Test
    @DisplayName("updateTaskStatus - Should throw IllegalStateException on invalid transition")
    void updateTaskStatus_InvalidTransition_ThrowsException() {
        // BACKLOG directly to COMPLETED is invalid
        UpdateTaskStatusRequestDto request = new UpdateTaskStatusRequestDto(TaskStatus.COMPLETED, null);
        when(taskRepository.findByIdAndDeletedAtIsNull(taskId)).thenReturn(Optional.of(activeTask));

        assertThrows(IllegalStateException.class, () -> taskService.updateTaskStatus(taskId, request));
    }

    @Test
    @DisplayName("updateTaskStatus - Should block transition if uncompleted dependencies exist")
    void updateTaskStatus_BlockedByDependency_ThrowsException() {
        activeTask.setStatus(TaskStatus.TODO);
        UpdateTaskStatusRequestDto request = new UpdateTaskStatusRequestDto(TaskStatus.IN_PROGRESS, null);

        TaskEntity blockerTask = TaskEntity.builder()
                .id(UUID.randomUUID())
                .title("Blocker Task")
                .status(TaskStatus.IN_PROGRESS)
                .build();

        TaskDependencyEntity dependency = TaskDependencyEntity.builder()
                .blockingTask(blockerTask)
                .dependentTask(activeTask)
                .build();

        when(taskRepository.findByIdAndDeletedAtIsNull(taskId)).thenReturn(Optional.of(activeTask));
        when(taskDependencyRepository.findByDependentTask_Id(taskId)).thenReturn(List.of(dependency));

        assertThrows(IllegalStateException.class, () -> taskService.updateTaskStatus(taskId, request));
    }

    @Test
    @DisplayName("addDependency - Should detect circular dependency and throw IllegalArgumentException")
    void addDependency_CircularDependency_ThrowsException() {
        UUID blockingId = UUID.randomUUID();
        UUID dependentId = taskId;

        TaskEntity blockingTask = TaskEntity.builder()
                .id(blockingId)
                .title("Blocking Task")
                .build();

        TaskDependencyRequestDto request = new TaskDependencyRequestDto(blockingId, dependentId);

        when(taskRepository.findByIdAndDeletedAtIsNull(blockingId)).thenReturn(Optional.of(blockingTask));
        when(taskRepository.findByIdAndDeletedAtIsNull(dependentId)).thenReturn(Optional.of(activeTask));
        when(taskDependencyRepository.existsByBlockingTask_IdAndDependentTask_Id(blockingId, dependentId)).thenReturn(false);

        TaskDependencyEntity reverseDep = TaskDependencyEntity.builder()
                .blockingTask(activeTask)
                .dependentTask(blockingTask)
                .build();
        lenient().when(taskDependencyRepository.findByDependentTask_Id(blockingId)).thenReturn(List.of(reverseDep));
        lenient().when(taskDependencyRepository.findByDependentTask_Id(dependentId)).thenReturn(Collections.emptyList());

        assertThrows(IllegalArgumentException.class, () -> taskService.addDependency(request));
    }
}
