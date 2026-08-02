package com.intelliflow.modules.document.service.parsing;

import com.intelliflow.modules.document.storage.exception.StorageException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;

import java.io.InputStream;

/**
 * Apache Tika Implementation of TextParsingService.
 * 
 * Leverages Apache Tika facade to extract text content across 1000+ file formats.
 */
@Slf4j
@Service
public class ApacheTikaTextParsingService implements TextParsingService {

    private final Tika tika;

    public ApacheTikaTextParsingService() {
        this.tika = new Tika();
        this.tika.setMaxStringLength(10 * 1024 * 1024); // 10 MB text buffer limit
    }

    @Override
    public String parseText(InputStream inputStream, String mimeType) {
        try {
            String extractedText = tika.parseToString(inputStream);
            if (extractedText == null || extractedText.isBlank()) {
                log.warn("Extracted text payload is empty for MIME type: {}", mimeType);
                return "";
            }
            return extractedText.trim();
        } catch (Exception e) {
            log.error("Failed to parse text payload with Apache Tika for MIME type: {}", mimeType, e);
            throw new StorageException("Failed to parse document text payload", e);
        }
    }
}
