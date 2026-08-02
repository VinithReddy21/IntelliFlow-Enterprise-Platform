# Section 04: Authentication Mastery Checklist

Verify your deep understanding of every concept before proceeding to Section 4.2 (Code Implementation):

- [ ] **AuthN vs AuthZ**: Can you explain why authentication verifies identity while authorization determines permissions?
- [ ] **Session Evolution**: Can you explain why stateful session cookies fail to scale across multi-pod microservices without sticky sessions?
- [ ] **RS256 vs HS256**: Can you explain why asymmetric private/public keys protect downstream microservices from token forgery?
- [ ] **Dual-Token System**: Can you explain why Access Tokens are short-lived (15m) while Refresh Tokens are long-lived (7d) in Redis?
- [ ] **Refresh Token Rotation**: Can you trace the exact sequence when an old refresh token is reused by an attacker?
- [ ] **Redis Blacklisting**: Can you explain how adding JWT `jti` claims to Redis blacklists on logout enables instant revocation?
- [ ] **XSS vs CSRF**: Can you explain why refresh tokens belong in `HttpOnly; Secure; SameSite=Strict` cookies while access tokens belong in HTTP headers?
- [ ] **Password Hashing**: Can you explain why Argon2id or BCrypt with unique salts are required instead of SHA-256 or MD5?
- [ ] **Rate Limiting**: Can you explain how Redis Lua scripts prevent brute force and credential stuffing attacks?
- [ ] **End-to-End Sequence**: Can you trace the full flow for Login, Logout, Refresh Token, Password Reset, and Email Verification?
