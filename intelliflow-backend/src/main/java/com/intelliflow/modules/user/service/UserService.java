package com.intelliflow.modules.user.service;

import com.intelliflow.modules.user.domain.UserEntity;
import com.intelliflow.modules.user.domain.UserStatus;
import com.intelliflow.modules.user.dto.ChangePasswordRequestDto;
import com.intelliflow.modules.user.dto.UpdateProfileRequestDto;
import com.intelliflow.modules.user.dto.UserProfileResponseDto;

import java.util.UUID;

/**
 * Business Service Interface contract for User Domain Operations.
 * 
 * Defines business logic boundaries for user retrieval, account protection locking,
 * status transitions, and soft-delete governance.
 */
public interface UserService {

    /**
     * Retrieves user entity by email regardless of soft deletion state.
     */
    UserEntity getByEmail(String email);

    /**
     * Retrieves active non-deleted user entity by UUID.
     */
    UserEntity getActiveById(UUID userId);

    /**
     * Updates profile information for an active user.
     */
    UserProfileResponseDto updateProfile(UUID userId, UpdateProfileRequestDto request);

    /**
     * Changes user password after verifying current credentials and liquidates active refresh sessions.
     */
    void changePassword(UUID userId, ChangePasswordRequestDto request);

    /**
     * Handles invalid login attempts counter increment and triggers account locking.
     */
    void handleFailedLogin(UUID userId);

    /**
     * Resets failed login attempt counter upon successful authentication.
     */
    void handleSuccessfulLogin(UUID userId);

    /**
     * Soft-deletes a user account and revokes active sessions.
     */
    void softDeleteUser(UUID userId);

    /**
     * Updates user administrative status (ACTIVE, SUSPENDED, LOCKED).
     */
    void updateStatus(UUID userId, UserStatus status);
}
