package com.intelliflow.modules.document.service.ai;

import com.intelliflow.modules.document.domain.DocumentChunkEntity;
import com.intelliflow.modules.document.domain.DocumentEntity;
import com.intelliflow.modules.document.domain.DocumentStatus;
import com.intelliflow.modules.document.dto.RagQueryRequestDto;
import com.intelliflow.modules.document.dto.RagResponseDto;
import com.intelliflow.modules.document.dto.SimilaritySearchRequestDto;
import com.intelliflow.modules.document.dto.SimilaritySearchResponseDto;
import com.intelliflow.modules.document.repository.DocumentChunkRepository;
import com.intelliflow.modules.document.repository.DocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RagSearchEngineServiceImplTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private DocumentChunkRepository documentChunkRepository;

    @Mock
    private EmbeddingGenerationService embeddingGenerationService;

    @Mock
    private LlmGenerationService llmGenerationService;

    @InjectMocks
    private RagSearchEngineServiceImpl ragSearchEngineService;

    private UUID documentId;
    private DocumentEntity mockDocument;
    private DocumentChunkEntity mockChunk;

    @BeforeEach
    void setUp() {
        documentId = UUID.randomUUID();
        mockDocument = DocumentEntity.builder()
                .id(documentId)
                .title("Security Policy")
                .status(DocumentStatus.CHUNKED)
                .build();

        mockChunk = DocumentChunkEntity.builder()
                .id(UUID.randomUUID())
                .document(mockDocument)
                .chunkIndex(0)
                .content("Password must contain at least 12 characters.")
                .tokenCount(8)
                .metadata(Map.of("category", "security"))
                .build();
    }

    @Test
    @DisplayName("processDocumentEmbeddings - Should generate embeddings and transition document to ACTIVE")
    void processDocumentEmbeddings_Success() {
        when(documentRepository.findByIdAndDeletedAtIsNull(documentId)).thenReturn(Optional.of(mockDocument));
        when(documentChunkRepository.findByDocument_IdOrderByChunkIndexAsc(documentId)).thenReturn(List.of(mockChunk));
        when(embeddingGenerationService.generateEmbedding(anyString())).thenReturn(new float[1536]);

        ragSearchEngineService.processDocumentEmbeddings(documentId);

        assertEquals(DocumentStatus.ACTIVE, mockDocument.getStatus());
        verify(documentChunkRepository).saveAll(anyList());
        verify(documentRepository).save(mockDocument);
    }

    @Test
    @DisplayName("searchSimilarChunks - Should execute vector search and return filtered results")
    void searchSimilarChunks_Success() {
        SimilaritySearchRequestDto requestDto = SimilaritySearchRequestDto.builder()
                .query("Password rules")
                .topK(5)
                .minSimilarity(0.5f)
                .build();

        when(embeddingGenerationService.generateEmbedding("Password rules")).thenReturn(new float[1536]);
        when(documentChunkRepository.findSimilarChunksNative(anyString(), any(), eq(5))).thenReturn(List.of(mockChunk));

        SimilaritySearchResponseDto response = ragSearchEngineService.searchSimilarChunks(requestDto);

        assertNotNull(response);
        assertEquals(1, response.getTotalMatches());
        assertEquals("Security Policy", response.getResults().get(0).getDocumentTitle());
    }

    @Test
    @DisplayName("executeRagQuery - Should retrieve top chunks, assemble prompt context, and return grounded answer with citations")
    void executeRagQuery_Success() {
        RagQueryRequestDto requestDto = RagQueryRequestDto.builder()
                .prompt("What is the password requirement?")
                .maxSourceChunks(3)
                .includeCitations(true)
                .build();

        when(embeddingGenerationService.generateEmbedding(anyString())).thenReturn(new float[1536]);
        when(documentChunkRepository.findSimilarChunksNative(anyString(), any(), eq(3))).thenReturn(List.of(mockChunk));
        when(llmGenerationService.generateAnswer(anyString(), anyString())).thenReturn("Passwords require 12 characters.");

        RagResponseDto response = ragSearchEngineService.executeRagQuery(requestDto);

        assertNotNull(response);
        assertEquals("Passwords require 12 characters.", response.getGeneratedAnswer());
        assertEquals(1, response.getRetrievedChunkCount());
        assertEquals(1, response.getCitations().size());
        assertEquals("Security Policy", response.getCitations().get(0).getDocumentTitle());
    }
}
