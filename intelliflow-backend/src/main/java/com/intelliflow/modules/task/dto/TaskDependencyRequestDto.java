package com.intelliflow.modules.task.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Data Transfer Object for creating DAG task dependency links.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDependencyRequestDto {

    @NotNull(message = "Blocking task ID is required")
    private UUID blockingTaskId;

    @NotNull(message = "Dependent task ID is required")
    private UUID dependentTaskId;
}
