package com.intelliflow.common.security.hardening;

import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

/**
 * File Upload Size, Path Sanitization, and MIME Type Security Validator.
 */
public final class FileUploadValidationUtil {

    private static final long MAX_FILE_SIZE_BYTES = 50 * 1024 * 1024L; // 50 MB
    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "application/pdf",
            "text/plain",
            "text/markdown",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/json",
            "image/png",
            "image/jpeg"
    );

    private FileUploadValidationUtil() {}

    public static void validateUpload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file cannot be null or empty");
        }

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new IllegalArgumentException(String.format("File size (%d bytes) exceeds maximum allowable limit of %d bytes (50 MB)", file.getSize(), MAX_FILE_SIZE_BYTES));
        }

        String contentType = file.getContentType();
        if (contentType != null && !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException(String.format("Unsupported MIME type: '%s'. Allowed types: %s", contentType, ALLOWED_MIME_TYPES));
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null && (originalFilename.contains("..") || originalFilename.contains("/") || originalFilename.contains("\\"))) {
            throw new IllegalArgumentException("Filename contains invalid path traversal characters");
        }
    }
}
