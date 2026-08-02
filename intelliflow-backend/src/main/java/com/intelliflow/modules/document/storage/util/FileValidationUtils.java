package com.intelliflow.modules.document.storage.util;

import com.intelliflow.modules.document.storage.exception.StorageException;

import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;

/**
 * Security and Validation Utilities for Storage Operations.
 * 
 * Enforces path sanitization against directory traversal attacks (CWE-22),
 * file size constraints, MIME type white-lists, and streaming SHA-256 checksums.
 */
public final class FileValidationUtils {

    private FileValidationUtils() {
    }

    /**
     * Sanitizes a filename to prevent path traversal attacks.
     */
    public static String sanitizeFilename(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            return "unnamed_file";
        }

        // Strip path separators and null bytes
        String sanitized = originalFilename.replaceAll("[/\\\\]", "_")
                .replaceAll("\\x00", "")
                .trim();

        // Prevent directory traversal sequences
        while (sanitized.contains("..")) {
            sanitized = sanitized.replace("..", "_");
        }

        return sanitized;
    }

    /**
     * Validates file size and MIME type whitelist.
     */
    public static void validateFile(String contentType, long sizeBytes, long maxSizeBytes, List<String> allowedMimeTypes) {
        if (sizeBytes <= 0) {
            throw new StorageException("Cannot upload an empty file");
        }

        if (sizeBytes > maxSizeBytes) {
            throw new StorageException("File size exceeds maximum permitted limit of " + (maxSizeBytes / (1024 * 1024)) + " MB");
        }

        if (contentType != null && allowedMimeTypes != null && !allowedMimeTypes.isEmpty()) {
            boolean isAllowed = allowedMimeTypes.stream()
                    .anyMatch(type -> type.equalsIgnoreCase(contentType) || contentType.startsWith(type));
            if (!isAllowed) {
                throw new StorageException("File MIME type '" + contentType + "' is not permitted");
            }
        }
    }

    /**
     * Computes hex SHA-256 checksum over a stream while reading bytes.
     */
    public static String calculateSha256Checksum(InputStream inputStream) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
            return HexFormat.of().formatHex(digest.digest());
        } catch (Exception e) {
            throw new StorageException("Failed to calculate SHA-256 checksum", e);
        }
    }
}
