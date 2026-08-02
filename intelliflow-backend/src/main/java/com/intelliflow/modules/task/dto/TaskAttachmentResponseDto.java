package com.intelliflow.modules.task.dto;

import com.intelliflow.modules.task.domain.TaskAttachmentEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Data Transfer Object projecting file attachment details.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskAttachmentResponseDto {

    private UUID id;
    private String entityType;
    private UUID entityId;
    private UUID uploaderId;
    private String uploaderName;
    private String fileName;
    private String fileType;
    private Long fileSizeBytes;
    private String storageUrl;
    private Instant createdAt;

    public static TaskAttachmentResponseDto fromEntity(TaskAttachmentEntity entity) {
        if (entity == null) {
            return null;
        }

        String uploaderFullName = null;
        UUID uploaderUuid = null;
        if (entity.getUploader() != null) {
            uploaderUuid = entity.getUploader().getId();
            uploaderFullName = entity.getUploader().getFirstName() + " " + entity.getUploader().getLastName();
        }

        return TaskAttachmentResponseDto.builder()
                .id(entity.getId())
                .entityType(entity.getEntityType())
                .entityId(entity.getEntityId())
                .uploaderId(uploaderUuid)
                .uploaderName(uploaderFullName)
                .fileName(entity.getFileName())
                .fileType(entity.getFileType())
                .fileSizeBytes(entity.getFileSizeBytes())
                .storageUrl(entity.getStorageUrl())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
