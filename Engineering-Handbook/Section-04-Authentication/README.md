# Section 04: Authentication Engineering & Security Architecture

---

## 1. Prerequisites

Before reading this section, you should understand:
- HTTP REST API basics (Headers, Status Codes, Cookies).
- High-level concepts of Cryptography (Hashing vs Symmetric vs Asymmetric Encryption).
- Basic database CRUD operations and Redis in-memory storage.

---

## 2. Learning Objectives

After completing Section 04, you will master:
- **Authentication Core Concepts**: The fundamental distinction between Authentication ("Who are you?") and Authorization ("What are you allowed to do?").
- **Evolution of Identity Systems**: How web authentication evolved from monolithic Stateful Sessions to Cookies, Tokens, OAuth 2.0, asymmetric RS256 JWTs, and Refresh Token Rotation.
- **Security Attack Vectors & OWASP Safeguards**: Defending against XSS, CSRF, Token Theft, Replay Attacks, Brute Force, and Credential Stuffing.
- **Stateful JWT Revocation with Redis**: Why purely stateless JWTs fail in enterprise logouts and how Redis blacklists and refresh family tracking bridge the gap.
- **Architectural Flow Design**: Designing end-to-end Login, Logout, Token Refresh, Password Reset, and Email Verification sequences.

---

## 3. Implementation Checklist (Section 4.2 Code Verified)

Verify that you understand every implemented Section 4.2 component:
- [x] **User Domain**: `UserEntity`, `RoleEnum`, `UserStatus`, `UserRepository`.
- [x] **Password Hashing**: `PasswordEncoderConfig` configured with `BCryptPasswordEncoder(12)`.
- [x] **JWT Core**: `JwtTokenProvider` handling token generation, signature validation, and `jti` extraction.
- [x] **Stateful Refresh Lifecycle**: `RefreshTokenService` using Redis keys with 7-day TTL.
- [x] **Security Filter Chain**: `SecurityConfig`, `JwtAuthenticationFilter`, `CustomAuthenticationEntryPoint`.
- [x] **DTOs & Controllers**: `LoginRequestDto`, `JwtResponseDto`, `RefreshTokenRequestDto`, `AuthController`.
- [x] **Unit Test Suite**: `JwtTokenProviderTest` and `AuthServiceTest` passing cleanly.

---

## 4. Class-by-Class Engineering Architecture (Section 4.2 Implementation)

### 1. `PasswordEncoderConfig.java`
- **Why it exists**: Registers a `PasswordEncoder` bean using `BCryptPasswordEncoder(12)`.
- **Security Rationale**: Cost factor 12 applies $2^{12} = 4096$ hashing iterations per password, targeting ~250ms per evaluation. This slows down offline brute-force attacks on database dumps while remaining responsive for legitimate user logins.

### 2. `JwtTokenProvider.java`
- **Why it exists**: Encapsulates HMAC-SHA256 / RS256 token issuance and signature parsing.
- **Design**: Injects `jwt.secret` and `jwt.expiration-ms` (15m). Embeds `jti` (UUID), `sub` (userId), `email`, and `role` claims into signed JWTs.

### 3. `JwtAuthenticationFilter.java`
- **Why it exists**: Extends `OncePerRequestFilter` to intercept HTTP requests, extract Bearer tokens, check Redis `auth:bl_tok:<jti>` blacklists, and populate `SecurityContextHolder`.

### 4. `SecurityConfig.java`
- **Why it exists**: Configures Spring Security 6 stateless filter chain. Permits public auth routes (`/api/v1/auth/login`, `/refresh`, Swagger) while requiring Bearer authentication for all other routes.

---

## 5. Security Attack Vectors & OWASP Defenses

| Attack Vector | Attack Mechanism | OWASP Defense Implemented in IntelliFlow |
| :--- | :--- | :--- |
| **XSS (Cross-Site Scripting)** | Malicious JS script executes in browser and reads `localStorage` tokens. | Store Refresh Tokens in `HttpOnly; Secure; SameSite=Strict` cookies. Never store tokens in `localStorage`. |
| **CSRF (Cross-Site Request Forgery)** | Malicious site tricks victim browser into sending authenticated cookie requests. | Access Token passed via `Authorization: Bearer` header; `SameSite=Strict` on cookies. |
| **Token Theft & Replay** | Attacker intercepts a valid token and reuses it. | Short Access Token TTL (15m) + Refresh Token Rotation (old refresh token reuse revokes entire session family). |
| **Brute Force & Stuffing** | Automated bots test thousands of breached credentials against `/login`. | Redis Lua script sliding-window rate limiting per IP + BCrypt cost 12 password hashing. |

---

## 6. Enterprise Dual-Token Architecture

```
Client (Browser)                 Spring Boot Auth                   Redis Token Store
   │                                    │                                  │
   │ 1. POST /api/v1/auth/login         │                                  │
   ├───────────────────────────────────►│                                  │
   │                                    │ 2. Validate Password (BCrypt 12)  │
   │                                    │ 3. Sign Access Token (15m, jti)  │
   │                                    │ 4. Generate Refresh Token UUID   │
   │                                    │ 5. Store Refresh Token Hash ─────┼──► SET auth:ref:<userId>:<tokenId>
   │ 6. Return Access Token in JSON     │                                  │    TTL 7 Days
   │    & Refresh Token Payload         │                                  │
   ◄────────────────────────────────────┤                                  │
```
