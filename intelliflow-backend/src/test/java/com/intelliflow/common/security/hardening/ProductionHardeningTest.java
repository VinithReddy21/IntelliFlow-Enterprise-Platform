package com.intelliflow.common.security.hardening;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductionHardeningTest {

    @Mock
    private RateLimiter rateLimiter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private RateLimitingFilter rateLimitingFilter;

    @Test
    @DisplayName("RateLimiter - Should enforce bucket token consumption limits")
    void rateLimiter_EnforcesLimits() {
        RateLimiter limiter = new RateLimiter();

        assertTrue(limiter.tryConsume("testClient", 2, 60000L));
        assertTrue(limiter.tryConsume("testClient", 2, 60000L));
        assertFalse(limiter.tryConsume("testClient", 2, 60000L));
    }

    @Test
    @DisplayName("RateLimitingFilter - Should trigger 429 Too Many Requests when rate limit exceeded")
    void rateLimitingFilter_Triggers429() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/v1/auth/login");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(rateLimiter.tryConsume(anyString(), anyInt(), anyLong())).thenReturn(false);

        StringWriter writer = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(writer));

        rateLimitingFilter.doFilter(request, response, filterChain);

        verify(response).setStatus(429);
        assertTrue(writer.toString().contains("ERR_429_TOO_MANY_REQUESTS"));
    }

    @Test
    @DisplayName("SecurityHeadersFilter - Should set OWASP security response headers")
    void securityHeadersFilter_SetsHeaders() throws Exception {
        SecurityHeadersFilter filter = new SecurityHeadersFilter();

        filter.doFilter(request, response, filterChain);

        verify(response).setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains; preload");
        verify(response).setHeader("X-Content-Type-Options", "nosniff");
        verify(response).setHeader("X-Frame-Options", "DENY");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("FileUploadValidationUtil - Should reject invalid filenames and excessive file sizes")
    void fileUploadValidation_RejectsMaliciousFiles() {
        MockMultipartFile validFile = new MockMultipartFile("file", "document.txt", "text/plain", "Hello".getBytes());
        assertDoesNotThrow(() -> FileUploadValidationUtil.validateUpload(validFile));

        MockMultipartFile pathTraversalFile = new MockMultipartFile("file", "../etc/passwd", "text/plain", "Data".getBytes());
        assertThrows(IllegalArgumentException.class, () -> FileUploadValidationUtil.validateUpload(pathTraversalFile));

        MockMultipartFile invalidMimeFile = new MockMultipartFile("file", "exe.sh", "application/x-sh", "malware".getBytes());
        assertThrows(IllegalArgumentException.class, () -> FileUploadValidationUtil.validateUpload(invalidMimeFile));
    }
}
