package com.intelliflow.modules.document.service.parsing;

import java.io.InputStream;

/**
 * Text Parsing Engine Contract.
 * 
 * Extracts clean, plain-text content from raw binary files (PDF, DOCX, TXT, HTML, Markdown).
 */
public interface TextParsingService {

    /**
     * Parses an input stream into extracted plain text.
     */
    String parseText(InputStream inputStream, String mimeType);
}
