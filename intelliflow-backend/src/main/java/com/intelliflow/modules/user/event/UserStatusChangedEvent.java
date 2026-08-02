package com.intelliflow.modules.user.event;

import com.intelliflow.common.event.DomainEvent;
import com.intelliflow.modules.user.domain.UserStatus;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UserStatusChangedEvent extends DomainEvent {

    private final UUID userId;
    private final UserStatus oldStatus;
    private final UserStatus newStatus;

    public UserStatusChangedEvent(UUID userId, UserStatus oldStatus, UserStatus newStatus) {
        super("USER_STATUS_CHANGED");
        this.userId = userId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }
}
