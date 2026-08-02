# Section 04: User Domain Design Mastery Checklist

Verify your deep understanding of every concept before proceeding to User Management code implementation:

- [ ] **User State Machine**: Can you trace valid state transitions from `PENDING_VERIFICATION` to `ACTIVE`, `LOCKED`, `SUSPENDED`, and `DELETED`?
- [ ] **Account Locking Mechanics**: Can you explain how checking `failed_login_attempts` and `lockout_until` protects against online brute-force guessing?
- [ ] **Soft Delete & Partial Indexes**: Can you explain why `WHERE deleted_at IS NULL` partial indexes allow soft-deleted user retention without breaking unique email rules?
- [ ] **JPA Auditing**: Can you explain how `@EntityListeners(AuditingEntityListener.class)` auto-populates `created_at` and `updated_at`?
- [ ] **ADR-007 Justification**: Can you explain why BCrypt cost factor 12 was chosen over Argon2id for V1.0 to avoid JVM Garbage Collection spikes?
- [ ] **REST API Design**: Can you describe the request/response contract for `/api/v1/users/me`, `/api/v1/users/{id}/status`, and password update routes?
- [ ] **Password Validation**: Can you state the regex pattern enforcing min 10 characters, uppercase, lowercase, numeric, and special character rules?
