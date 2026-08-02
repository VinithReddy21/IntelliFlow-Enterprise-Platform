# Section 04 Interview Questions: Advanced Tier

## Q1: How do you implement immediate stateless JWT revocation across microservices without introducing database latency bottlenecks?
- **Ideal Answer**: Purely stateless JWTs cannot be revoked before natural expiration (`exp`). To achieve instant revocation (e.g. on logout or security events) while preserving high throughput:
  1. Inject a unique `jti` (JWT ID) UUID claim in every access token.
  2. Maintain a short Access Token TTL (15 minutes).
  3. When a user logs out, write the token's `jti` to a Redis blacklist key (`SETEX blacklist:jti:<uuid> <ttl_seconds> "revoked"`).
  4. Microservice security filters check Redis (`EXISTS blacklist:jti:<uuid>`) using sub-millisecond in-memory lookups.
  5. Keys auto-expire from Redis once the token's natural `exp` time elapses, keeping memory footprints small.
- **Common Wrong Answer**: *"Query PostgreSQL on every single API request to check if the user is logged out."*
- **Follow-up Question**: What eviction policy should Redis use for blacklists? (`volatile-ttl`).
- **Interview Tip**: Emphasize bounded Redis memory footprint through short access token TTLs.
