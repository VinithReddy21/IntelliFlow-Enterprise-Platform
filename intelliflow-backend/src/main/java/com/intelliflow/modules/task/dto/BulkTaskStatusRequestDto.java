package com.intelliflow.modules.task.dto;

import com.intelliflow.modules.task.domain.TaskStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Data Transfer Object for bulk task status updates.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkTaskStatusRequestDto {

    @NotEmpty(message = "Task IDs list cannot be empty")
    private List<UUID> taskIds;

    @NotNull(message = "Status is required")
    private TaskStatus status;
}
