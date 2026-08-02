# Section 04 Interview Questions: Beginner Tier

## Q1: What is the difference between Authentication and Authorization?
- **Ideal Answer**: Authentication is identity verification ("Who are you?"), such as validating a username and password. Authorization is access permission determination ("What are you allowed to do?"), such as checking if a user has the `ROLE_ADMIN` role to delete a user record.
- **Common Wrong Answer**: *"They are synonyms for logging into a website."*
- **Follow-up Question**: Which comes first during an API request processing cycle? (Authentication always precedes Authorization).
- **Interview Tip**: Use the passport analogy: Authentication is your passport proving who you are; Authorization is your visa granting permission to enter a country.

## Q2: What is a JSON Web Token (JWT), and what are its three parts?
- **Ideal Answer**: A JWT (RFC 7519) is a compact, URL-safe standard for transmitting JSON claims signed digitally. It consists of three parts separated by dots (`.`):
  1. **Header**: Specifies algorithm (e.g. RS256) and token type.
  2. **Payload**: Contains claims (user ID, role, expiration `exp`, JWT ID `jti`).
  3. **Signature**: Cryptographic signature generated using the issuer's private key.
- **Common Wrong Answer**: *"A JWT is an encrypted database password."*
- **Follow-up Question**: Are claims in a JWT payload encrypted by default? (No, base64url encoded; readable by anyone unless using JWE).
- **Interview Tip**: Emphasize that standard JWTs are signed, not encrypted.

## Q3: Why should sensitive tokens never be stored in `localStorage`?
- **Ideal Answer**: `localStorage` is accessible by any JavaScript code running on the same domain context. If the application has an XSS (Cross-Site Scripting) vulnerability, a malicious script can read tokens from `localStorage` and transmit them to an attacker.
- **Common Wrong Answer**: *"Because `localStorage` clears when you close the browser tab."*
- **Follow-up Question**: Where should refresh tokens be stored instead? (In an `HttpOnly; Secure; SameSite=Strict` cookie).
- **Interview Tip**: Connect storage mechanism directly to XSS attack vectors.
