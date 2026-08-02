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
public class RagQueryRequestDto {

    @NotBlank(message = "Prompt query is required")
    @Size(min = 3, max = 2000, message = "Prompt query must be between 3 and 2000 characters")
    private String prompt;

    private UUID departmentId;

    @Min(value = 1, message = "MaxSourceChunks must be at least 1")
    @Max(value = 20, message = "MaxSourceChunks cannot exceed 20")
    @Builder.Default
    private int maxSourceChunks = 5;

    @Builder.Default
    private boolean includeCitations = true;
}
