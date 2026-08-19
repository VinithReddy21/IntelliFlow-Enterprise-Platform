package com.intelliflow.modules.document.service.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Sentence Transformer (all-MiniLM-L6-v2) Implementation of EmbeddingGenerationService.
 * 
 * Dispatches text payload requests to Python FastAPI AI Microservice to generate
 * 384-dimensional dense float vector embeddings.
 */
@Slf4j
@Service
public class OpenAiEmbeddingService implements EmbeddingGenerationService {

    @Value("${ai-service.base-url:http://localhost:8000}")
    private String aiServiceBaseUrl;

    private static final int VECTOR_DIMENSION = 384;
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public float[] generateEmbedding(String text) {
        if (text == null || text.isBlank()) {
            return new float[VECTOR_DIMENSION];
        }

        try {
            String url = aiServiceBaseUrl.replaceAll("/+$", "") + "/api/v1/embeddings";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestBody = Map.of("text", text);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);
            if (response != null && "success".equals(response.get("status"))) {
                Map<String, Object> data = (Map<String, Object>) response.get("data");
                if (data != null && data.containsKey("embedding")) {
                    List<Number> list = (List<Number>) data.get("embedding");
                    float[] vector = new float[list.size()];
                    for (int i = 0; i < list.size(); i++) {
                        vector[i] = list.get(i).floatValue();
                    }
                    return vector;
                }
            }
            throw new IllegalStateException("FastAPI embedding service returned invalid or missing response payload");
        } catch (Exception e) {
            log.error("Failed to generate SentenceTransformer embedding via Python AI Microservice at {}", aiServiceBaseUrl, e);
            throw new RuntimeException("Embedding generation failed via Python AI microservice: " + e.getMessage(), e);
        }
    }

    @Override
    public List<float[]> generateBatchEmbeddings(List<String> textChunks) {
        if (textChunks == null || textChunks.isEmpty()) {
            return List.of();
        }

        List<float[]> embeddings = new ArrayList<>(textChunks.size());
        for (String chunk : textChunks) {
            embeddings.add(generateEmbedding(chunk));
        }
        return embeddings;
    }
}
