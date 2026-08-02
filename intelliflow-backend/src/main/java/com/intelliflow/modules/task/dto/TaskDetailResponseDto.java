package com.intelliflow.modules.task.dto;

import com.intelliflow.modules.task.domain.TaskEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Extended Data Transfer Object projecting task details including subtasks and comment counts.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDetailResponseDto {

    private TaskResponseDto task;
    private List<TaskResponseDto> subtasks;
    private long commentCount;

    public static TaskDetailResponseDto fromEntity(TaskEntity entity, List<TaskResponseDto> subtasks, long commentCount) {
        return TaskDetailResponseDto.builder()
                .task(TaskResponseDto.fromEntity(entity))
                .subtasks(subtasks)
                .commentCount(commentCount)
                .build();
    }
}
