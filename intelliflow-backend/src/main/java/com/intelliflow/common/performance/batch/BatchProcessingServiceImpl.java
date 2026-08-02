package com.intelliflow.common.performance.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * JDBC Batch Processing Implementation.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BatchProcessingServiceImpl implements BatchProcessingService {

    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public <T> int[] executeBatchUpdate(String sql, List<T> items, int batchSize, BatchPreparedStatementSetter<T> setter) {
        if (items == null || items.isEmpty()) {
            return new int[0];
        }

        long startTime = System.currentTimeMillis();
        log.info("Executing JDBC batch operation for {} records (Batch size: {})", items.size(), batchSize);

        int[][] result = jdbcTemplate.batchUpdate(
                sql,
                items,
                batchSize,
                (PreparedStatement ps, T argument) -> setter.setValues(ps, argument)
        );

        int totalAffected = 0;
        for (int[] batch : result) {
            for (int count : batch) {
                totalAffected += Math.max(count, 0);
            }
        }

        long duration = System.currentTimeMillis() - startTime;
        log.info("Completed JDBC batch update: Affected {} rows in {} ms", totalAffected, duration);
        return new int[]{totalAffected};
    }
}
