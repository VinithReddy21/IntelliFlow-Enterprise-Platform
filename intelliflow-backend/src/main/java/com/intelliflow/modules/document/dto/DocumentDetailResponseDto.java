package com.intelliflow.modules.document.dto;

import com.intelliflow.modules.document.domain.DocumentEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentDetailResponseDto {

    private DocumentResponseDto document;
    private int totalChunks;
    private List<ChunkProjectionDto> chunkProjections;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ChunkProjectionDto {
        private String chunkId;
        private int chunkIndex;
        private int tokenCount;
        private String contentSnippet;
    }

    public static DocumentDetailResponseDto fromEntity(DocumentEntity entity, int totalChunks, List<ChunkProjectionDto> chunkProjections) {
        if (entity == null) {
            return null;
        }

        return DocumentDetailResponseDto.builder()
                .document(DocumentResponseDto.fromEntity(entity))
                .totalChunks(totalChunks)
                .chunkProjections(chunkProjections)
                .build();
    }
}
