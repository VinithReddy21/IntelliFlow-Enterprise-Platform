# Section 04 User Domain Interview Questions: Intermediate Tier

## Q1: How do you implement automated audit fields (`created_at`, `updated_at`) in Spring Data JPA?
- **Ideal Answer**: We annotate the Spring Boot application configuration with `@EnableJpaAuditing`. On entity models, we add `@EntityListeners(AuditingEntityListener.class)` and annotate timestamp fields with `@CreatedDate` and `@LastModifiedDate` using `java.time.Instant`. JPA automatically populates UTC timestamps on `persist` and `update` lifecycle events.
- **Common Wrong Answer**: *"Manually write `user.setUpdatedAt(new Date())` inside every controller method."*
- **Follow-up Question**: How do you automate `created_by` and `updated_by` fields? (Implement `AuditorAware<UUID>` interface querying `SecurityContextHolder`).
- **Interview Tip**: Emphasize Aspect-Oriented Programming (AOP) entity listeners over manual code setters.

## Q2: Why is BCrypt cost factor 12 preferred over Argon2id for V1.0 Spring Boot applications?
- **Ideal Answer**: BCrypt with cost factor 12 applies 4,096 hashing iterations per evaluation (~250ms work factor), providing strong protection against offline rainbow-table attacks with minimal RAM consumption. Argon2id requires ~64MB RAM per hash, which can cause Garbage Collection spikes and Out-Of-Memory (OOM) crashes under concurrent login bursts unless complex C-native memory tuning is performed. BCrypt offers native Java support and zero RAM strain.
- **Common Wrong Answer**: *"BCrypt is faster and uses less CPU."*
- **Follow-up Question**: How do you support future algorithm upgrades without breaking existing user hashes? (Use Spring Security's `DelegatingPasswordEncoder`).
- **Interview Tip**: Contrast CPU execution cost against JVM memory footprint overhead.
