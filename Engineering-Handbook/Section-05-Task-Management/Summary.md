# Section 05: Task Management — 5-Minute Architecture Summary

## Key Domain Decisions

1. **DDD Aggregate Root**: `TaskEntity` acts as the aggregate root. Comments, attachments, activity logs, and labels can only be mutated through `TaskEntity` transaction boundaries.
2. **7-State Machine**: `BACKLOG` -> `TODO` -> `IN_PROGRESS` -> `BLOCKED` -> `IN_REVIEW` -> `COMPLETED` -> `ARCHIVED`.
3. **Database Normalization**: Normalized PostgreSQL 16 schema with `tasks`, `task_comments`, `task_labels`, `task_assignees`, `task_activity_logs`, and `attachments`.
4. **ABAC Security**: Tasks enforce Department and Tenant isolation. Creators, Assignees, and Managers can edit tasks; regular employees can only update task progress.
5. **Partial Indexes**: `CREATE INDEX idx_tasks_active ON tasks(creator_id, status) WHERE deleted_at IS NULL;` for sub-millisecond query performance.
