package com.intelliflow.common.observability.health;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

/**
 * Actuator Custom Health Indicator for Redis Distributed Cache.
 */
@Component("redisHealthIndicator")
@RequiredArgsConstructor
public class RedisHealthIndicator implements HealthIndicator {

    private final RedisConnectionFactory redisConnectionFactory;

    @Override
    public Health health() {
        try {
            String ping = redisConnectionFactory.getConnection().ping();
            if ("PONG".equalsIgnoreCase(ping)) {
                return Health.up()
                        .withDetail("cache", "Redis")
                        .withDetail("ping", "PONG")
                        .build();
            }
            return Health.down().withDetail("cache", "Redis").withDetail("error", "PING response: " + ping).build();
        } catch (Exception e) {
            return Health.down(e).withDetail("cache", "Redis").withDetail("error", e.getMessage()).build();
        }
    }
}
