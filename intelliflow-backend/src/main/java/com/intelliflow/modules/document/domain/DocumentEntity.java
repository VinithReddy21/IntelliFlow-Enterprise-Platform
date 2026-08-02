package com.intelliflow.modules.document.domain;

import com.intelliflow.modules.user.domain.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * DDD Aggregate Root representing an Enterprise Document.
 * 
 * Manages document metadata, object store file references, ingestion status lifecycle,
 * and cascade-managed text chunks for RAG vector search.
 */
@Entity
@Table(name = "documents", indexes = {
        @Index(name = "idx_documents_file_key", columnList = "file_key", unique = true),
        @Index(name = "idx_documents_uploader_id", columnList = "uploader_id"),
        @Index(name = "idx_documents_department_id", columnList = "department_id"),
        @Index(name = "idx_documents_entity", columnList = "entity_type, entity_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "file_key", nullable = false, unique = true, length = 512)
    private String fileKey;

    @Column(name = "mime_type", nullable = false, length = 100)
    private String mimeType;

    @Column(name = "file_size_bytes", nullable = false)
    private long fileSizeBytes;

    @Column(name = "checksum_sha256", nullable = false, length = 64)
    private String checksumSha256;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    @Builder.Default
    private DocumentStatus status = DocumentStatus.UPLOADED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploader_id", nullable = false)
    private UserEntity uploader;

    @Column(name = "department_id")
    private UUID departmentId;

    @Column(name = "entity_type", length = 50)
    private String entityType;

    @Column(name = "entity_id")
    private UUID entityId;

    @Version
    private Long version;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DocumentChunkEntity> chunks = new ArrayList<>();

    public void addChunk(DocumentChunkEntity chunk) {
        chunks.add(chunk);
        chunk.setDocument(this);
    }

    public void removeChunk(DocumentChunkEntity chunk) {
        chunks.remove(chunk);
        chunk.setDocument(null);
    }
}
