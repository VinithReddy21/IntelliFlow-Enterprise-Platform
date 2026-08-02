# ADR-007: Selection of BCrypt (Cost Factor 12) over Argon2id for V1.0 Password Hashing

## Status
**ACCEPTED**

## Context
Password hashing algorithms protect user credentials against offline dictionary, GPU, and ASIC rainbow-table attacks if a database dump is compromised. We need a secure, well-supported hashing algorithm that balances cryptographic resistance with Spring Security framework integration, memory footprint, and CPU execution latency.

## Alternatives Considered

### 1. Plain SHA-256 / MD5 (REJECTED)
- **Pros**: Extremely fast CPU execution.
- **Cons**: Cryptographically broken for password storage; lacks salt support; modern GPUs can compute over 10 billion SHA-256 hashes per second, rendering offline brute-force trivial.

### 2. Argon2id (`Argon2PasswordEncoder`)
- **Pros**: Winner of the 2015 Password Hashing Competition; offers memory-hard and time-hard parameters that severely restrict GPU and custom ASIC parallel cracking.
- **Cons**: Consumes substantial JVM heap RAM per hash evaluation (~64MB RAM per hash). Under high concurrent login bursts, memory spikes can trigger JVM Garbage Collection (GC) pauses or Out-Of-Memory (OOM) crashes unless carefully tuned with C-native bindings (`BouncyCastle` / `JNA`).

### 3. BCrypt with Cost Factor 12 (`BCryptPasswordEncoder(12)`) (SELECTED)
- **Pros**: Native integration with Spring Security (`BCryptPasswordEncoder`); automatically handles unique 128-bit salt generation per hash; cost factor 12 applies $2^{12} = 4096$ hashing rounds, targeting ~250ms evaluation time. Low memory overhead permits thousands of concurrent login attempts without JVM RAM strain.
- **Cons**: Less resistant to specialized hardware (ASICs) compared to memory-hard Argon2id.

## Decision
We select **BCrypt with Cost Factor 12** as our V1.0 password hashing standard, integrated via Spring Security's `DelegatingPasswordEncoder` abstraction to support seamless future upgrades to Argon2id without invalidating existing user password hashes.

## Consequences & Trade-offs
- **Positive**: Native Spring Security support, deterministic CPU work factor (~250ms), minimal JVM RAM usage, zero native C-library dependencies.
- **Negative**: Argon2id remains theoretically superior against high-end ASIC hardware attacks.

## Interview Defense & Key Summary
> *"We selected BCrypt with cost factor 12 for V1.0 because it provides an ideal trade-off between offline brute-force resistance (~250ms work factor per hash) and low JVM memory overhead. Unlike Argon2id, which requires ~64MB RAM per hash and native C-library bindings that can cause Garbage Collection spikes during login bursts, BCrypt runs natively in Java with negligible memory overhead. Furthermore, we wrap our encoder in Spring Security's `DelegatingPasswordEncoder`, enabling us to upgrade to Argon2id in the future without breaking existing user hashes."*
