package com.intelliflow.modules.task.dto;

import com.intelliflow.modules.task.domain.TaskActivityLogEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Data Transfer Object projecting audit trail activity log records.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskActivityLogResponseDto {

    private UUID id;
    private UUID taskId;
    private UUID actorId;
    private String actorName;
    private String action;
    private String oldValue;
    private String newValue;
    private Instant createdAt;

    public static TaskActivityLogResponseDto fromEntity(TaskActivityLogEntity entity) {
        if (entity == null) {
            return null;
        }

        String actorFullName = null;
        UUID actorUuid = null;
        if (entity.getActor() != null) {
            actorUuid = entity.getActor().getId();
            actorFullName = entity.getActor().getFirstName() + " " + entity.getActor().getLastName();
        }

        UUID taskUuid = entity.getTask() != null ? entity.getTask().getId() : null;

        return TaskActivityLogResponseDto.builder()
                .id(entity.getId())
                .taskId(taskUuid)
                .actorId(actorUuid)
                .actorName(actorFullName)
                .action(entity.getAction())
                .oldValue(entity.getOldValue())
                .newValue(entity.getNewValue())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
