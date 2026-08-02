package com.intelliflow.common.performance.batch;

import java.util.List;

/**
 * Enterprise High-Speed Batch Processing Service Contract.
 * 
 * Executes bulk batch inserts and updates bypassing Hibernate single-entity overhead.
 */
public interface BatchProcessingService {

    <T> int[] executeBatchUpdate(String sql, List<T> items, int batchSize, BatchPreparedStatementSetter<T> setter);

    @FunctionalInterface
    interface BatchPreparedStatementSetter<T> {
        void setValues(java.sql.PreparedStatement ps, T item) throws java.sql.SQLException;
    }
}
