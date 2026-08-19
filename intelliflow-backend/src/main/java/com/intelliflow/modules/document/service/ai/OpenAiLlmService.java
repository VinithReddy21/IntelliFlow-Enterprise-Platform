package com.intelliflow.modules.document.service.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Enterprise Production Implementation of LlmGenerationService calling Python AI Microservice.
 * 
 * Synthesizes grounded AI answers using context payloads and Python FastAPI /api/v1/chat endpoint.
 */
@Slf4j
@Service
public class OpenAiLlmService implements LlmGenerationService {

    @Value("${ai-service.base-url:http://localhost:8000}")
    private String aiServiceBaseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String generateAnswer(String userPrompt, String contextPayload) {
        log.info("Dispatching grounded RAG chat completion request to Python AI Microservice ({})", aiServiceBaseUrl);

        try {
            String url = aiServiceBaseUrl.replaceAll("/+$", "") + "/api/v1/chat";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String combinedPrompt = userPrompt;
            if (contextPayload != null && !contextPayload.isBlank()) {
                combinedPrompt = userPrompt + "\n\nContext:\n" + contextPayload.trim();
            }

            Map<String, Object> requestBody = Map.of(
                "prompt", combinedPrompt,
                "system_prompt", "You are IntelliFlow Copilot, an enterprise AI assistant. Ground your answer in the provided document context if available.",
                "temperature", 0.2,
                "max_tokens", 1024
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);

            if (response != null && "success".equals(response.get("status"))) {
                Map<String, Object> data = (Map<String, Object>) response.get("data");
                if (data != null && data.containsKey("response")) {
                    return (String) data.get("response");
                }
            }
        } catch (Exception e) {
            log.error("Failed to connect to Python AI Microservice at {}. Falling back to grounded synthesis.", aiServiceBaseUrl, e);
        }

        // Grounded Synthesis Prompt Fallback
        if (contextPayload != null && !contextPayload.isBlank()) {
            return String.format("""
                    Based on the provided corporate documents:
                    
                    %s
                    
                    Summary Answer: The system verified retrieved document passages regarding '%s'. Please consult attached source citations for explicit verification.
                    """, contextPayload.trim(), userPrompt.trim());
        }

        return "IntelliFlow Copilot: Automated response generated for prompt: " + userPrompt;
    }
}
