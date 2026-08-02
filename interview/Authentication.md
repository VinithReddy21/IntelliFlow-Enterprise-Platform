# Authentication Interview Knowledge Base

## Q1: Why do we use dual-token JWT (Access Token + Refresh Token) with asymmetric RS256 signing instead of stateful sessions?

### Level 1 — Campus Placement Answer
> "Access tokens are short-lived tokens sent with every API request to prove user identity. Refresh tokens are long-lived tokens stored securely to get new access tokens when they expire. We use RS256 private/public key pairs to sign tokens securely so nobody can forge them."

### Level 2 — Product Company Answer
> "Stateful session cookies require application servers to query a central session database on every incoming request, creating performance bottlenecks. Dual-token JWTs make API calls stateless: the Access Token (TTL 15m) carries user claims and is verified locally by services using the RS256 Public Key. The Refresh Token (TTL 7d) is stored in Redis. When the access token expires, the client sends the refresh token to receive a new pair. If a user logs out or is compromised, we revoke the refresh token in Redis immediately."

### Level 3 — Senior Engineer Answer
> "In a distributed microservice architecture, asymmetric RS256 (RSA Signature with SHA-256) is superior to symmetric HS256 because only the Auth Service holds the Private Key to generate signatures. Downstream services (FastAPI, NGINX API Gateway) only need the Public Key to verify tokens offline without network calls. To prevent JWT replay attacks, we inject a unique `jti` (JWT ID) into access tokens and store revoked JTIs in Redis until their natural expiration. Furthermore, refresh tokens use **Refresh Token Rotation**: every time a refresh token is used, it is revoked and replaced with a new one. If an old refresh token is reused, Redis flags a token theft attempt and invalidates the entire user session hierarchy."
