# Section 04: User Domain Design — 5-Minute Summary

## Key Design Patterns

1. **User States**: `PENDING_VERIFICATION`, `ACTIVE`, `LOCKED`, `SUSPENDED`, `DELETED`.
2. **Account Locking**: Locks account for 15 minutes after 5 consecutive failed login attempts.
3. **Soft Deletion**: Uses `deleted_at` timestamp column to preserve audit trail integrity and prevent foreign key cascade errors.
4. **JPA Auditing**: Automatically populates `created_at` and `updated_at` timestamps using `@EnableJpaAuditing`.
5. **Password Policy**: Minimum 10 characters, requiring uppercase, lowercase, numeric, and special character.
