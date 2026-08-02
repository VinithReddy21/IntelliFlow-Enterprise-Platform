package com.intelliflow.modules.document.domain;

/**
 * Lifecycle status enumeration for Enterprise Document Ingestion and Processing.
 */
public enum DocumentStatus {
    UPLOADED,
    PARSING,
    CHUNKED,
    EMBEDDED,
    ACTIVE,
    FAILED
}
