package com.intelliflow.modules.task.event;

import com.intelliflow.common.event.DomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class TaskAssignedEvent extends DomainEvent {

    private final UUID taskId;
    private final UUID oldAssigneeId;
    private final UUID newAssigneeId;

    public TaskAssignedEvent(UUID taskId, UUID oldAssigneeId, UUID newAssigneeId) {
        super("TASK_ASSIGNED");
        this.taskId = taskId;
        this.oldAssigneeId = oldAssigneeId;
        this.newAssigneeId = newAssigneeId;
    }
}
