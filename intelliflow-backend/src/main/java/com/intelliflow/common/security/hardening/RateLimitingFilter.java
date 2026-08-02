package com.intelliflow.common.security.hardening;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Servlet Filter enforcing IP-based Rate Limiting on sensitive API endpoints.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitingFilter extends OncePerRequestFilter {

    private final RateLimiter rateLimiter;

    private static final int AUTH_LIMIT_PER_MINUTE = 10;
    private static final int AI_LIMIT_PER_MINUTE = 20;
    private static final long ONE_MINUTE_MILLIS = 60000L;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();
        String clientIp = getClientIp(request);

        if (uri.startsWith("/api/v1/auth")) {
            if (!rateLimiter.tryConsume("auth:" + clientIp, AUTH_LIMIT_PER_MINUTE, ONE_MINUTE_MILLIS)) {
                log.warn("RATE LIMIT EXCEEDED: Client IP {} hit limit on Auth endpoint {}", clientIp, uri);
                sendRateLimitError(response, "Too many authentication attempts. Please try again in 1 minute.");
                return;
            }
        } else if (uri.startsWith("/api/v1/documents/search")) {
            if (!rateLimiter.tryConsume("ai:" + clientIp, AI_LIMIT_PER_MINUTE, ONE_MINUTE_MILLIS)) {
                log.warn("RATE LIMIT EXCEEDED: Client IP {} hit limit on AI Search endpoint {}", clientIp, uri);
                sendRateLimitError(response, "Too many AI search requests. Rate limit is 20 requests per minute.");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null && !xfHeader.isEmpty()) {
            return xfHeader.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private void sendRateLimitError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        String jsonPayload = String.format("{\"success\":false,\"error\":{\"code\":\"ERR_429_TOO_MANY_REQUESTS\",\"message\":\"%s\"}}", message);
        response.getWriter().write(jsonPayload);
    }
}
