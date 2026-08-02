package com.intelliflow.modules.document.service;

import com.intelliflow.modules.document.domain.DocumentEntity;
import com.intelliflow.modules.document.domain.DocumentStatus;
import com.intelliflow.modules.document.dto.DocumentResponseDto;
import com.intelliflow.modules.document.dto.UploadDocumentRequestDto;
import com.intelliflow.modules.document.repository.DocumentChunkRepository;
import com.intelliflow.modules.document.repository.DocumentRepository;
import com.intelliflow.modules.document.service.chunking.ChunkingService;
import com.intelliflow.modules.document.service.parsing.TextParsingService;
import com.intelliflow.modules.document.storage.service.FileStorageService;
import com.intelliflow.modules.user.domain.UserEntity;
import com.intelliflow.modules.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceImplTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private DocumentChunkRepository documentChunkRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private TextParsingService textParsingService;

    @Mock
    private ChunkingService chunkingService;

    @InjectMocks
    private DocumentServiceImpl documentService;

    private UserEntity mockUploader;
    private UUID uploaderId;
    private UUID documentId;

    @BeforeEach
    void setUp() {
        uploaderId = UUID.randomUUID();
        documentId = UUID.randomUUID();

        mockUploader = UserEntity.builder()
                .id(uploaderId)
                .email("uploader@intelliflow.com")
                .firstName("Doc")
                .lastName("Uploader")
                .build();
    }

    @Test
    @DisplayName("uploadAndProcessDocument - Should successfully upload, parse, chunk, and transition state to CHUNKED")
    void uploadAndProcessDocument_Success() {
        byte[] bytes = "Enterprise AI Document Processing Context".getBytes(StandardCharsets.UTF_8);
        InputStream inputStream = new ByteArrayInputStream(bytes);

        UploadDocumentRequestDto requestDto = UploadDocumentRequestDto.builder()
                .title("Architecture Handbook")
                .build();

        FileStorageService.StorageResult storageResult = new FileStorageService.StorageResult(
                "file_key_123.txt", "checksum_sha256_abc", bytes.length, "text/plain");

        when(userRepository.findById(uploaderId)).thenReturn(Optional.of(mockUploader));
        when(fileStorageService.storeFile(any(), anyString(), anyString(), anyLong())).thenReturn(storageResult);
        when(documentRepository.findByChecksumSha256AndDeletedAtIsNull("checksum_sha256_abc")).thenReturn(Optional.empty());

        DocumentEntity mockSavedDoc = DocumentEntity.builder()
                .id(documentId)
                .title("Architecture Handbook")
                .fileKey("file_key_123.txt")
                .mimeType("text/plain")
                .checksumSha256("checksum_sha256_abc")
                .status(DocumentStatus.UPLOADED)
                .uploader(mockUploader)
                .build();

        when(documentRepository.save(any(DocumentEntity.class))).thenAnswer(invocation -> {
            DocumentEntity arg = invocation.getArgument(0);
            arg.setId(documentId);
            return arg;
        });

        when(fileStorageService.loadFileAsInputStream("file_key_123.txt")).thenReturn(new ByteArrayInputStream(bytes));
        when(textParsingService.parseText(any(), eq("text/plain"))).thenReturn("Enterprise AI Document Processing Context");

        when(chunkingService.chunkText(anyString(), eq(500), eq(50))).thenReturn(List.of(
                new ChunkingService.TextChunkResult(0, "Enterprise AI Document Processing Context", 6)
        ));

        DocumentResponseDto responseDto = documentService.uploadAndProcessDocument(inputStream, "doc.txt", "text/plain", bytes.length, requestDto, uploaderId);

        assertNotNull(responseDto);
        assertEquals(documentId, responseDto.getId());
        assertEquals(DocumentStatus.CHUNKED, responseDto.getStatus());
        verify(documentRepository, times(2)).save(any(DocumentEntity.class));
    }

    @Test
    @DisplayName("uploadAndProcessDocument - Should return existing document response when SHA-256 duplicate exists")
    void uploadAndProcessDocument_DuplicateFound_ReturnsExisting() {
        byte[] bytes = "Duplicate Content".getBytes(StandardCharsets.UTF_8);
        InputStream inputStream = new ByteArrayInputStream(bytes);

        UploadDocumentRequestDto requestDto = UploadDocumentRequestDto.builder()
                .title("Duplicate Doc")
                .build();

        FileStorageService.StorageResult storageResult = new FileStorageService.StorageResult(
                "file_key_duplicate.txt", "existing_checksum_123", bytes.length, "text/plain");

        DocumentEntity existingDoc = DocumentEntity.builder()
                .id(documentId)
                .title("Existing Original Doc")
                .fileKey("existing_key.txt")
                .checksumSha256("existing_checksum_123")
                .status(DocumentStatus.CHUNKED)
                .uploader(mockUploader)
                .build();

        when(userRepository.findById(uploaderId)).thenReturn(Optional.of(mockUploader));
        when(fileStorageService.storeFile(any(), anyString(), anyString(), anyLong())).thenReturn(storageResult);
        when(documentRepository.findByChecksumSha256AndDeletedAtIsNull("existing_checksum_123")).thenReturn(Optional.of(existingDoc));

        DocumentResponseDto responseDto = documentService.uploadAndProcessDocument(inputStream, "dup.txt", "text/plain", bytes.length, requestDto, uploaderId);

        assertNotNull(responseDto);
        assertEquals(documentId, responseDto.getId());
        assertEquals("Existing Original Doc", responseDto.getTitle());
        verify(fileStorageService).deleteFile("file_key_duplicate.txt");
    }
}
