package com.intelliflow.common.security.hardening;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Idempotency Key & Request Replay Protection Servlet Filter.
 * 
 * Intercepts POST/PUT operations carrying 'Idempotency-Key', preventing duplicate database mutations.
 */
@Slf4j
@Component
public class IdempotencyFilter extends OncePerRequestFilter {

    public static final String IDEMPOTENCY_KEY_HEADER = "Idempotency-Key";

    private final Map<String, CachedResponse> idempotencyStore = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String method = request.getMethod();
        String idempotencyKey = request.getHeader(IDEMPOTENCY_KEY_HEADER);

        if (("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method)) && StringUtils.hasText(idempotencyKey)) {
            String cacheKey = method + ":" + request.getRequestURI() + ":" + idempotencyKey;

            CachedResponse cached = idempotencyStore.get(cacheKey);
            if (cached != null) {
                log.info("IDEMPOTENCY HIT: Replaying cached response for Key: {}", idempotencyKey);
                response.setStatus(cached.status);
                response.setContentType(cached.contentType);
                response.setHeader("X-Cache", "HIT-IDEMPOTENT");
                response.getWriter().write(cached.body);
                return;
            }

            ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
            filterChain.doFilter(request, responseWrapper);

            int status = responseWrapper.getStatus();
            if (status >= 200 && status < 300) {
                byte[] responseBody = responseWrapper.getContentAsByteArray();
                String bodyString = new String(responseBody, responseWrapper.getCharacterEncoding());
                String contentType = responseWrapper.getContentType();

                idempotencyStore.put(cacheKey, new CachedResponse(status, contentType != null ? contentType : MediaType.APPLICATION_JSON_VALUE, bodyString));
                log.info("IDEMPOTENCY STORED: Cached successful execution for Key: {}", idempotencyKey);
            }
            responseWrapper.copyBodyToResponse();
            return;
        }

        filterChain.doFilter(request, response);
    }

    private record CachedResponse(int status, String contentType, String body) {}
}
