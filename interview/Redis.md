# Redis Interview Knowledge Base

## Q1: How does Redis support API Rate Limiting and Session Revocation?

### Level 1 — Campus Placement Answer
> "Redis is an in-memory database that stores data in key-value pairs. It is super fast, so we use it to count API requests per user for rate limiting and store logged-out token IDs to block revoked tokens."

### Level 2 — Product Company Answer
> "Redis runs in memory, providing sub-millisecond read/write speeds. For API Rate Limiting, we use the Sliding Window Algorithm executed via atomic Redis Lua scripts to track request timestamps per IP/user key. For Session Revocation, we store refresh token hashes in Redis with a 7-day TTL. When a user logs out, we write the JWT's `jti` to a Redis blacklist with a TTL equal to the token's remaining lifetime."

### Level 3 — Senior Engineer Answer
> "Redis handles our platform's shared operational state. By executing rate-limiting logic inside Lua scripts (`EVAL`), Redis runs the script atomically on a single thread, eliminating race conditions across horizontally scaled application instances. For caching domain objects (Cache-Aside pattern), we configure Redis max-memory eviction policies to `allkeys-lru` (Least Recently Used) to prevent Out-Of-Memory (OOM) crashes. For critical auth tokens, we store them in a separate Redis logical database or key namespace with `noeviction` guarantees."
