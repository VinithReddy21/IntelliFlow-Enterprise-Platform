package com.intelliflow.modules.task.dto;

import com.intelliflow.modules.task.domain.TaskStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for updating task state machine status.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateTaskStatusRequestDto {

    @NotNull(message = "Status is required")
    private TaskStatus status;

    private String blockerReason;
}
