package com.intelliflow.modules.task.dto;

import com.intelliflow.modules.task.domain.TaskEntity;
import com.intelliflow.modules.task.domain.TaskPriority;
import com.intelliflow.modules.task.domain.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Data Transfer Object projecting core Task entity fields.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskResponseDto {

    private UUID id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private UUID creatorId;
    private String creatorName;
    private UUID departmentId;
    private UUID parentTaskId;
    private Instant dueDate;
    private Instant startedAt;
    private Instant completedAt;
    private BigDecimal estimatedHours;
    private BigDecimal actualHours;
    private boolean isRecurring;
    private String recurrenceCron;
    private Long version;
    private Instant createdAt;
    private Instant updatedAt;

    public static TaskResponseDto fromEntity(TaskEntity entity) {
        if (entity == null) {
            return null;
        }

        String creatorFullName = null;
        UUID creatorUuid = null;
        if (entity.getCreator() != null) {
            creatorUuid = entity.getCreator().getId();
            creatorFullName = entity.getCreator().getFirstName() + " " + entity.getCreator().getLastName();
        }

        UUID parentUuid = entity.getParentTask() != null ? entity.getParentTask().getId() : null;

        return TaskResponseDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .priority(entity.getPriority())
                .creatorId(creatorUuid)
                .creatorName(creatorFullName)
                .departmentId(entity.getDepartmentId())
                .parentTaskId(parentUuid)
                .dueDate(entity.getDueDate())
                .startedAt(entity.getStartedAt())
                .completedAt(entity.getCompletedAt())
                .estimatedHours(entity.getEstimatedHours())
                .actualHours(entity.getActualHours())
                .isRecurring(entity.isRecurring())
                .recurrenceCron(entity.getRecurrenceCron())
                .version(entity.getVersion())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
