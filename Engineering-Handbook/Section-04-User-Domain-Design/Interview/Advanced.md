# Section 04 User Domain Interview Questions: Advanced Tier

## Q1: How do you handle unique email constraints in PostgreSQL when using soft deletes?
- **Ideal Answer**: Standard unique indexes (`CREATE UNIQUE INDEX idx_email ON users(email)`) fail with soft delete because once a user is soft-deleted, another user cannot register with that same email address.
  We solve this using a **Partial Unique Index**:
  `CREATE UNIQUE INDEX idx_users_active_email ON users(email) WHERE deleted_at IS NULL;`
  This enforces uniqueness only among active users while allowing soft-deleted historical records to retain their original email strings for audit logs.
- **Common Wrong Answer**: *"Append a random UUID to the email address when soft-deleting."*
- **Follow-up Question**: How does partial indexing affect query execution plans in PostgreSQL?
- **Interview Tip**: Name-drop partial unique indexes in PostgreSQL.
