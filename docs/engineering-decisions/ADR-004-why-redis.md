# ADR-004: Selection of Redis 7.x for Distributed Caching, Token Revocation & Rate Limiting

## Status
**ACCEPTED**

## Context
Our enterprise platform requires sub-millisecond session validation, JWT refresh token lifecycle management, API rate-limiting per tenant, and distributed caching to protect PostgreSQL from repetitive read queries.

## Alternatives Considered

### 1. Memcached
- **Pros**: High-speed simple key-value caching.
- **Cons**: Lacks advanced data structures (Hashes, Sets, Sorted Sets, Streams), persistence mechanisms, and atomic script execution (Lua).

### 2. In-Memory JVM Cache (Caffeine / Ehcache)
- **Pros**: Zero network overhead; stored directly in Java heap.
- **Cons**: Cannot be shared across horizontally scaled Spring Boot instances; invalidation across multi-pod deployments requires complex pub/sub synchronization.

### 3. Redis 7.x (SELECTED)
- **Pros**: Multi-purpose operational engine providing sub-millisecond key-value caching, atomic sliding-window rate limiting via Lua, stateful refresh token tracking, and async background event queuing via Redis Streams.
- **Cons**: RAM-bound memory capacity requires strict TTL management and eviction policies (`allkeys-lru`).

## Decision
We select **Redis 7.x** as our centralized caching, token store, and rate-limiting infrastructure.

## Interview Defense & Key Summary
> *"Redis acts as our platform's shared operational state. It allows us to keep our application servers completely stateless by storing refresh tokens, token revocation blacklists, and rate-limiting buckets in a centralized, sub-millisecond memory store."*
