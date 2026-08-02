# Section 04: User Domain Design — 1-Minute Cheat Sheet

```
+---------------------------------------------------------------------------------------------------+
|                        SECTION 04: USER DOMAIN DESIGN CHEAT SHEET                                 |
+---------------------------------------------------------------------------------------------------+
| USER STATES         | PENDING_VERIFICATION -> ACTIVE -> LOCKED -> SUSPENDED -> DELETED            |
| ACCOUNT LOCKING     | Failed Attempts >= 5 -> Set status = LOCKED, lockout_until = NOW() + 15m   |
| SOFT DELETE         | Set deleted_at = NOW(); filter active views with WHERE deleted_at IS NULL    |
| AUDIT FIELDS        | JPA @CreatedDate, @LastModifiedDate using UTC Instant timestamps             |
| PASSWORD POLICY     | Min 10 chars, regex: ^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&]).{10,}$    |
+---------------------------------------------------------------------------------------------------+
```
