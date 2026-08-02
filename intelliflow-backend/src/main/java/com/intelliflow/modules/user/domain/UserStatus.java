package com.intelliflow.modules.user.domain;

/**
 * User account lifecycle state flags.
 */
public enum UserStatus {
    ACTIVE,
    PENDING_VERIFICATION,
    LOCKED,
    SUSPENDED,
    DELETED
}
