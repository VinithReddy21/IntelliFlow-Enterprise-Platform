package com.intelliflow.modules.document.event;

import com.intelliflow.common.event.DomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DocumentUploadedEvent extends DomainEvent {

    private final UUID documentId;
    private final String title;
    private final String mimeType;
    private final UUID uploaderId;

    public DocumentUploadedEvent(UUID documentId, String title, String mimeType, UUID uploaderId) {
        super("DOCUMENT_UPLOADED");
        this.documentId = documentId;
        this.title = title;
        this.mimeType = mimeType;
        this.uploaderId = uploaderId;
    }
}
