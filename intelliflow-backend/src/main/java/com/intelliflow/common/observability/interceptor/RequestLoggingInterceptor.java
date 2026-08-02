package com.intelliflow.common.observability.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Spring WebMVC HandlerInterceptor logging structured HTTP request/response metrics and execution latency.
 */
@Slf4j
@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private static final String START_TIME_ATTRIBUTE = "startTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(START_TIME_ATTRIBUTE, System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        Long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
        long latency = startTime != null ? System.currentTimeMillis() - startTime : 0;

        if (ex != null) {
            log.error("HTTP REQUEST FAILED: [{} {}] -> Status: {} (Latency: {} ms) | Exception: {}",
                    request.getMethod(), request.getRequestURI(), response.getStatus(), latency, ex.getMessage());
        } else {
            log.info("HTTP REQUEST COMPLETE: [{} {}] -> Status: {} (Latency: {} ms)",
                    request.getMethod(), request.getRequestURI(), response.getStatus(), latency);
        }
    }
}
