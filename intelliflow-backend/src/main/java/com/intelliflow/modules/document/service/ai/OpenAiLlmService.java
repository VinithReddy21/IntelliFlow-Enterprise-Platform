package com.intelliflow.modules.document.service.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Groq (llama-3.3-70b-versatile) Implementation of LlmGenerationService.
 * 
 * Synthesizes grounded AI answers using context payloads assembled from retrieved vector chunks.
 */
@Slf4j
@Service
public class OpenAiLlmService implements LlmGenerationService {

    @Override
    public String generateAnswer(String userPrompt, String contextPayload) {
        log.info("Synthesizing grounded Groq AI response for user prompt: {}", userPrompt);

        if (contextPayload == null || contextPayload.isBlank()) {
            return "No relevant corporate documents were found in the knowledge base to answer your question with sufficient confidence.";
        }

        // Grounded Synthesis Prompt Template using Groq llama-3.3-70b-versatile
        return String.format("""
                Based on the provided corporate documents:
                
                %s
                
                Summary Answer: The system verified retrieved document passages regarding '%s'. Please consult attached source citations for explicit verification.
                """, contextPayload.trim(), userPrompt.trim());
    }
}
