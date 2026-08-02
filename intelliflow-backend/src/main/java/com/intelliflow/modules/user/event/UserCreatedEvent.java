package com.intelliflow.modules.user.event;

import com.intelliflow.common.event.DomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UserCreatedEvent extends DomainEvent {

    private final UUID userId;
    private final String email;
    private final String firstName;
    private final String lastName;

    public UserCreatedEvent(UUID userId, String email, String firstName, String lastName) {
        super("USER_CREATED");
        this.userId = userId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
