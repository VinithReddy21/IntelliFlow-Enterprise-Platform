package com.intelliflow.modules.document.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimilaritySearchRequestDto {

    @NotBlank(message = "Query text is required")
    @Size(min = 2, max = 2000, message = "Query text must be between 2 and 2000 characters")
    private String query;

    private UUID departmentId;

    @Min(value = 1, message = "TopK must be at least 1")
    @Max(value = 50, message = "TopK cannot exceed 50")
    @Builder.Default
    private int topK = 5;

    @Min(value = 0, message = "MinSimilarity score cannot be negative")
    @Max(value = 1, message = "MinSimilarity score cannot exceed 1.0")
    @Builder.Default
    private float minSimilarity = 0.5f;
}
