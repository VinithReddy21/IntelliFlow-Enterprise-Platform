package com.intelliflow.modules.auth.service;

import com.intelliflow.common.exception.ErrorCode;

import com.intelliflow.common.security.JwtTokenProvider;
import com.intelliflow.modules.auth.dto.JwtResponseDto;
import com.intelliflow.modules.auth.dto.LoginRequestDto;
import com.intelliflow.modules.auth.dto.RefreshTokenRequestDto;
import com.intelliflow.modules.user.domain.UserEntity;
import com.intelliflow.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.UUID;

/**
 * Implementation of AuthService managing Login, Logout, and Refresh Token Rotation.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final StringRedisTemplate redisTemplate;

    @Override
    @Transactional(readOnly = true)
    public JwtResponseDto login(LoginRequestDto loginRequest) {
        UserEntity user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new BadCredentialsException(ErrorCode.UNAUTHORIZED.getDefaultMessage()));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException(ErrorCode.UNAUTHORIZED.getDefaultMessage());
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail(), user.getRole().name());
        String refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return JwtResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public JwtResponseDto refresh(RefreshTokenRequestDto refreshRequest) {
        String rawToken = refreshRequest.getRefreshToken();
        
        if (!refreshTokenService.validateRefreshToken(rawToken)) {
            throw new BadCredentialsException("Invalid or expired refresh token");
        }

        UUID userId = UUID.fromString(rawToken.split(":")[0]);
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new BadCredentialsException(ErrorCode.UNAUTHORIZED.getDefaultMessage()));

        // Rotate Refresh Token
        refreshTokenService.revokeRefreshToken(rawToken);
        String newRefreshToken = refreshTokenService.createRefreshToken(user.getId());
        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail(), user.getRole().name());

        return JwtResponseDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    @Override
    public void logout(String accessToken, String refreshToken) {
        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            String token = accessToken.substring(7);
            if (jwtTokenProvider.validateToken(token)) {
                String jti = jwtTokenProvider.getJtiFromToken(token);
                long remainingMs = jwtTokenProvider.getRemainingExpirationMs(token);
                
                // Add JTI to Redis Blacklist
                redisTemplate.opsForValue().set("auth:bl_tok:" + jti, "revoked", Duration.ofMillis(remainingMs));
            }
        }

        if (refreshToken != null) {
            refreshTokenService.revokeRefreshToken(refreshToken);
        }
    }
}
