# 🛡️ IntelliFlow Platform — Enterprise Security Checklist

## 1. Secret & Credentials Verification

- [x] **Zero Hardcoded Passwords**: All database passwords externalized via `${SPRING_DATASOURCE_PASSWORD}` and `DATABASE_URL`.
- [x] **Zero Hardcoded API Keys**: OpenAI keys externalized via `${OPENAI_API_KEY}`.
- [x] **Zero Hardcoded JWT Keys**: Default JWT secrets removed from configuration; production key length validated at startup.
- [x] **Git Repository Safety**: `.gitignore` configured to ignore `.env`, `.env.production`, `*.pem`, `*.key`, and build artifacts.

---

## 2. Environment Variable Verification

- [x] **Entropy Validation**: `ProductionConfigValidator.java` verifies runtime `JWT_SECRET` contains at least 32 characters (256-bit entropy).
- [x] **Isolated Profiles**: Production environment uses `SPRING_PROFILES_ACTIVE=prod` to prevent SQL/error leaks.
- [x] **Fail-Fast Configuration**: Application fails to start if critical database or Redis environment variables are missing.

---

## 3. CORS Policy Verification

- [x] **Explicit Origin Restriction**: `SecurityConfig.java` enforces explicit origin matching via `${ALLOWED_ORIGINS}`.
- [x] **Wildcard Prevention in Production**: Wildcard `*` headers disabled for authenticated routes.
- [x] **Pre-Flight Handling**: `OPTIONS` pre-flight requests configured with 3600s cache max-age.

---

## 4. JWT Authentication & RBAC Verification

- [x] **Stateless Sessions**: Spring Security session creation policy set strictly to `SessionCreationPolicy.STATELESS`.
- [x] **Token Expiration**: Access tokens expire in 15 minutes (`900000ms`); Refresh tokens expire in 7 days (`604800000ms`).
- [x] **Signature Algorithm**: Signed with HMAC-SHA256 signature verification.
- [x] **Method-Level Security**: `@EnableMethodSecurity` active for role enforcement (`@PreAuthorize("hasRole('ADMIN')")`).

---

## 5. Cache & Rate Limiting Verification

- [x] **TLS/SSL Encryption**: Upstash Redis configured with SSL (`SPRING_DATA_REDIS_SSL=true`).
- [x] **RateLimitingFilter**: Sliding window token bucket rate limiter active (10 req/min on Auth endpoints).
- [x] **Idempotency Protection**: `IdempotencyFilter` caches POST response body by `Idempotency-Key` header for 24 hours.

---

## 6. Database Security Verification

- [x] **SSL Mode Required**: Neon PostgreSQL connection uses `sslmode=require`.
- [x] **Parameterized Queries**: JPA/Hibernate parameter binding prevents SQL injection vulnerabilities.
- [x] **ABAC Department Isolation**: Vector similarity queries strictly filter candidates by user department authority.
