package com.intelliflow.common.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {

    @Test
    @DisplayName("Should create successful ApiResponse envelope with data")
    void shouldCreateSuccessResponse() {
        String payload = "Test Payload";
        ApiResponse<String> response = ApiResponse.success(payload, "Custom Success");

        assertNotNull(response);
        assertEquals(ResponseStatus.SUCCESS, response.getStatus());
        assertEquals("Custom Success", response.getMessage());
        assertEquals("Test Payload", response.getData());
        assertNotNull(response.getTimestamp());
    }

    @Test
    @DisplayName("Should create error ApiResponse envelope")
    void shouldCreateErrorResponse() {
        ApiResponse<Void> response = ApiResponse.error("Something went wrong");

        assertNotNull(response);
        assertEquals(ResponseStatus.ERROR, response.getStatus());
        assertEquals("Something went wrong", response.getMessage());
        assertNull(response.getData());
        assertNotNull(response.getTimestamp());
    }
}
