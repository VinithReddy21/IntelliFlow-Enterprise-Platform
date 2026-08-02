package com.intelliflow.modules.document.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intelliflow.modules.document.domain.DocumentStatus;
import com.intelliflow.modules.document.dto.*;
import com.intelliflow.modules.document.service.DocumentService;
import com.intelliflow.modules.document.service.ai.RagSearchEngineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class DocumentControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private DocumentService documentService;

    @Mock
    private RagSearchEngineService ragSearchEngineService;

    @InjectMocks
    private DocumentController documentController;

    private UUID userId;
    private UUID documentId;
    private DocumentResponseDto mockResponseDto;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(documentController).build();
        objectMapper = new ObjectMapper();

        userId = UUID.randomUUID();
        documentId = UUID.randomUUID();

        authentication = new UsernamePasswordAuthenticationToken(
                userId.toString(), null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        mockResponseDto = DocumentResponseDto.builder()
                .id(documentId)
                .title("Architecture Handbook")
                .fileKey("key_123.txt")
                .mimeType("text/plain")
                .fileSizeBytes(100)
                .checksumSha256("sha_123")
                .status(DocumentStatus.CHUNKED)
                .build();
    }

    @Test
    @DisplayName("uploadDocument - Should return 201 Created on valid file upload")
    void uploadDocument_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Hello World".getBytes());
        UploadDocumentRequestDto requestDto = UploadDocumentRequestDto.builder()
                .title("Architecture Handbook")
                .build();
        MockMultipartFile data = new MockMultipartFile("data", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(requestDto));

        when(documentService.uploadAndProcessDocument(any(), anyString(), anyString(), anyLong(), any(), any())).thenReturn(mockResponseDto);

        mockMvc.perform(multipart("/api/v1/documents")
                        .file(file)
                        .file(data)
                        .principal(authentication))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(documentId.toString()))
                .andExpect(jsonPath("$.data.title").value("Architecture Handbook"));

        verify(ragSearchEngineService).processDocumentEmbeddings(documentId);
    }

    @Test
    @DisplayName("getDocumentById - Should return 200 OK with document metadata")
    void getDocumentById_Success() throws Exception {
        when(documentService.getDocumentById(documentId)).thenReturn(mockResponseDto);

        mockMvc.perform(get("/api/v1/documents/{id}", documentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.title").value("Architecture Handbook"));
    }

    @Test
    @DisplayName("downloadDocument - Should stream raw file contents with valid disposition header")
    void downloadDocument_Success() throws Exception {
        when(documentService.getDocumentById(documentId)).thenReturn(mockResponseDto);
        when(documentService.downloadDocumentFile(documentId)).thenReturn(new ByteArrayInputStream("File Stream Content".getBytes()));

        mockMvc.perform(get("/api/v1/documents/{id}/download", documentId))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"Architecture Handbook\""))
                .andExpect(content().string("File Stream Content"));
    }

    @Test
    @DisplayName("searchSimilarChunks - Should return 200 OK with vector similarity results")
    void searchSimilarChunks_Success() throws Exception {
        SimilaritySearchRequestDto searchRequest = SimilaritySearchRequestDto.builder()
                .query("Vector Search Query")
                .topK(5)
                .build();

        SimilaritySearchResponseDto searchResponse = SimilaritySearchResponseDto.builder()
                .query("Vector Search Query")
                .totalMatches(1)
                .results(List.of(
                        SimilaritySearchResponseDto.VectorChunkResultDto.builder()
                                .chunkId(UUID.randomUUID())
                                .documentId(documentId)
                                .documentTitle("Architecture Handbook")
                                .chunkIndex(0)
                                .content("Matched Content")
                                .similarityScore(0.92f)
                                .build()
                ))
                .build();

        when(ragSearchEngineService.searchSimilarChunks(any())).thenReturn(searchResponse);

        mockMvc.perform(post("/api/v1/documents/search/similarity")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.totalMatches").value(1))
                .andExpect(jsonPath("$.data.results[0].documentTitle").value("Architecture Handbook"));
    }

    @Test
    @DisplayName("executeRagQuery - Should return 200 OK with grounded answer and citations")
    void executeRagQuery_Success() throws Exception {
        RagQueryRequestDto ragRequest = RagQueryRequestDto.builder()
                .prompt("What is the architecture?")
                .maxSourceChunks(3)
                .includeCitations(true)
                .build();

        RagResponseDto ragResponse = RagResponseDto.builder()
                .query("What is the architecture?")
                .generatedAnswer("The system uses clean architecture.")
                .retrievedChunkCount(1)
                .citations(List.of(
                        RagResponseDto.SourceCitationDto.builder()
                                .documentId(documentId)
                                .documentTitle("Architecture Handbook")
                                .chunkIndex(0)
                                .excerptSnippet("Clean architecture...")
                                .similarityScore(0.95f)
                                .build()
                ))
                .build();

        when(ragSearchEngineService.executeRagQuery(any())).thenReturn(ragResponse);

        mockMvc.perform(post("/api/v1/documents/search/rag")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ragRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.generatedAnswer").value("The system uses clean architecture."))
                .andExpect(jsonPath("$.data.citations[0].documentTitle").value("Architecture Handbook"));
    }
}
