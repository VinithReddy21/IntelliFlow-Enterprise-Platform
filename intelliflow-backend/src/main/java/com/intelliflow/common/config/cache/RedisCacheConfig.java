package com.intelliflow.common.config.cache;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Enterprise Production Redis Cache Configuration.
 * 
 * Configures Spring Cache abstractions with Redis distributed storage, custom TTLs per domain,
 * String key serialization, Jackson JSON value serialization with ISO time support,
 * and null-value caching prevention.
 */
@Configuration
@EnableCaching
public class RedisCacheConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer));

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        // 1. User Module Caches (15 Min TTL)
        cacheConfigurations.put(CacheNames.USERS, defaultConfig.entryTtl(Duration.ofMinutes(15)));
        cacheConfigurations.put(CacheNames.USER_DETAILS, defaultConfig.entryTtl(Duration.ofMinutes(15)));

        // 2. Task Module Caches (5 Min TTL)
        cacheConfigurations.put(CacheNames.TASKS, defaultConfig.entryTtl(Duration.ofMinutes(5)));
        cacheConfigurations.put(CacheNames.TASK_DETAILS, defaultConfig.entryTtl(Duration.ofMinutes(5)));

        // 3. Document Module Caches (30 Min TTL)
        cacheConfigurations.put(CacheNames.DOCUMENTS, defaultConfig.entryTtl(Duration.ofMinutes(30)));
        cacheConfigurations.put(CacheNames.DOCUMENT_DETAILS, defaultConfig.entryTtl(Duration.ofMinutes(30)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}
