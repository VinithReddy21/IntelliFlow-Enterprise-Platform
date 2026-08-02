# Section 05: Task Management — 1-Minute Cheat Sheet

```
+----------------------------------------------------------------------------------------------------+
|                      SECTION 05: TASK MANAGEMENT CHEAT SHEET                                       |
+----------------------------------------------------------------------------------------------------+
| AGGREGATE ROOT       | TaskEntity controls child entities (Comments, Attachments, Logs, Labels)    |
| TASK STATES          | BACKLOG -> TODO -> IN_PROGRESS -> BLOCKED -> IN_REVIEW -> COMPLETED -> ARCHIVED |
| PRIORITY LEVELS      | LOW (4), MEDIUM (3), HIGH (2), URGENT (1)                                   |
| ABAC AUTHORIZATION   | Creator/Manager: Full Edit; Assignee: Progress/Status; Others: Read Only    |
| SOFT DELETE FILTER   | Filter active lists with WHERE deleted_at IS NULL                           |
| SEARCH INDEX         | PostgreSQL GIN index on to_tsvector('english', title || ' ' || description) |
+----------------------------------------------------------------------------------------------------+
```
