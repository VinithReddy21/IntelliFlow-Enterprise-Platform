package com.intelliflow.modules.document.service;

import com.intelliflow.modules.document.dto.DocumentDetailResponseDto;
import com.intelliflow.modules.document.dto.DocumentResponseDto;
import com.intelliflow.modules.document.dto.UploadDocumentRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.InputStream;
import java.util.UUID;

/**
 * Enterprise Document Service Interface Contract.
 * 
 * Manages document uploads, ingestion workflow state machine (UPLOADED -> PARSING -> CHUNKED),
 * chunk cascade persistence, and retrieval projections.
 */
public interface DocumentService {

    DocumentResponseDto uploadAndProcessDocument(InputStream inputStream, String originalFilename, String contentType, long sizeBytes, UploadDocumentRequestDto requestDto, UUID uploaderId);

    DocumentResponseDto getDocumentById(UUID id);

    DocumentDetailResponseDto getDocumentDetails(UUID id);

    Page<DocumentResponseDto> getDocumentsByDepartment(UUID departmentId, Pageable pageable);

    Page<DocumentResponseDto> getDocumentsByUploader(UUID uploaderId, Pageable pageable);

    void softDeleteDocument(UUID id, UUID currentUserId);

    InputStream downloadDocumentFile(UUID id);
}
