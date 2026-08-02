package com.intelliflow.modules.document.storage.service;

import com.intelliflow.modules.document.storage.config.StorageProperties;
import com.intelliflow.modules.document.storage.exception.StorageException;
import com.intelliflow.modules.document.storage.exception.StorageFileNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LocalFileSystemStorageServiceTest {

    private LocalFileSystemStorageService storageService;

    private StorageProperties properties;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        properties = new StorageProperties();
        properties.getLocal().setBaseDir(tempDir.toString());
        properties.setMaxFileSizeBytes(1024 * 1024); // 1 MB
        properties.setAllowedMimeTypes(List.of("text/plain", "application/pdf"));

        storageService = new LocalFileSystemStorageService(properties);
    }

    @Test
    @DisplayName("storeFile - Should store file on disk and return valid StorageResult with SHA-256")
    void storeFile_Success() {
        byte[] content = "Hello IntelliFlow Knowledge Engine".getBytes(StandardCharsets.UTF_8);
        InputStream inputStream = new ByteArrayInputStream(content);

        FileStorageService.StorageResult result = storageService.storeFile(inputStream, "test_document.txt", "text/plain", content.length);

        assertNotNull(result);
        assertNotNull(result.fileKey());
        assertTrue(result.fileKey().contains("test_document.txt"));
        assertNotNull(result.checksumSha256());
        assertEquals(content.length, result.fileSizeBytes());
        assertTrue(storageService.fileExists(result.fileKey()));
    }

    @Test
    @DisplayName("storeFile - Should reject path traversal attempt in filename")
    void storeFile_PathTraversalSanitized() {
        byte[] content = "Path Traversal Test".getBytes(StandardCharsets.UTF_8);
        InputStream inputStream = new ByteArrayInputStream(content);

        FileStorageService.StorageResult result = storageService.storeFile(inputStream, "../../../etc/passwd", "text/plain", content.length);

        assertNotNull(result);
        assertFalse(result.fileKey().contains(".."));
        assertTrue(storageService.fileExists(result.fileKey()));
    }

    @Test
    @DisplayName("storeFile - Should throw StorageException when file size exceeds limit")
    void storeFile_ExceedsSize_ThrowsException() {
        byte[] content = new byte[100];
        InputStream inputStream = new ByteArrayInputStream(content);

        assertThrows(StorageException.class, () ->
                storageService.storeFile(inputStream, "large.txt", "text/plain", 2 * 1024 * 1024));
    }

    @Test
    @DisplayName("loadFileAsInputStream - Should throw StorageFileNotFoundException for non-existing key")
    void loadFile_NotFound_ThrowsException() {
        assertThrows(StorageFileNotFoundException.class, () ->
                storageService.loadFileAsInputStream("non_existing_file_key.txt"));
    }
}
