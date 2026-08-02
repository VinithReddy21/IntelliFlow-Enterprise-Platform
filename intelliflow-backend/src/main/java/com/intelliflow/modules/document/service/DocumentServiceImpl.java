package com.intelliflow.modules.document.service;

import com.intelliflow.common.config.cache.CacheNames;
import com.intelliflow.common.exception.ResourceNotFoundException;
import com.intelliflow.modules.document.domain.DocumentChunkEntity;
import com.intelliflow.modules.document.domain.DocumentEntity;
import com.intelliflow.modules.document.domain.DocumentStatus;
import com.intelliflow.modules.document.dto.DocumentDetailResponseDto;
import com.intelliflow.modules.document.dto.DocumentResponseDto;
import com.intelliflow.modules.document.dto.UploadDocumentRequestDto;
import com.intelliflow.modules.document.repository.DocumentChunkRepository;
import com.intelliflow.modules.document.repository.DocumentRepository;
import com.intelliflow.modules.document.service.chunking.ChunkingService;
import com.intelliflow.modules.document.service.parsing.TextParsingService;
import com.intelliflow.modules.document.storage.service.FileStorageService;
import com.intelliflow.modules.user.domain.UserEntity;
import com.intelliflow.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.*;

/**
 * Enterprise Document Service Production Implementation.
 * 
 * Orchestrates object storage uploads, single-pass SHA-256 deduplication,
 * Apache Tika text parsing, recursive sliding-window token chunking,
 * Redis distributed caching, and Document aggregate root lifecycle updates.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentChunkRepository documentChunkRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final TextParsingService textParsingService;
    private final ChunkingService chunkingService;

    @Override
    @Transactional
    public DocumentResponseDto uploadAndProcessDocument(InputStream inputStream, String originalFilename, String contentType, long sizeBytes, UploadDocumentRequestDto requestDto, UUID uploaderId) {
        log.info("Initiating document upload and ingestion process for file: {} (Uploader ID: {})", originalFilename, uploaderId);

        UserEntity uploader = userRepository.findById(uploaderId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", uploaderId));

        FileStorageService.StorageResult storageResult = fileStorageService.storeFile(inputStream, originalFilename, contentType, sizeBytes);

        // Deduplication Check via Checksum SHA-256
        Optional<DocumentEntity> existing = documentRepository.findByChecksumSha256AndDeletedAtIsNull(storageResult.checksumSha256());
        if (existing.isPresent()) {
            log.info("Identical document with checksum {} already exists. Reusing document ID: {}", storageResult.checksumSha256(), existing.get().getId());
            fileStorageService.deleteFile(storageResult.fileKey());
            return DocumentResponseDto.fromEntity(existing.get());
        }

        DocumentEntity document = DocumentEntity.builder()
                .title(requestDto.getTitle())
                .fileKey(storageResult.fileKey())
                .mimeType(storageResult.mimeType() != null ? storageResult.mimeType() : "application/octet-stream")
                .fileSizeBytes(storageResult.fileSizeBytes())
                .checksumSha256(storageResult.checksumSha256())
                .status(DocumentStatus.UPLOADED)
                .uploader(uploader)
                .departmentId(requestDto.getDepartmentId())
                .entityType(requestDto.getEntityType())
                .entityId(requestDto.getEntityId())
                .build();

        document = documentRepository.save(document);
        log.info("Persisted DocumentEntity ID: {} with state UPLOADED", document.getId());

        processIngestionPipeline(document);

        return DocumentResponseDto.fromEntity(documentRepository.save(document));
    }

    private void processIngestionPipeline(DocumentEntity document) {
        try {
            document.setStatus(DocumentStatus.PARSING);
            log.info("Transitioned Document ID: {} to PARSING state", document.getId());

            String extractedText;
            try (InputStream fileStream = fileStorageService.loadFileAsInputStream(document.getFileKey())) {
                extractedText = textParsingService.parseText(fileStream, document.getMimeType());
            }

            if (extractedText.isBlank()) {
                log.warn("Extracted text payload is empty for Document ID: {}. Marking state as FAILED", document.getId());
                document.setStatus(DocumentStatus.FAILED);
                return;
            }

            document.setStatus(DocumentStatus.CHUNKED);
            log.info("Transitioned Document ID: {} to CHUNKED state", document.getId());

            List<ChunkingService.TextChunkResult> chunks = chunkingService.chunkText(extractedText, 500, 50);
            for (ChunkingService.TextChunkResult chunkResult : chunks) {
                DocumentChunkEntity chunkEntity = DocumentChunkEntity.builder()
                        .chunkIndex(chunkResult.chunkIndex())
                        .content(chunkResult.content())
                        .tokenCount(chunkResult.tokenCount())
                        .metadata(Map.of("source_title", document.getTitle(), "char_length", chunkResult.content().length()))
                        .build();
                document.addChunk(chunkEntity);
            }

            log.info("Successfully added {} chunks to Document ID: {}", chunks.size(), document.getId());
        } catch (Exception e) {
            log.error("Failed during document ingestion pipeline for Document ID: {}", document.getId(), e);
            document.setStatus(DocumentStatus.FAILED);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheNames.DOCUMENTS, key = "#id")
    public DocumentResponseDto getDocumentById(UUID id) {
        DocumentEntity entity = documentRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document", "id", id));
        return DocumentResponseDto.fromEntity(entity);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheNames.DOCUMENT_DETAILS, key = "#id")
    public DocumentDetailResponseDto getDocumentDetails(UUID id) {
        DocumentEntity entity = documentRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document", "id", id));

        List<DocumentChunkEntity> chunks = documentChunkRepository.findByDocument_IdOrderByChunkIndexAsc(id);
        List<DocumentDetailResponseDto.ChunkProjectionDto> chunkProjections = chunks.stream()
                .map(c -> DocumentDetailResponseDto.ChunkProjectionDto.builder()
                        .chunkId(c.getId() != null ? c.getId().toString() : null)
                        .chunkIndex(c.getChunkIndex())
                        .tokenCount(c.getTokenCount())
                        .contentSnippet(c.getContent().length() > 100 ? c.getContent().substring(0, 100) + "..." : c.getContent())
                        .build())
                .toList();

        return DocumentDetailResponseDto.fromEntity(entity, chunks.size(), chunkProjections);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DocumentResponseDto> getDocumentsByDepartment(UUID departmentId, Pageable pageable) {
        return documentRepository.findByDepartmentIdAndDeletedAtIsNull(departmentId, pageable)
                .map(DocumentResponseDto::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DocumentResponseDto> getDocumentsByUploader(UUID uploaderId, Pageable pageable) {
        return documentRepository.findByUploader_IdAndDeletedAtIsNull(uploaderId, pageable)
                .map(DocumentResponseDto::fromEntity);
    }

    @Override
    @Transactional
    @CacheEvict(value = {CacheNames.DOCUMENTS, CacheNames.DOCUMENT_DETAILS}, key = "#id")
    public void softDeleteDocument(UUID id, UUID currentUserId) {
        DocumentEntity entity = documentRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document", "id", id));

        documentRepository.softDeleteDocument(id);
        log.info("Successfully soft-deleted Document ID: {} by User ID: {}", id, currentUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public InputStream downloadDocumentFile(UUID id) {
        DocumentEntity entity = documentRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document", "id", id));

        return fileStorageService.loadFileAsInputStream(entity.getFileKey());
    }
}
