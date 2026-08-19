package com.intelliflow.modules.document.service.ai;

import java.util.List;

/**
 * Vector Embedding Generation Service Contract.
 * 
 * Generates 384-dimensional dense float vector embeddings for text chunks.
 */
public interface EmbeddingGenerationService {

    /**
     * Generates a 384-dimensional embedding vector for a single text chunk.
     */
    float[] generateEmbedding(String text);

    /**
     * Batch generates 384-dimensional embedding vectors for multiple text chunks.
     */
    List<float[]> generateBatchEmbeddings(List<String> textChunks);
}
