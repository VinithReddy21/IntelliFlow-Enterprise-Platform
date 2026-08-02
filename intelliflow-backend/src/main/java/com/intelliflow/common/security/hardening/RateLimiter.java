package com.intelliflow.common.security.hardening;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-Memory Sliding Window Token Bucket Rate Limiter.
 * 
 * Enforces configurable request rate limits per IP / Client identity.
 */
@Component
public class RateLimiter {

    private final Map<String, ClientWindow> clientWindows = new ConcurrentHashMap<>();

    public boolean tryConsume(String key, int maxRequests, long windowMillis) {
        long now = System.currentTimeMillis();
        ClientWindow window = clientWindows.compute(key, (k, v) -> {
            if (v == null || now - v.windowStartTime.get() > windowMillis) {
                return new ClientWindow(now, 1);
            } else {
                v.requestCount.incrementAndGet();
                return v;
            }
        });

        return window.requestCount.get() <= maxRequests;
    }

    private static class ClientWindow {
        final AtomicLong windowStartTime;
        final AtomicInteger requestCount;

        ClientWindow(long startTime, int initialCount) {
            this.windowStartTime = new AtomicLong(startTime);
            this.requestCount = new AtomicInteger(initialCount);
        }
    }
}
