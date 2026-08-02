package com.intelliflow.common.observability;

import com.intelliflow.common.observability.filter.CorrelationIdFilter;
import com.intelliflow.common.observability.health.AiServiceHealthIndicator;
import com.intelliflow.common.observability.health.DatabaseHealthIndicator;
import com.intelliflow.common.observability.health.RedisHealthIndicator;
import com.intelliflow.common.observability.health.StorageServiceHealthIndicator;
import com.intelliflow.common.observability.service.BusinessMetricsServiceImpl;
import com.intelliflow.modules.document.storage.service.FileStorageService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.boot.actuate.health.Status;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObservabilityTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private RedisConnectionFactory redisConnectionFactory;

    @Mock
    private RedisConnection redisConnection;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Test
    @DisplayName("BusinessMetricsService - Should register and increment Micrometer metrics")
    void businessMetricsService_RegistersMetrics() {
        MeterRegistry registry = new SimpleMeterRegistry();
        BusinessMetricsServiceImpl businessMetricsService = new BusinessMetricsServiceImpl(registry);

        businessMetricsService.recordUserRegistration();
        businessMetricsService.recordTaskCreation();
        businessMetricsService.recordDocumentUpload();
        businessMetricsService.recordRagQuery(120);

        assertEquals(1.0, registry.get("intelliflow.users.registered").counter().count());
        assertEquals(1.0, registry.get("intelliflow.tasks.created").counter().count());
        assertEquals(1.0, registry.get("intelliflow.documents.uploaded").counter().count());
        assertEquals(1.0, registry.get("intelliflow.rag.queries.count").counter().count());
    }

    @Test
    @DisplayName("CorrelationIdFilter - Should generate correlation ID and populate MDC")
    void correlationIdFilter_PopulatesMdc() throws Exception {
        CorrelationIdFilter filter = new CorrelationIdFilter();

        when(request.getHeader(CorrelationIdFilter.CORRELATION_ID_HEADER)).thenReturn(null);

        filter.doFilter(request, response, filterChain);

        verify(response).setHeader(eq(CorrelationIdFilter.CORRELATION_ID_HEADER), anyString());
        verify(filterChain).doFilter(request, response);
        assertNull(MDC.get(CorrelationIdFilter.MDC_CORRELATION_ID_KEY)); // MDC cleared in finally
    }

    @Test
    @DisplayName("Health Indicators - Should return UP status for custom health probes")
    void healthIndicators_ReturnUpStatus() {
        AiServiceHealthIndicator aiHealth = new AiServiceHealthIndicator();
        assertEquals(Status.UP, aiHealth.health().getStatus());

        when(jdbcTemplate.queryForObject("SELECT 1", Integer.class)).thenReturn(1);
        DatabaseHealthIndicator dbHealth = new DatabaseHealthIndicator(jdbcTemplate);
        assertEquals(Status.UP, dbHealth.health().getStatus());

        when(redisConnectionFactory.getConnection()).thenReturn(redisConnection);
        when(redisConnection.ping()).thenReturn("PONG");
        RedisHealthIndicator redisHealth = new RedisHealthIndicator(redisConnectionFactory);
        assertEquals(Status.UP, redisHealth.health().getStatus());

        when(fileStorageService.fileExists(anyString())).thenReturn(false);
        StorageServiceHealthIndicator storageHealth = new StorageServiceHealthIndicator(fileStorageService);
        assertEquals(Status.UP, storageHealth.health().getStatus());
    }
}
