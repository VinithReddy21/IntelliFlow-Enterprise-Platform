# Section 05 Task Management Interview Questions: Advanced Tier

## Q1: How do you handle circular task dependency deadlocks in complex task hierarchies?
- **Ideal Answer**: When a user sets Task A to depend on Task B (`Task A -> depends_on -> Task B`), we execute a Directed Acyclic Graph (DAG) cycle detection algorithm using a PostgreSQL Recursive Common Table Expression (CTE) or in-memory depth-first search (DFS). If Task B already directly or transitively depends on Task A, we reject the dependency link with a `CircularDependencyException`.
- **Common Wrong Answer**: *"Allow circular dependencies and handle them at runtime."*

## Q2: How do you optimize high-throughput task activity logging without locking core task database updates?
- **Ideal Answer**: Instead of writing activity log records (`task_activity_logs`) synchronously in the primary task write transaction, we publish asynchronous domain events (`TaskUpdatedEvent`, `TaskStatusChangedEvent`) via Spring `ApplicationEventPublisher`. Event listeners consume the event asynchronously using `@Async` workers or Kafka topics, inserting audit records into PostgreSQL without delaying the primary task API response.
- **Common Wrong Answer**: *"Perform synchronous database writes inside the same HTTP thread."*
