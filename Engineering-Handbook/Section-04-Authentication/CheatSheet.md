# Section 04: 1-Minute Cheat Sheet

```
+---------------------------------------------------------------------------------------------------+
|                        SECTION 04: AUTHENTICATION ENGINEERING CHEAT SHEET                         |
+---------------------------------------------------------------------------------------------------+
| ACCESS TOKEN        | RS256 Signed JWT | TTL: 15 Mins | Header: Authorization: Bearer <JWT>       |
| REFRESH TOKEN       | UUID / Hash Store | TTL: 7 Days  | Cookie: HttpOnly; Secure; SameSite=Strict  |
| TOKEN ROTATION      | Every refresh issues new pair & invalidates old token; flags reuse theft    |
| TOKEN REVOCATION    | Logout writes JWT `jti` claim to Redis blacklist until natural expiration   |
+---------------------------------------------------------------------------------------------------+
| SECURITY DEFENSES   | XSS      -> HttpOnly cookies (No localStorage token storage)                 |
|                     | CSRF     -> Bearer HTTP Header + SameSite=Strict                             |
|                     | BRUTE    -> Redis Lua Script Rate Limiter (5 attempts / min)                |
|                     | STORAGE  -> Argon2id / BCrypt Password Hashing with unique salt            |
+---------------------------------------------------------------------------------------------------+
```
