package com.intelliflow;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

/**
 * Main Entry Point for IntelliFlow AI Core Backend Service.
 * 
 * Configured with Java 21 Virtual Threads and timezone standardization (UTC).
 */
@SpringBootApplication
public class IntelliflowApplication {

    @PostConstruct
    public void init() {
        // Enforce UTC timezone across all application servers to prevent timestamp skew
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    public static void main(String[] args) {
        SpringApplication.run(IntelliflowApplication.class, args);
    }
}
