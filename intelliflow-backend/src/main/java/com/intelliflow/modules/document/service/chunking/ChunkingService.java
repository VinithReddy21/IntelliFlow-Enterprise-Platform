package com.intelliflow.modules.document.service.chunking;

import java.util.List;

/**
 * Text Chunking Engine Contract.
 * 
 * Splits raw extracted document text into semantic chunks for vector embedding generation.
 */
public interface ChunkingService {

    /**
     * Splits full text into sliding-window text chunks.
     */
    List<TextChunkResult> chunkText(String fullText, int chunkSizeTokens, int chunkOverlapTokens);

    record TextChunkResult(
            int chunkIndex,
            String content,
            int tokenCount
    ) {}
}
