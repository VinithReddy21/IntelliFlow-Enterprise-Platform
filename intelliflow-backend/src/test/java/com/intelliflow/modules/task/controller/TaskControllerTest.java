package com.intelliflow.modules.task.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.intelliflow.modules.task.domain.TaskPriority;
import com.intelliflow.modules.task.domain.TaskStatus;
import com.intelliflow.modules.task.dto.*;
import com.intelliflow.modules.task.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private UUID userId;
    private UUID taskId;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        userId = UUID.randomUUID();
        taskId = UUID.randomUUID();

        authentication = new UsernamePasswordAuthenticationToken(
                userId, null, List.of(new SimpleGrantedAuthority("ROLE_EMPLOYEE")));
    }

    @Test
    @DisplayName("POST /api/v1/tasks - Should create task and return 201 CREATED")
    void createTask_ReturnsCreated() throws Exception {
        CreateTaskRequestDto request = CreateTaskRequestDto.builder()
                .title("New Feature")
                .description("Build new REST API endpoint")
                .priority(TaskPriority.HIGH)
                .estimatedHours(new BigDecimal("8.0"))
                .build();

        TaskResponseDto response = TaskResponseDto.builder()
                .id(taskId)
                .title("New Feature")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.HIGH)
                .creatorId(userId)
                .build();

        when(taskService.createTask(eq(userId), any(CreateTaskRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/tasks")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.title").value("New Feature"));
    }

    @Test
    @DisplayName("GET /api/v1/tasks/{id} - Should return task detail projection")
    void getTaskById_ReturnsOk() throws Exception {
        TaskResponseDto coreDto = TaskResponseDto.builder()
                .id(taskId)
                .title("New Feature")
                .status(TaskStatus.TODO)
                .build();

        TaskDetailResponseDto detailDto = TaskDetailResponseDto.builder()
                .task(coreDto)
                .commentCount(2L)
                .build();

        when(taskService.getTaskById(taskId)).thenReturn(detailDto);

        mockMvc.perform(get("/api/v1/tasks/{id}", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.task.title").value("New Feature"))
                .andExpect(jsonPath("$.data.commentCount").value(2));
    }

    @Test
    @DisplayName("PATCH /api/v1/tasks/{id}/status - Should update status and return 200 OK")
    void updateTaskStatus_ReturnsOk() throws Exception {
        UpdateTaskStatusRequestDto request = new UpdateTaskStatusRequestDto(TaskStatus.IN_PROGRESS, null);

        TaskResponseDto response = TaskResponseDto.builder()
                .id(taskId)
                .title("New Feature")
                .status(TaskStatus.IN_PROGRESS)
                .build();

        when(taskService.updateTaskStatus(eq(taskId), any(UpdateTaskStatusRequestDto.class))).thenReturn(response);

        mockMvc.perform(patch("/api/v1/tasks/{id}/status", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.status").value("IN_PROGRESS"));
    }

    @Test
    @DisplayName("DELETE /api/v1/tasks/{id} - Should soft delete task and return 200 OK")
    void softDeleteTask_ReturnsOk() throws Exception {
        doNothing().when(taskService).softDeleteTask(taskId);

        mockMvc.perform(delete("/api/v1/tasks/{id}", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Task soft-deleted successfully"));
    }
}
