package com.intelliflow.modules.document.service.ai;

/**
 * LLM Text Generation Service Contract.
 * 
 * Abstraction over LLM providers (OpenAI GPT-4, Anthropic Claude, LLaMA) for RAG context synthesis.
 */
public interface LlmGenerationService {

    /**
     * Synthesizes an answer grounded in retrieved document context chunks.
     */
    String generateAnswer(String userPrompt, String contextPayload);
}
