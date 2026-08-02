package com.intelliflow.modules.document.dto;

import com.intelliflow.modules.document.domain.DocumentEntity;
import com.intelliflow.modules.document.domain.DocumentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentResponseDto {

    private UUID id;
    private String title;
    private String fileKey;
    private String mimeType;
    private long fileSizeBytes;
    private String checksumSha256;
    private DocumentStatus status;
    private UUID uploaderId;
    private String uploaderName;
    private UUID departmentId;
    private String entityType;
    private UUID entityId;
    private Long version;
    private Instant createdAt;
    private Instant updatedAt;

    public static DocumentResponseDto fromEntity(DocumentEntity entity) {
        if (entity == null) {
            return null;
        }

        UUID uploaderUuid = entity.getUploader() != null ? entity.getUploader().getId() : null;
        String uploaderFullName = entity.getUploader() != null
                ? entity.getUploader().getFirstName() + " " + entity.getUploader().getLastName()
                : null;

        return DocumentResponseDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .fileKey(entity.getFileKey())
                .mimeType(entity.getMimeType())
                .fileSizeBytes(entity.getFileSizeBytes())
                .checksumSha256(entity.getChecksumSha256())
                .status(entity.getStatus())
                .uploaderId(uploaderUuid)
                .uploaderName(uploaderFullName)
                .departmentId(entity.getDepartmentId())
                .entityType(entity.getEntityType())
                .entityId(entity.getEntityId())
                .version(entity.getVersion())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
