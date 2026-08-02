package com.intelliflow.modules.user.event;

import com.intelliflow.common.event.DomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UserPasswordChangedEvent extends DomainEvent {

    private final UUID userId;

    public UserPasswordChangedEvent(UUID userId) {
        super("USER_PASSWORD_CHANGED");
        this.userId = userId;
    }
}
