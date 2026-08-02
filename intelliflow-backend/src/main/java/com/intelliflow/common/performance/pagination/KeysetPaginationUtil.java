package com.intelliflow.common.performance.pagination;

import java.time.Instant;
import java.util.UUID;

/**
 * Keyset / Cursor-Based Pagination Utility.
 * 
 * Generates SQL conditions eliminating OFFSET performance degradation on deep page iteration.
 */
public final class KeysetPaginationUtil {

    private KeysetPaginationUtil() {}

    public static String buildKeysetCondition(Instant lastCreatedAt, UUID lastId) {
        if (lastCreatedAt == null || lastId == null) {
            return "1=1";
        }
        return String.format("(created_at < '%s' OR (created_at = '%s' AND id < '%s'))",
                lastCreatedAt.toString(), lastCreatedAt.toString(), lastId.toString());
    }
}
