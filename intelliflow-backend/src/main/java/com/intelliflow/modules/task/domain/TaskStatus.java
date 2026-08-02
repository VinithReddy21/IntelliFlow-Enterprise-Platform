package com.intelliflow.modules.task.domain;

/**
 * Task lifecycle state machine status flags.
 */
public enum TaskStatus {
    BACKLOG,
    TODO,
    IN_PROGRESS,
    BLOCKED,
    IN_REVIEW,
    COMPLETED,
    ARCHIVED
}
