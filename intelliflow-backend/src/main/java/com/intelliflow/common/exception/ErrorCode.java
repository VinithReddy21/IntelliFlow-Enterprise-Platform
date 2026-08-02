package com.intelliflow.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Platform Error Codes for RFC 7807 Exception Framework.
 */
@Getter
public enum ErrorCode {

    RESOURCE_NOT_FOUND("ERR_404_NOT_FOUND", "Requested resource was not found", HttpStatus.NOT_FOUND),
    UNAUTHORIZED("ERR_401_UNAUTHORIZED", "Authentication is required to access this resource", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("ERR_403_FORBIDDEN", "You do not have permission to access this resource", HttpStatus.FORBIDDEN),
    BAD_REQUEST("ERR_400_BAD_REQUEST", "Invalid request parameters provided", HttpStatus.BAD_REQUEST),
    INTERNAL_ERROR("ERR_500_INTERNAL", "An unexpected internal server error occurred", HttpStatus.INTERNAL_SERVER_ERROR),
    DOWNSTREAM_SERVICE_ERROR("ERR_503_DOWNSTREAM", "A downstream microservice is unavailable", HttpStatus.SERVICE_UNAVAILABLE);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    ErrorCode(String code, String defaultMessage, HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }
}
