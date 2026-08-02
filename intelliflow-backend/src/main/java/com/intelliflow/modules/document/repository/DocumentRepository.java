package com.intelliflow.modules.document.repository;

import com.intelliflow.modules.document.domain.DocumentEntity;
import com.intelliflow.modules.document.domain.DocumentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository interface for DocumentEntity domain aggregate root.
 */
@Repository
public interface DocumentRepository extends JpaRepository<DocumentEntity, UUID>, JpaSpecificationExecutor<DocumentEntity> {

    Optional<DocumentEntity> findByIdAndDeletedAtIsNull(UUID id);

    Optional<DocumentEntity> findByChecksumSha256AndDeletedAtIsNull(String checksumSha256);

    Page<DocumentEntity> findByUploader_IdAndDeletedAtIsNull(UUID uploaderId, Pageable pageable);

    Page<DocumentEntity> findByDepartmentIdAndDeletedAtIsNull(UUID departmentId, Pageable pageable);

    List<DocumentEntity> findByEntityTypeAndEntityIdAndDeletedAtIsNull(String entityType, UUID entityId);

    List<DocumentEntity> findByStatusAndDeletedAtIsNull(DocumentStatus status);

    @Modifying
    @Query("UPDATE DocumentEntity d SET d.status = :status, d.updatedAt = CURRENT_TIMESTAMP WHERE d.id = :id AND d.deletedAt IS NULL")
    int updateDocumentStatus(@Param("id") UUID id, @Param("status") DocumentStatus status);

    @Modifying
    @Query("UPDATE DocumentEntity d SET d.deletedAt = CURRENT_TIMESTAMP, d.updatedAt = CURRENT_TIMESTAMP WHERE d.id = :id AND d.deletedAt IS NULL")
    int softDeleteDocument(@Param("id") UUID id);

    @Modifying
    @Query("DELETE FROM DocumentEntity d WHERE d.deletedAt IS NOT NULL AND d.deletedAt < :threshold")
    int deleteSoftDeletedOlderThan(@Param("threshold") Instant threshold);

    boolean existsByChecksumSha256AndDeletedAtIsNull(String checksumSha256);
}
