package com.intelliflow.common.performance;

import com.intelliflow.common.performance.batch.BatchProcessingServiceImpl;
import com.intelliflow.common.performance.benchmark.PerformanceBenchmarkUtil;
import com.intelliflow.common.performance.pagination.KeysetPaginationUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceOptimizationTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private BatchProcessingServiceImpl batchProcessingService;

    @Test
    @DisplayName("BatchProcessingService - Should execute JDBC batch update")
    void executeBatchUpdate_Success() {
        String sql = "INSERT INTO test (name) VALUES (?)";
        List<String> items = List.of("A", "B", "C");

        when(jdbcTemplate.batchUpdate(anyString(), anyList(), anyInt(), any())).thenReturn(new int[][]{{1, 1, 1}});

        int[] result = batchProcessingService.executeBatchUpdate(sql, items, 100, (ps, item) -> ps.setString(1, item));

        assertEquals(3, result[0]);
        verify(jdbcTemplate).batchUpdate(eq(sql), eq(items), eq(100), any());
    }

    @Test
    @DisplayName("KeysetPaginationUtil - Should generate keyset cursor SQL condition")
    void buildKeysetCondition_Success() {
        Instant now = Instant.now();
        UUID id = UUID.randomUUID();

        String condition = KeysetPaginationUtil.buildKeysetCondition(now, id);

        assertNotNull(condition);
        assertTrue(condition.contains("created_at <"));
    }

    @Test
    @DisplayName("PerformanceBenchmarkUtil - Should measure execution time and return result")
    void measureExecutionTime_Success() {
        String result = PerformanceBenchmarkUtil.measureExecutionTime("testBenchmark", () -> "SUCCESS");

        assertEquals("SUCCESS", result);
    }
}
