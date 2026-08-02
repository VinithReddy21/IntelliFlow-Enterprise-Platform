package com.intelliflow.modules.document.event;

import com.intelliflow.common.event.DomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DocumentDeletedEvent extends DomainEvent {

    private final UUID documentId;
    private final UUID deletedByUserId;

    public DocumentDeletedEvent(UUID documentId, UUID deletedByUserId) {
        super("DOCUMENT_DELETED");
        this.documentId = documentId;
        this.deletedByUserId = deletedByUserId;
    }
}
