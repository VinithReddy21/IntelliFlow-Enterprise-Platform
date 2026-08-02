package com.intelliflow.modules.user.service;

import com.intelliflow.common.exception.ResourceNotFoundException;
import com.intelliflow.modules.auth.service.RefreshTokenService;
import com.intelliflow.modules.user.domain.RoleEnum;
import com.intelliflow.modules.user.domain.UserEntity;
import com.intelliflow.modules.user.domain.UserStatus;
import com.intelliflow.modules.user.dto.ChangePasswordRequestDto;
import com.intelliflow.modules.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * JUnit 5 Unit Test Suite for UserServiceImpl domain logic.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private UserEntity activeUser;
    private UUID userId;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(userService, "maxFailedAttempts", 5);
        ReflectionTestUtils.setField(userService, "lockoutDurationMinutes", 15L);

        userId = UUID.randomUUID();
        activeUser = UserEntity.builder()
                .id(userId)
                .email("test@intelliflow.com")
                .passwordHash("hashed_pass")
                .firstName("Test")
                .lastName("User")
                .role(RoleEnum.ROLE_EMPLOYEE)
                .status(UserStatus.ACTIVE)
                .failedLoginAttempts(0)
                .build();
    }

    @Test
    @DisplayName("getByEmail - Should return user entity when found")
    void getByEmail_Success() {
        when(userRepository.findByEmail("test@intelliflow.com")).thenReturn(Optional.of(activeUser));

        UserEntity result = userService.getByEmail("test@intelliflow.com");

        assertNotNull(result);
        assertEquals("test@intelliflow.com", result.getEmail());
        verify(userRepository, times(1)).findByEmail("test@intelliflow.com");
    }

    @Test
    @DisplayName("getByEmail - Should throw ResourceNotFoundException when user missing")
    void getByEmail_NotFound_ThrowsException() {
        when(userRepository.findByEmail("missing@intelliflow.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getByEmail("missing@intelliflow.com"));
    }

    @Test
    @DisplayName("getActiveById - Should return active user entity when found")
    void getActiveById_Success() {
        when(userRepository.findByIdAndDeletedAtIsNull(userId)).thenReturn(Optional.of(activeUser));

        UserEntity result = userService.getActiveById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        verify(userRepository, times(1)).findByIdAndDeletedAtIsNull(userId);
    }

    @Test
    @DisplayName("getActiveById - Should throw ResourceNotFoundException for soft-deleted user")
    void getActiveById_Deleted_ThrowsException() {
        when(userRepository.findByIdAndDeletedAtIsNull(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getActiveById(userId));
    }

    @Test
    @DisplayName("changePassword - Should successfully change password and revoke refresh tokens")
    void changePassword_Success() {
        ChangePasswordRequestDto request = new ChangePasswordRequestDto("old_pass", "NewPass123!");
        when(userRepository.findByIdAndDeletedAtIsNull(userId)).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches("old_pass", "hashed_pass")).thenReturn(true);
        when(passwordEncoder.matches("NewPass123!", "hashed_pass")).thenReturn(false);
        when(passwordEncoder.encode("NewPass123!")).thenReturn("new_hashed_pass");

        userService.changePassword(userId, request);

        verify(userRepository, times(1)).save(activeUser);
        verify(refreshTokenService, times(1)).revokeAllUserRefreshTokens(userId);
    }

    @Test
    @DisplayName("changePassword - Should throw BadCredentialsException when current password invalid")
    void changePassword_WrongCurrentPassword_ThrowsException() {
        ChangePasswordRequestDto request = new ChangePasswordRequestDto("wrong_pass", "NewPass123!");
        when(userRepository.findByIdAndDeletedAtIsNull(userId)).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches("wrong_pass", "hashed_pass")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> userService.changePassword(userId, request));
        verify(userRepository, never()).save(any());
        verify(refreshTokenService, never()).revokeAllUserRefreshTokens(any());
    }

    @Test
    @DisplayName("handleFailedLogin - Should increment attempt counter on normal failure")
    void handleFailedLogin_Normal() {
        activeUser.setFailedLoginAttempts(1);
        when(userRepository.findById(userId)).thenReturn(Optional.of(activeUser));

        userService.handleFailedLogin(userId);

        verify(userRepository, times(1)).incrementFailedLoginAttempts(userId);
        verify(userRepository, never()).lockAccount(any(), any(), any());
    }

    @Test
    @DisplayName("handleFailedLogin - Should trigger account lockout when attempt count reaches threshold")
    void handleFailedLogin_LockoutThreshold() {
        activeUser.setFailedLoginAttempts(4);
        when(userRepository.findById(userId)).thenReturn(Optional.of(activeUser));

        userService.handleFailedLogin(userId);

        verify(userRepository, times(1)).incrementFailedLoginAttempts(userId);
        verify(userRepository, times(1)).lockAccount(eq(userId), eq(UserStatus.LOCKED), any());
    }

    @Test
    @DisplayName("handleSuccessfulLogin - Should reset failed login attempts")
    void handleSuccessfulLogin_Reset() {
        activeUser.setFailedLoginAttempts(3);
        when(userRepository.findById(userId)).thenReturn(Optional.of(activeUser));

        userService.handleSuccessfulLogin(userId);

        verify(userRepository, times(1)).resetFailedLoginAttempts(userId);
    }

    @Test
    @DisplayName("updateStatus - Should successfully update status on valid transition")
    void updateStatus_ValidTransition() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(activeUser));

        userService.updateStatus(userId, UserStatus.SUSPENDED);

        verify(userRepository, times(1)).updateUserStatus(userId, UserStatus.SUSPENDED);
    }

    @Test
    @DisplayName("updateStatus - Should throw IllegalStateException when modifying soft-deleted account")
    void updateStatus_SoftDeleted_ThrowsException() {
        activeUser.setDeletedAt(Instant.now());
        when(userRepository.findById(userId)).thenReturn(Optional.of(activeUser));

        assertThrows(IllegalStateException.class, () -> userService.updateStatus(userId, UserStatus.ACTIVE));
        verify(userRepository, never()).updateUserStatus(any(), any());
    }

    @Test
    @DisplayName("softDeleteUser - Should mark user soft-deleted and revoke all active refresh tokens")
    void softDeleteUser_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(activeUser));

        userService.softDeleteUser(userId);

        verify(userRepository, times(1)).softDeleteUser(eq(userId), eq(UserStatus.SUSPENDED), any());
        verify(refreshTokenService, times(1)).revokeAllUserRefreshTokens(userId);
    }

    @Test
    @DisplayName("softDeleteUser - Should throw IllegalStateException when account already soft-deleted")
    void softDeleteUser_AlreadyDeleted_ThrowsException() {
        activeUser.setDeletedAt(Instant.now());
        when(userRepository.findById(userId)).thenReturn(Optional.of(activeUser));

        assertThrows(IllegalStateException.class, () -> userService.softDeleteUser(userId));
        verify(userRepository, never()).softDeleteUser(any(), any(), any());
        verify(refreshTokenService, never()).revokeAllUserRefreshTokens(any());
    }
}
