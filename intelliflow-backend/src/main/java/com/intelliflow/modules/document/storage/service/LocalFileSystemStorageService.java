package com.intelliflow.modules.document.storage.service;

import com.intelliflow.modules.document.storage.config.StorageProperties;
import com.intelliflow.modules.document.storage.exception.StorageException;
import com.intelliflow.modules.document.storage.exception.StorageFileNotFoundException;
import com.intelliflow.modules.document.storage.util.FileValidationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.UUID;

/**
 * Local File System Implementation of FileStorageService.
 * 
 * Manages local disk storage with strict path sanitization against directory traversal.
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "intelliflow.storage.provider", havingValue = "local", matchIfMissing = true)
public class LocalFileSystemStorageService implements FileStorageService {

    private final Path rootLocation;
    private final StorageProperties storageProperties;

    public LocalFileSystemStorageService(StorageProperties storageProperties) {
        this.storageProperties = storageProperties;
        this.rootLocation = Paths.get(storageProperties.getLocal().getBaseDir()).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize local storage location", e);
        }
    }

    @Override
    public StorageResult storeFile(InputStream inputStream, String originalFilename, String contentType, long sizeBytes) {
        FileValidationUtils.validateFile(contentType, sizeBytes, storageProperties.getMaxFileSizeBytes(), storageProperties.getAllowedMimeTypes());

        String sanitizedName = FileValidationUtils.sanitizeFilename(originalFilename);
        String fileKey = UUID.randomUUID() + "_" + sanitizedName;

        Path destinationFile = this.rootLocation.resolve(fileKey).normalize();
        if (!destinationFile.getParent().equals(this.rootLocation)) {
            throw new StorageException("Cannot store file outside current directory boundary");
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            try (DigestInputStream digestInputStream = new DigestInputStream(inputStream, digest)) {
                Files.copy(digestInputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
            String checksum = HexFormat.of().formatHex(digest.digest());

            log.info("Successfully stored file locally with fileKey: {} (Size: {} bytes)", fileKey, sizeBytes);
            return new StorageResult(fileKey, checksum, sizeBytes, contentType);
        } catch (Exception e) {
            throw new StorageException("Failed to store file " + originalFilename, e);
        }
    }

    @Override
    public InputStream loadFileAsInputStream(String fileKey) {
        try {
            Path file = resolvePath(fileKey);
            if (!Files.exists(file) || !Files.isReadable(file)) {
                throw new StorageFileNotFoundException("Could not read file: " + fileKey);
            }
            return Files.newInputStream(file);
        } catch (IOException e) {
            throw new StorageFileNotFoundException("Could not read file: " + fileKey, e);
        }
    }

    @Override
    public void deleteFile(String fileKey) {
        try {
            Path file = resolvePath(fileKey);
            Files.deleteIfExists(file);
            log.info("Successfully deleted file: {}", fileKey);
        } catch (IOException e) {
            throw new StorageException("Failed to delete file: " + fileKey, e);
        }
    }

    @Override
    public boolean fileExists(String fileKey) {
        try {
            Path file = resolvePath(fileKey);
            return Files.exists(file);
        } catch (Exception e) {
            return false;
        }
    }

    private Path resolvePath(String fileKey) {
        String sanitizedKey = FileValidationUtils.sanitizeFilename(fileKey);
        Path resolved = this.rootLocation.resolve(sanitizedKey).normalize();
        if (!resolved.startsWith(this.rootLocation)) {
            throw new StorageException("Invalid file key path traversal attempt");
        }
        return resolved;
    }
}
