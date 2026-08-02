# Section 05 Task Management Interview Questions: Intermediate Tier

## Q1: How do you design Attribute-Based Access Control (ABAC) for task modifications in a multi-tenant platform?
- **Ideal Answer**: While RBAC checks *who you are* (`ROLE_EMPLOYEE`), ABAC evaluates environmental and contextual attributes: `User.department_id == Task.department_id` (department scoping), `User.id == Task.creator_id` (ownership), or `User.id IN Task.assignees` (assignment). ABAC allows managers full edit rights within their department, while assignees can update task progress/comments but cannot re-assign tasks across departments.
- **Common Wrong Answer**: *"Use `@PreAuthorize("hasRole('EMPLOYEE')")` for everything."*

## Q2: How do you implement full-text search across task titles and descriptions in PostgreSQL?
- **Ideal Answer**: We create a PostgreSQL GIN (Generalized Inverted Index) over a generated tsvector column:
  `CREATE INDEX idx_tasks_fts ON tasks USING gin(to_tsvector('english', title || ' ' || description));`
  We then execute queries using `to_tsquery()` which allows sub-millisecond keyword searching over millions of tasks.
- **Common Wrong Answer**: *"Use SQL `LIKE '%keyword%'` queries."* (LIKE leads to full table scans).
