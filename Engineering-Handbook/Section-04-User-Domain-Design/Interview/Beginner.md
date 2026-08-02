# Section 04 User Domain Interview Questions: Beginner Tier

## Q1: What is the difference between Hard Delete and Soft Delete?
- **Ideal Answer**: Hard delete permanently removes the row from the database (`DELETE FROM users`). Soft delete updates a column (e.g., `deleted_at = NOW()`) to mark the record as inactive while preserving historical data for foreign key references and compliance auditing.
- **Common Wrong Answer**: *"Soft delete means deleting data from cache only."*
- **Follow-up Question**: How do you query only non-deleted records efficiently in PostgreSQL? (Use a partial index `WHERE deleted_at IS NULL`).
- **Interview Tip**: Mention audit compliance and foreign key integrity.

## Q2: Why do enterprise systems enforce account locking after multiple failed login attempts?
- **Ideal Answer**: Account locking defends against automated online brute-force attacks where an attacker attempts thousands of password combinations against a single user account. Locking the account after 5 failed attempts for 15 minutes throttles attack execution speed.
- **Common Wrong Answer**: *"To force users to pay a fine for forgetting their password."*
- **Follow-up Question**: What HTTP status code should be returned when an account is locked? (`423 Locked`).
- **Interview Tip**: Link account locking to OWASP authentication security controls.
