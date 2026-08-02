# Section 05 Task Management Interview Questions: Beginner Tier

## Q1: What is an Aggregate Root in Domain-Driven Design (DDD), and why is `TaskEntity` chosen as the Aggregate Root?
- **Ideal Answer**: An Aggregate Root is the primary entry point entity that guarantees consistency across a cluster of related domain objects (an Aggregate). `TaskEntity` is the aggregate root for tasks because comments, assignees, labels, and activity logs cannot exist without a task. External services mutate child objects through `TaskEntity` transaction boundaries to enforce invariants.
- **Common Wrong Answer**: *"Aggregate root means the table with the most rows in SQL."*

## Q2: How do state machine transitions improve business workflow reliability in task management?
- **Ideal Answer**: State machines enforce explicit transition rules between task states (`TODO` -> `IN_PROGRESS` -> `IN_REVIEW` -> `COMPLETED`). They prevent illegal status skips (like moving directly from `BACKLOG` to `COMPLETED` without review) and log every transition in audit logs.
- **Common Wrong Answer**: *"State machines are just string columns with no validation."*
