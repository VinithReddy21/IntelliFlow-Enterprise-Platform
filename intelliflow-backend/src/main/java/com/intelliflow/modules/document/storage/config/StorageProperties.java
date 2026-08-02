package com.intelliflow.modules.document.storage.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "intelliflow.storage")
public class StorageProperties {

    private String provider = "local";

    private Local local = new Local();

    private Minio minio = new Minio();

    private long maxFileSizeBytes = 52428800L; // 50 MB

    private List<String> allowedMimeTypes = List.of(
            "application/pdf",
            "text/plain",
            "text/markdown",
            "text/html",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/msword",
            "application/json"
    );

    @Data
    public static class Local {
        private String baseDir = "./storage/documents";
    }

    @Data
    public static class Minio {
        private String endpoint = "http://localhost:9000";
        private String bucket = "intelliflow-documents";
        private String accessKey = "minioadmin";
        private String secretKey = "minioadmin";
    }
}
