package com.intelliflow.modules.user.service;

import com.intelliflow.common.config.cache.CacheNames;
import com.intelliflow.common.exception.ResourceNotFoundException;
import com.intelliflow.modules.auth.service.RefreshTokenService;
import com.intelliflow.modules.user.domain.UserEntity;
import com.intelliflow.modules.user.domain.UserStatus;
import com.intelliflow.modules.user.dto.ChangePasswordRequestDto;
import com.intelliflow.modules.user.dto.UpdateProfileRequestDto;
import com.intelliflow.modules.user.dto.UserProfileResponseDto;
import com.intelliflow.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * Production Implementation of UserService domain operations.
 * 
 * Manages user authentication lifecycle, account brute-force protection,
 * status transitions, Redis caching, and soft deletion governance.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;

    @Value("${security.lockout.max-attempts:5}")
    private int maxFailedAttempts;

    @Value("${security.lockout.duration-minutes:15}")
    private long lockoutDurationMinutes;

    @Override
    @Transactional(readOnly = true)
    public UserEntity getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheNames.USERS, key = "#userId")
    public UserEntity getActiveById(UUID userId) {
        return userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheNames.USERS, key = "#userId")
    public UserProfileResponseDto updateProfile(UUID userId, UpdateProfileRequestDto request) {
        UserEntity user = getActiveById(userId);

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        UserEntity updatedUser = userRepository.save(user);
        log.info("Updated profile names for user ID: {}", userId);
        return UserProfileResponseDto.fromEntity(updatedUser);
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheNames.USERS, key = "#userId")
    public void changePassword(UUID userId, ChangePasswordRequestDto request) {
        UserEntity user = getActiveById(userId);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Current password is incorrect");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("New password cannot be the same as current password");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        refreshTokenService.revokeAllUserRefreshTokens(userId);

        log.info("Successfully changed password and revoked active refresh sessions for user ID: {}", userId);
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheNames.USERS, key = "#userId")
    public void handleFailedLogin(UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (user.getStatus() == UserStatus.LOCKED && user.getLockoutUntil() != null && user.getLockoutUntil().isAfter(Instant.now())) {
            log.warn("Account is already LOCKED for user ID: {} until {}", userId, user.getLockoutUntil());
            return;
        }

        userRepository.incrementFailedLoginAttempts(userId);
        int updatedAttempts = user.getFailedLoginAttempts() + 1;

        log.warn("Failed login attempt for user ID: {}. Current failed attempts: {}", userId, updatedAttempts);

        if (updatedAttempts >= maxFailedAttempts) {
            Instant lockoutUntil = Instant.now().plus(Duration.ofMinutes(lockoutDurationMinutes));
            userRepository.lockAccount(userId, UserStatus.LOCKED, lockoutUntil);
            log.warn("Account LOCKED for user ID: {} until {}", userId, lockoutUntil);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheNames.USERS, key = "#userId")
    public void handleSuccessfulLogin(UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (user.getFailedLoginAttempts() > 0 || user.getLockoutUntil() != null) {
            userRepository.resetFailedLoginAttempts(userId);
            log.info("Reset failed login attempts and cleared lockout for user ID: {}", userId);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheNames.USERS, key = "#userId")
    public void updateStatus(UUID userId, UserStatus newStatus) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        UserStatus currentStatus = user.getStatus();

        if (user.getDeletedAt() != null) {
            throw new IllegalStateException("Cannot change status of a soft-deleted user account");
        }

        if ("DELETED".equals(newStatus.name())) {
            throw new IllegalArgumentException("Use softDeleteUser API to perform account deletion");
        }

        if (currentStatus == newStatus) {
            log.info("Status for user ID: {} is already {}", userId, newStatus);
            return;
        }

        userRepository.updateUserStatus(userId, newStatus);

        if (newStatus == UserStatus.ACTIVE && (currentStatus == UserStatus.LOCKED || currentStatus == UserStatus.SUSPENDED)) {
            userRepository.resetFailedLoginAttempts(userId);
        }

        log.info("Updated status for user ID: {} from {} to {}", userId, currentStatus, newStatus);
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheNames.USERS, key = "#userId")
    public void softDeleteUser(UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (user.getDeletedAt() != null) {
            throw new IllegalStateException("User account is already soft-deleted");
        }

        Instant deletedAt = Instant.now();

        userRepository.softDeleteUser(userId, UserStatus.SUSPENDED, deletedAt);

        refreshTokenService.revokeAllUserRefreshTokens(userId);

        log.info("Successfully soft-deleted user account ID: {} at {} and revoked active refresh tokens", userId, deletedAt);
    }
}
