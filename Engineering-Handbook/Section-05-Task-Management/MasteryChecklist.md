# Section 05: Task Management Architectural Mastery Checklist

Verify your deep understanding of Task Domain Architecture before starting code implementation:

- [ ] **DDD Aggregate Root**: Can you explain why `TaskEntity` is the Aggregate Root controlling child comments and labels?
- [ ] **State Machine**: Can you trace all 7 states (`BACKLOG` -> `TODO` -> `IN_PROGRESS` -> `BLOCKED` -> `IN_REVIEW` -> `COMPLETED` -> `ARCHIVED`) and state transition rules?
- [ ] **PostgreSQL Schema (`V3__task_management.sql`)**: Do you understand foreign key cascades, partial indexes, and GIN full-text search indexes?
- [ ] **ABAC Authorization**: Can you explain the difference between RBAC role checks and ABAC task ownership/department authorization rules?
- [ ] **DAG Dependencies**: Can you explain how cycle detection prevents circular task dependency deadlocks?
- [ ] **REST API Catalog**: Can you describe filtering (`Specification`), pagination (`Pageable`), sorting, and bulk action contracts?
