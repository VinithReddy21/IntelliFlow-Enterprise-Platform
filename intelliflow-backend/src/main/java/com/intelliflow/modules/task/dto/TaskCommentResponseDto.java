package com.intelliflow.modules.task.dto;

import com.intelliflow.modules.task.domain.TaskCommentEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Data Transfer Object projecting task comment thread entries.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskCommentResponseDto {

    private UUID id;
    private UUID taskId;
    private UUID authorId;
    private String authorName;
    private String content;
    private Instant createdAt;
    private Instant updatedAt;

    public static TaskCommentResponseDto fromEntity(TaskCommentEntity entity) {
        if (entity == null) {
            return null;
        }

        String authorFullName = null;
        UUID authorUuid = null;
        if (entity.getAuthor() != null) {
            authorUuid = entity.getAuthor().getId();
            authorFullName = entity.getAuthor().getFirstName() + " " + entity.getAuthor().getLastName();
        }

        UUID taskUuid = entity.getTask() != null ? entity.getTask().getId() : null;

        return TaskCommentResponseDto.builder()
                .id(entity.getId())
                .taskId(taskUuid)
                .authorId(authorUuid)
                .authorName(authorFullName)
                .content(entity.getContent())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
