package com.intelliflow.modules.document.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimilaritySearchResponseDto {

    private String query;
    private int totalMatches;
    private List<VectorChunkResultDto> results;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VectorChunkResultDto {
        private UUID chunkId;
        private UUID documentId;
        private String documentTitle;
        private int chunkIndex;
        private String content;
        private int tokenCount;
        private float similarityScore;
        private Map<String, Object> metadata;
    }
}
