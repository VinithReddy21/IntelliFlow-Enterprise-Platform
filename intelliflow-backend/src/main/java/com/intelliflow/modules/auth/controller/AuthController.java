package com.intelliflow.modules.auth.controller;

import com.intelliflow.common.response.ApiResponse;
import com.intelliflow.modules.auth.dto.JwtResponseDto;
import com.intelliflow.modules.auth.dto.LoginRequestDto;
import com.intelliflow.modules.auth.dto.RefreshTokenRequestDto;
import com.intelliflow.modules.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller exposing Public Authentication APIs.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication & JWT Token Management Endpoints")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and issue JWT Access & Refresh tokens")
    public ResponseEntity<ApiResponse<JwtResponseDto>> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        JwtResponseDto response = authService.login(loginRequest);
        return ResponseEntity.ok(ApiResponse.success(response, "User authenticated successfully"));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Rotate refresh token and issue new access token")
    public ResponseEntity<ApiResponse<JwtResponseDto>> refresh(@Valid @RequestBody RefreshTokenRequestDto refreshRequest) {
        JwtResponseDto response = authService.refresh(refreshRequest);
        return ResponseEntity.ok(ApiResponse.success(response, "Token refreshed successfully"));
    }

    @PostMapping("/logout")
    @Operation(summary = "Revoke access token and invalidate refresh token session")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader(value = "Authorization", required = false) String accessToken,
            @RequestBody(required = false) RefreshTokenRequestDto refreshRequest) {
        
        String refreshToken = (refreshRequest != null) ? refreshRequest.getRefreshToken() : null;
        authService.logout(accessToken, refreshToken);
        return ResponseEntity.ok(ApiResponse.success(null, "Logged out successfully"));
    }
}
