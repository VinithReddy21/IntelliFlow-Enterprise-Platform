package com.intelliflow.modules.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;

/**
 * Service managing Redis Stateful Refresh Token Lifecycle and Rotation.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final StringRedisTemplate redisTemplate;
    private static final long REFRESH_EXPIRATION_DAYS = 7;

    public String createRefreshToken(UUID userId) {
        String tokenId = UUID.randomUUID().toString();
        String redisKey = buildKey(userId, tokenId);
        
        // Store refresh token with 7-day TTL
        redisTemplate.opsForValue().set(redisKey, "valid", Duration.ofDays(REFRESH_EXPIRATION_DAYS));
        return userId + ":" + tokenId;
    }

    public boolean validateRefreshToken(String rawRefreshToken) {
        try {
            String[] parts = rawRefreshToken.split(":");
            if (parts.length != 2) return false;
            
            UUID userId = UUID.fromString(parts[0]);
            String tokenId = parts[1];
            String redisKey = buildKey(userId, tokenId);

            return Boolean.TRUE.equals(redisTemplate.hasKey(redisKey));
        } catch (Exception ex) {
            log.warn("Invalid refresh token structure: {}", ex.getMessage());
            return false;
        }
    }

    public void revokeRefreshToken(String rawRefreshToken) {
        try {
            String[] parts = rawRefreshToken.split(":");
            if (parts.length == 2) {
                UUID userId = UUID.fromString(parts[0]);
                String tokenId = parts[1];
                redisTemplate.delete(buildKey(userId, tokenId));
            }
        } catch (Exception ex) {
            log.warn("Error revoking refresh token: {}", ex.getMessage());
        }
    }

    public void revokeAllUserRefreshTokens(UUID userId) {
        try {
            String pattern = "auth:ref:" + userId + ":*";
            Set<String> keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.info("Revoked {} active refresh token sessions for user ID: {}", keys.size(), userId);
            }
        } catch (Exception ex) {
            log.warn("Error revoking all refresh tokens for user ID: {}: {}", userId, ex.getMessage());
        }
    }

    private String buildKey(UUID userId, String tokenId) {
        return "auth:ref:" + userId + ":" + tokenId;
    }
}
