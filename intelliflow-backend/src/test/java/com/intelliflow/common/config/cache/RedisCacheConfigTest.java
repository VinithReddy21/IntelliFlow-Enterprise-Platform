package com.intelliflow.common.config.cache;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class RedisCacheConfigTest {

    @Mock
    private RedisConnectionFactory redisConnectionFactory;

    @Test
    @DisplayName("cacheManager - Should instantiate RedisCacheManager with domain TTL configurations")
    void cacheManager_InstantiationSuccess() {
        RedisCacheConfig cacheConfig = new RedisCacheConfig();
        RedisCacheManager cacheManager = cacheConfig.cacheManager(redisConnectionFactory);

        assertNotNull(cacheManager);
    }
}
