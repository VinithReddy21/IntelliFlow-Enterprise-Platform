package com.intelliflow.modules.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Authentication Response Payload DTO containing Access Token and User Metadata.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponseDto {

    private String accessToken;
    @Builder.Default
    private String tokenType = "Bearer";
    private String refreshToken;
    private UUID userId;
    private String email;
    private String role;
}
