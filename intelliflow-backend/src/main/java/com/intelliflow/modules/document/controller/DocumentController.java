package com.intelliflow.modules.document.controller;

import com.intelliflow.common.response.ApiResponse;
import com.intelliflow.modules.document.dto.*;
import com.intelliflow.modules.document.service.DocumentService;
import com.intelliflow.modules.document.service.ai.RagSearchEngineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

/**
 * Enterprise Document Management & AI Knowledge Engine REST API Controller.
 * 
 * Provides multipart file ingestion, streaming downloads, document metadata queries,
 * pgvector similarity search, and grounded AI RAG answer generation.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
@Tag(name = "Document Management & AI Engine", description = "Enterprise Document Ingestion, Object Storage, Vector Search, and RAG Knowledge APIs")
public class DocumentController {

    private final DocumentService documentService;
    private final RagSearchEngineService ragSearchEngineService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Upload and process a document", description = "Uploads a binary document file, calculates single-pass SHA-256, parses text with Apache Tika, and chunks text into sliding window tokens")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Document successfully uploaded and queued for processing"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid file payload or metadata validation failed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized JWT authentication")
    })
    public ResponseEntity<ApiResponse<DocumentResponseDto>> uploadDocument(
            @Parameter(description = "Binary file payload", required = true)
            @RequestPart("file") MultipartFile file,
            @Parameter(description = "Upload metadata JSON", required = true)
            @Valid @RequestPart("data") UploadDocumentRequestDto requestDto,
            Authentication authentication) {

        UUID currentUserId = extractUserId(authentication);
        log.info("REST request to upload document file: {} by User ID: {}", file.getOriginalFilename(), currentUserId);

        try (InputStream inputStream = file.getInputStream()) {
            DocumentResponseDto response = documentService.uploadAndProcessDocument(
                    inputStream,
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getSize(),
                    requestDto,
                    currentUserId
            );

            // Queue asynchronous vector embedding phase for CHUNKED document
            if (response.getStatus() == com.intelliflow.modules.document.domain.DocumentStatus.CHUNKED) {
                ragSearchEngineService.processDocumentEmbeddings(response.getId());
            }

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(response, "Document uploaded and processing pipeline initiated successfully"));
        } catch (Exception e) {
            log.error("Failed to read upload file input stream for file: {}", file.getOriginalFilename(), e);
            throw new com.intelliflow.modules.document.storage.exception.StorageException("Could not process uploaded file payload", e);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get document metadata by ID")
    public ResponseEntity<ApiResponse<DocumentResponseDto>> getDocumentById(@PathVariable UUID id) {
        DocumentResponseDto response = documentService.getDocumentById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Document metadata retrieved successfully"));
    }

    @GetMapping("/{id}/details")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get detailed document information with constituent text chunk snippets")
    public ResponseEntity<ApiResponse<DocumentDetailResponseDto>> getDocumentDetails(@PathVariable UUID id) {
        DocumentDetailResponseDto response = documentService.getDocumentDetails(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Document details and chunk projections retrieved successfully"));
    }

    @GetMapping("/{id}/download")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Download raw document file stream")
    public ResponseEntity<Resource> downloadDocument(@PathVariable UUID id) {
        DocumentResponseDto document = documentService.getDocumentById(id);
        InputStream inputStream = documentService.downloadDocumentFile(id);

        InputStreamResource resource = new InputStreamResource(inputStream);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getTitle() + "\"")
                .contentType(MediaType.parseMediaType(document.getMimeType()))
                .contentLength(document.getFileSizeBytes())
                .body(resource);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "List documents with department filtering and pagination")
    public ResponseEntity<ApiResponse<Page<DocumentResponseDto>>> getDocuments(
            @RequestParam(required = false) UUID departmentId,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<DocumentResponseDto> page = departmentId != null
                ? documentService.getDocumentsByDepartment(departmentId, pageable)
                : documentService.getDocumentsByDepartment(null, pageable);

        return ResponseEntity.ok(ApiResponse.success(page, "Documents page retrieved successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Soft delete a document by ID")
    public ResponseEntity<ApiResponse<Void>> softDeleteDocument(
            @PathVariable UUID id,
            Authentication authentication) {

        UUID currentUserId = extractUserId(authentication);
        documentService.softDeleteDocument(id, currentUserId);
        return ResponseEntity.ok(ApiResponse.success(null, "Document soft-deleted successfully"));
    }

    @PostMapping("/search/similarity")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Perform PostgreSQL pgvector similarity search", description = "Executes native HNSW cosine similarity search over text chunk embeddings")
    public ResponseEntity<ApiResponse<SimilaritySearchResponseDto>> searchSimilarChunks(
            @Valid @RequestBody SimilaritySearchRequestDto requestDto) {

        SimilaritySearchResponseDto response = ragSearchEngineService.searchSimilarChunks(requestDto);
        return ResponseEntity.ok(ApiResponse.success(response, "Vector similarity search executed successfully"));
    }

    @PostMapping("/search/rag")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Execute grounded RAG prompt query", description = "Retrieves relevant top-K vector chunks and synthesizes an answer grounded in corporate documents with explicit source citations")
    public ResponseEntity<ApiResponse<RagResponseDto>> executeRagQuery(
            @Valid @RequestBody RagQueryRequestDto requestDto) {

        RagResponseDto response = ragSearchEngineService.executeRagQuery(requestDto);
        return ResponseEntity.ok(ApiResponse.success(response, "RAG query executed successfully"));
    }

    private UUID extractUserId(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return UUID.fromString("00000000-0000-0000-0000-000000000001");
        }
        try {
            return UUID.fromString(authentication.getName());
        } catch (IllegalArgumentException e) {
            return UUID.nameUUIDFromBytes(authentication.getName().getBytes());
        }
    }
}
