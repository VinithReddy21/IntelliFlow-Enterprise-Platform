package com.intelliflow.modules.document.service.ai;

import com.intelliflow.modules.document.dto.RagQueryRequestDto;
import com.intelliflow.modules.document.dto.RagResponseDto;
import com.intelliflow.modules.document.dto.SimilaritySearchRequestDto;
import com.intelliflow.modules.document.dto.SimilaritySearchResponseDto;

import java.util.UUID;

/**
 * RAG & AI Vector Search Engine Service Contract.
 * 
 * Orchestrates native pgvector similarity queries, department ABAC filtering,
 * grounded prompt context assembly, and asynchronous embedding persistence.
 */
public interface RagSearchEngineService {

    SimilaritySearchResponseDto searchSimilarChunks(SimilaritySearchRequestDto requestDto);

    RagResponseDto executeRagQuery(RagQueryRequestDto requestDto);

    void processDocumentEmbeddings(UUID documentId);
}
