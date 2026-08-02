# Advanced Reading: Future Authentication Technologies

*Note: These concepts serve as optional advanced reading for future authentication phases.*

---

## 1. WebAuthn & Passkeys (FIDO2 Standard)
Replaces passwords with public-key cryptography built into user devices (Touch ID, Face ID, YubiKeys). Users sign authentication challenges using device biometrics without sending secrets across network connections.

---

## 2. OAuth 2.1 & OpenID Connect (OIDC)
Industry standard protocol for federated single sign-on (SSO) (e.g., "Sign in with Google/Microsoft"). Uses Authorization Code Flow with PKCE (Proof Key for Code Exchange) to prevent code interception attacks.

---

## 3. Mutual TLS (mTLS) for Inter-Microservice Auth
Enforces bi-directional cryptographic identity verification between Spring Boot and FastAPI microservices using client/server TLS certificates issued by an internal Certificate Authority (Istio / Vault PKI).
