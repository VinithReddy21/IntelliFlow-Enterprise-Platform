package com.intelliflow.modules.auth.service;

import com.intelliflow.common.security.JwtTokenProvider;
import com.intelliflow.modules.auth.dto.JwtResponseDto;
import com.intelliflow.modules.auth.dto.LoginRequestDto;
import com.intelliflow.modules.user.domain.RoleEnum;
import com.intelliflow.modules.user.domain.UserEntity;
import com.intelliflow.modules.user.domain.UserStatus;
import com.intelliflow.modules.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private StringRedisTemplate redisTemplate;

    @InjectMocks
    private AuthServiceImpl authService;

    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userEntity = UserEntity.builder()
                .id(UUID.randomUUID())
                .email("admin@intelliflow.com")
                .passwordHash("hashed_password")
                .firstName("Admin")
                .lastName("User")
                .role(RoleEnum.ROLE_ADMIN)
                .status(UserStatus.ACTIVE)
                .build();
    }

    @Test
    @DisplayName("Should successfully authenticate user and return JwtResponseDto")
    void shouldLoginSuccessfully() {
        LoginRequestDto loginRequest = new LoginRequestDto("admin@intelliflow.com", "password123");

        when(userRepository.findByEmail("admin@intelliflow.com")).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches("password123", "hashed_password")).thenReturn(true);
        when(jwtTokenProvider.generateAccessToken(any(), any(), any())).thenReturn("mock_access_token");
        when(refreshTokenService.createRefreshToken(any())).thenReturn("mock_user_id:mock_refresh_token");

        JwtResponseDto response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("mock_access_token", response.getAccessToken());
        assertEquals("admin@intelliflow.com", response.getEmail());
        assertEquals("ROLE_ADMIN", response.getRole());
    }

    @Test
    @DisplayName("Should throw BadCredentialsException on invalid password")
    void shouldThrowExceptionOnInvalidPassword() {
        LoginRequestDto loginRequest = new LoginRequestDto("admin@intelliflow.com", "wrong_password");

        when(userRepository.findByEmail("admin@intelliflow.com")).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches("wrong_password", "hashed_password")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.login(loginRequest));
    }
}
