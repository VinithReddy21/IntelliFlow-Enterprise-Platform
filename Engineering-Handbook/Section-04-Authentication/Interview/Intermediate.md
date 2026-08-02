# Section 04 Interview Questions: Intermediate Tier

## Q1: Why do we use asymmetric RS256 signing instead of symmetric HS256 for JWTs in a microservice architecture?
- **Ideal Answer**: HS256 uses a single shared secret key for both signing and verifying tokens. Every microservice that needs to validate JWTs must hold this secret key; if one downstream service (e.g. FastAPI AI microservice) is compromised, the attacker can forge valid tokens platform-wide. RS256 uses a key pair: the Auth Service holds the Private Key to sign tokens, while downstream microservices only need the Public Key to verify tokens offline.
- **Common Wrong Answer**: *"RS256 is faster than HS256."*
- **Follow-up Question**: How do downstream services receive the public key? (Via a JWKS endpoint `/oauth2/jwks` or shared config).
- **Interview Tip**: Highlight security isolation between token issuers and token verifiers.

## Q2: How does Refresh Token Rotation work, and how does it detect token theft?
- **Ideal Answer**: In Refresh Token Rotation, every time a client sends a refresh token to get a new access token, the server revokes that refresh token and returns a new Access Token + Refresh Token pair. The server maintains a token family chain in Redis. If an attacker steals an old refresh token and attempts to use it later, the server detects that a previously revoked token is being reused. It flags a **token theft event** and immediately revokes all refresh tokens in that user's token family, forcing the legitimate user to re-authenticate.
- **Common Wrong Answer**: *"Rotation means refresh tokens never expire."*
- **Follow-up Question**: How does Redis store token family hierarchies? (Using Redis Hashes or Sets).
- **Interview Tip**: Detail the reuse detection sequence step-by-step.
