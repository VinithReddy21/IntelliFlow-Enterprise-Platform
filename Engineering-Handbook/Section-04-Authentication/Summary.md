# Section 04: 5-Minute Summary

## Key Concepts Mastered

1. **Authentication vs Authorization**:
   - AuthN: "Who are you?" (Password, 2FA, JWT validation).
   - AuthZ: "What can you do?" (RBAC roles, ABAC resource ownership).

2. **Dual-Token System**:
   - **Access Token**: Short-lived (15 minutes), RS256 asymmetrically signed, passed in `Authorization: Bearer` header, validated offline by microservices.
   - **Refresh Token**: Long-lived (7 days), stored in Redis, passed in `HttpOnly; Secure; SameSite=Strict` cookie.

3. **Token Rotation & Security**:
   - **Refresh Token Rotation**: Using a refresh token revokes it and issues a new pair. If an old token is reused, Redis detects theft and invalidates the entire user session.
   - **Instant Logout / Revocation**: On logout, the Access Token `jti` is added to a Redis blacklist with a TTL equal to its remaining lifespan.

4. **OWASP Attack Prevention**:
   - **XSS**: Mitigated by HttpOnly cookies (JS cannot access tokens).
   - **CSRF**: Mitigated by Bearer headers and `SameSite=Strict` cookie flags.
   - **Brute Force**: Mitigated by Redis Lua sliding-window rate limiting.
