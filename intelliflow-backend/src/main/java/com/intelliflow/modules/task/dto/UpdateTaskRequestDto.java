package com.intelliflow.modules.task.dto;

import com.intelliflow.modules.task.domain.TaskPriority;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Data Transfer Object for updating task attributes.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateTaskRequestDto {

    @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
    private String title;

    private String description;

    private TaskPriority priority;

    private Instant dueDate;

    @DecimalMin(value = "0.0", message = "Estimated hours cannot be negative")
    private BigDecimal estimatedHours;

    @DecimalMin(value = "0.0", message = "Actual hours cannot be negative")
    private BigDecimal actualHours;
}
