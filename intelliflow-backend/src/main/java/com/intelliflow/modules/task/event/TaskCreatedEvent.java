package com.intelliflow.modules.task.event;

import com.intelliflow.common.event.DomainEvent;
import com.intelliflow.modules.task.domain.TaskPriority;
import lombok.Getter;

import java.util.UUID;

@Getter
public class TaskCreatedEvent extends DomainEvent {

    private final UUID taskId;
    private final String title;
    private final UUID creatorId;
    private final UUID assigneeId;
    private final TaskPriority priority;

    public TaskCreatedEvent(UUID taskId, String title, UUID creatorId, UUID assigneeId, TaskPriority priority) {
        super("TASK_CREATED");
        this.taskId = taskId;
        this.title = title;
        this.creatorId = creatorId;
        this.assigneeId = assigneeId;
        this.priority = priority;
    }
}
