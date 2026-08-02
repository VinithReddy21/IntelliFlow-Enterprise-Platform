package com.intelliflow.modules.document.event;

import com.intelliflow.common.event.DomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DocumentProcessedEvent extends DomainEvent {

    private final UUID documentId;
    private final int totalChunks;

    public DocumentProcessedEvent(UUID documentId, int totalChunks) {
        super("DOCUMENT_PROCESSED");
        this.documentId = documentId;
        this.totalChunks = totalChunks;
    }
}
