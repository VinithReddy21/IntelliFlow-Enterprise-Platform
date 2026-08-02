package com.intelliflow.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Enterprise Unified API Response Envelope.
 * 
 * Enforces consistent JSON contract across all platform REST APIs:
 * {
 *   "status": "SUCCESS",
 *   "message": "Operation completed successfully",
 *   "data": { ... },
 *   "timestamp": "2026-08-01T20:47:00Z"
 * }
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private ResponseStatus status;
    private String message;
    private T data;
    
    @Builder.Default
    private Instant timestamp = Instant.now();

    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .status(ResponseStatus.SUCCESS)
                .message(message)
                .data(data)
                .timestamp(Instant.now())
                .build();
    }

    public static <T> ApiResponse<T> success(T data) {
        return success(data, "Request processed successfully");
    }

    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .status(ResponseStatus.ERROR)
                .message(message)
                .timestamp(Instant.now())
                .build();
    }
}
