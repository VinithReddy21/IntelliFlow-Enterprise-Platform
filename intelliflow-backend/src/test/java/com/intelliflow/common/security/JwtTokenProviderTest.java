package com.intelliflow.common.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private static final String SECRET = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private static final long EXPIRATION_MS = 900000;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(SECRET, EXPIRATION_MS);
    }

    @Test
    @DisplayName("Should generate valid JWT Access Token with correct claims")
    void shouldGenerateValidToken() {
        UUID userId = UUID.randomUUID();
        String email = "test@intelliflow.com";
        String role = "ROLE_MANAGER";

        String token = jwtTokenProvider.generateAccessToken(userId, email, role);

        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals(userId, jwtTokenProvider.getUserIdFromToken(token));
        assertEquals(role, jwtTokenProvider.getRoleFromToken(token));
        
        Claims claims = jwtTokenProvider.getClaimsFromToken(token);
        assertEquals(email, claims.get("email", String.class));
        assertNotNull(claims.getId());
    }

    @Test
    @DisplayName("Should reject invalid or tampered JWT token")
    void shouldRejectInvalidToken() {
        String invalidToken = "eyJhbGciOiJIUzI1NiJ9.invalidpayload.signature";
        assertFalse(jwtTokenProvider.validateToken(invalidToken));
    }
}
