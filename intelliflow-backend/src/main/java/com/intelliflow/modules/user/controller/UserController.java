package com.intelliflow.modules.user.controller;

import com.intelliflow.common.response.ApiResponse;
import com.intelliflow.modules.user.domain.UserEntity;
import com.intelliflow.modules.user.dto.ChangePasswordRequestDto;
import com.intelliflow.modules.user.dto.UpdateProfileRequestDto;
import com.intelliflow.modules.user.dto.UpdateUserStatusRequestDto;
import com.intelliflow.modules.user.dto.UserProfileResponseDto;
import com.intelliflow.modules.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST Controller exposing User Management & Profile Governance Endpoints.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Endpoints for user profile and status governance")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user profile")
    public ResponseEntity<ApiResponse<UserProfileResponseDto>> getCurrentUserProfile(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        UserEntity user = userService.getActiveById(userId);
        UserProfileResponseDto response = UserProfileResponseDto.fromEntity(user);
        return ResponseEntity.ok(ApiResponse.success(response, "User profile retrieved successfully"));
    }

    @PatchMapping("/me")
    @Operation(summary = "Update current authenticated user profile details")
    public ResponseEntity<ApiResponse<UserProfileResponseDto>> updateCurrentUserProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequestDto request) {
        UUID userId = (UUID) authentication.getPrincipal();
        UserProfileResponseDto response = userService.updateProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "User profile updated successfully"));
    }

    @PostMapping("/me/password")
    @Operation(summary = "Change current authenticated user password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequestDto request) {
        UUID userId = (UUID) authentication.getPrincipal();
        userService.changePassword(userId, request);
        return ResponseEntity.ok(ApiResponse.success(null, "Password changed successfully. Please log in again."));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update user administrative status (Admin Only)")
    public ResponseEntity<ApiResponse<UserProfileResponseDto>> updateUserStatus(
            @PathVariable("id") UUID userId,
            @Valid @RequestBody UpdateUserStatusRequestDto request) {
        userService.updateStatus(userId, request.getStatus());
        UserEntity updatedUser = userService.getActiveById(userId);
        UserProfileResponseDto response = UserProfileResponseDto.fromEntity(updatedUser);
        return ResponseEntity.ok(ApiResponse.success(response, "User status updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Soft-delete user account (Admin Only)")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable("id") UUID userId) {
        userService.softDeleteUser(userId);
        return ResponseEntity.ok(ApiResponse.success(null, "User account soft-deleted successfully"));
    }
}
