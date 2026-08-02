package com.intelliflow.modules.document.repository;

import com.intelliflow.modules.document.domain.DocumentChunkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA Repository for DocumentChunkEntity.
 * 
 * Provides batch chunk retrieval and native PostgreSQL pgvector cosine similarity
 * vector search queries over 1536-dimensional embedding indexes.
 */
@Repository
public interface DocumentChunkRepository extends JpaRepository<DocumentChunkEntity, UUID> {

    List<DocumentChunkEntity> findByDocument_IdOrderByChunkIndexAsc(UUID documentId);

    @Modifying
    @Query("DELETE FROM DocumentChunkEntity c WHERE c.document.id = :documentId")
    void deleteByDocumentId(@Param("documentId") UUID documentId);

    /**
     * Native PostgreSQL pgvector Cosine Similarity Search using HNSW vector index.
     * Distance operator '<=>' measures Cosine Distance (1 - Cosine Similarity).
     */
    @Query(value = """
        SELECT c.*
        FROM document_chunks c
        JOIN documents d ON c.document_id = d.id
        WHERE d.deleted_at IS NULL
          AND d.status = 'ACTIVE'
          AND (:departmentId IS NULL OR d.department_id = :departmentId)
        ORDER BY c.embedding <=> CAST(:queryEmbedding AS vector)
        LIMIT :topK
        """, nativeQuery = true)
    List<DocumentChunkEntity> findSimilarChunksNative(
            @Param("queryEmbedding") String queryEmbedding,
            @Param("departmentId") UUID departmentId,
            @Param("topK") int topK);
}
