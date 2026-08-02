package com.intelliflow.modules.document.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RagResponseDto {

    private String query;
    private String generatedAnswer;
    private int retrievedChunkCount;
    private List<SourceCitationDto> citations;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SourceCitationDto {
        private UUID documentId;
        private String documentTitle;
        private int chunkIndex;
        private String excerptSnippet;
        private float similarityScore;
    }
}
