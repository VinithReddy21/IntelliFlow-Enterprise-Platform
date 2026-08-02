package com.intelliflow.common.performance.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Production Redis Connection Pool and Serialization Optimization.
 */
@Slf4j
@Configuration
public class RedisOptimizationConfig {

    @Bean
    public RedisTemplate<String, Object> optimizedRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setEnableTransactionSupport(true);
        template.afterPropertiesSet();
        log.info("Initialized Optimized RedisTemplate with key serialization and transaction support");
        return template;
    }
}
