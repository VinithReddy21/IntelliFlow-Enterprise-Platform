package com.intelliflow.modules.document.storage.service;

import java.io.InputStream;

/**
 * Storage Abstraction Service Interface.
 * 
 * Provider-independent contract for object store uploads, streaming downloads,
 * deletions, and path verification.
 */
public interface FileStorageService {

    /**
     * Stores a file stream safely into object storage.
     */
    StorageResult storeFile(InputStream inputStream, String originalFilename, String contentType, long sizeBytes);

    /**
     * Retrieves file payload as an InputStream.
     */
    InputStream loadFileAsInputStream(String fileKey);

    /**
     * Deletes a file from object storage.
     */
    void deleteFile(String fileKey);

    /**
     * Verifies if a file key exists.
     */
    boolean fileExists(String fileKey);

    /**
     * Value object recording storage execution outcome.
     */
    record StorageResult(
            String fileKey,
            String checksumSha256,
            long fileSizeBytes,
            String mimeType
    ) {}
}
