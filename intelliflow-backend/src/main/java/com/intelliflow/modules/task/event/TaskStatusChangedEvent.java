package com.intelliflow.modules.task.event;

import com.intelliflow.common.event.DomainEvent;
import com.intelliflow.modules.task.domain.TaskStatus;
import lombok.Getter;

import java.util.UUID;

@Getter
public class TaskStatusChangedEvent extends DomainEvent {

    private final UUID taskId;
    private final TaskStatus oldStatus;
    private final TaskStatus newStatus;

    public TaskStatusChangedEvent(UUID taskId, TaskStatus oldStatus, TaskStatus newStatus) {
        super("TASK_STATUS_CHANGED");
        this.taskId = taskId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }
}
