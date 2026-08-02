package com.intelliflow.modules.task.dto;

import com.intelliflow.modules.task.domain.TaskPriority;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Data Transfer Object for creating a new Task.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTaskRequestDto {

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
    private String title;

    private String description;

    private TaskPriority priority;

    private UUID departmentId;

    private UUID parentTaskId;

    @FutureOrPresent(message = "Due date must be in the present or future")
    private Instant dueDate;

    @DecimalMin(value = "0.0", message = "Estimated hours cannot be negative")
    private BigDecimal estimatedHours;
}
