package com.intelliflow.modules.document.storage.service;

import com.intelliflow.modules.document.storage.config.StorageProperties;
import com.intelliflow.modules.document.storage.exception.StorageException;
import com.intelliflow.modules.document.storage.exception.StorageFileNotFoundException;
import com.intelliflow.modules.document.storage.util.FileValidationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.UUID;

/**
 * MinIO / S3 Object Storage Adapter Implementation of FileStorageService.
 * 
 * Provides cloud-native object storage streaming over S3 REST APIs.
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "intelliflow.storage.provider", havingValue = "minio")
public class MinioFileStorageService implements FileStorageService {

    private final StorageProperties storageProperties;
    private final HttpClient httpClient;

    public MinioFileStorageService(StorageProperties storageProperties) {
        this.storageProperties = storageProperties;
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
    }

    @Override
    public StorageResult storeFile(InputStream inputStream, String originalFilename, String contentType, long sizeBytes) {
        FileValidationUtils.validateFile(contentType, sizeBytes, storageProperties.getMaxFileSizeBytes(), storageProperties.getAllowedMimeTypes());

        String sanitizedName = FileValidationUtils.sanitizeFilename(originalFilename);
        String fileKey = UUID.randomUUID() + "_" + sanitizedName;

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] data;
            try (DigestInputStream digestInputStream = new DigestInputStream(inputStream, digest)) {
                data = digestInputStream.readAllBytes();
            }
            String checksum = HexFormat.of().formatHex(digest.digest());

            String targetUrl = String.format("%s/%s/%s",
                    storageProperties.getMinio().getEndpoint(),
                    storageProperties.getMinio().getBucket(),
                    fileKey);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(targetUrl))
                    .header("Content-Type", contentType != null ? contentType : "application/octet-stream")
                    .PUT(HttpRequest.BodyPublishers.ofByteArray(data))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                log.info("Successfully uploaded object to MinIO bucket '{}' with key: {}", storageProperties.getMinio().getBucket(), fileKey);
                return new StorageResult(fileKey, checksum, data.length, contentType);
            } else {
                throw new StorageException("Failed to upload object to MinIO. HTTP status: " + response.statusCode());
            }
        } catch (Exception e) {
            throw new StorageException("Error uploading file to MinIO object storage", e);
        }
    }

    @Override
    public InputStream loadFileAsInputStream(String fileKey) {
        String sanitizedKey = FileValidationUtils.sanitizeFilename(fileKey);
        String targetUrl = String.format("%s/%s/%s",
                storageProperties.getMinio().getEndpoint(),
                storageProperties.getMinio().getBucket(),
                sanitizedKey);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(targetUrl))
                    .GET()
                    .build();

            HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
            if (response.statusCode() == 200) {
                return response.body();
            } else if (response.statusCode() == 404) {
                throw new StorageFileNotFoundException("MinIO object not found: " + fileKey);
            } else {
                throw new StorageException("Failed to fetch MinIO object. HTTP status: " + response.statusCode());
            }
        } catch (StorageFileNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new StorageException("Error downloading file from MinIO object storage", e);
        }
    }

    @Override
    public void deleteFile(String fileKey) {
        String sanitizedKey = FileValidationUtils.sanitizeFilename(fileKey);
        String targetUrl = String.format("%s/%s/%s",
                storageProperties.getMinio().getEndpoint(),
                storageProperties.getMinio().getBucket(),
                sanitizedKey);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(targetUrl))
                    .DELETE()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("Deleted object from MinIO key: {}. Response: {}", fileKey, response.statusCode());
        } catch (Exception e) {
            throw new StorageException("Error deleting file from MinIO object storage", e);
        }
    }

    @Override
    public boolean fileExists(String fileKey) {
        String sanitizedKey = FileValidationUtils.sanitizeFilename(fileKey);
        String targetUrl = String.format("%s/%s/%s",
                storageProperties.getMinio().getEndpoint(),
                storageProperties.getMinio().getBucket(),
                sanitizedKey);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(targetUrl))
                    .method("HEAD", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
            return response.statusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }
}
