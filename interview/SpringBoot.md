# Spring Boot Interview Knowledge Base

## Q1: How does Spring Boot auto-configuration work, and what happens during `@SpringBootApplication` execution?

### Level 1 — Campus Placement Answer
> "In Spring Boot, `@SpringBootApplication` is a combination annotation of `@Configuration`, `@EnableAutoConfiguration`, and `@ComponentScan`. `@EnableAutoConfiguration` automatically configures beans based on dependencies present on the classpath. For example, if `postgresql` dependency is present in `pom.xml`, Spring Boot automatically configures a DataSource bean for database connection."

### Level 2 — Product Company Answer
> "Spring Boot auto-configuration operates via `SpringFactoriesLoader` (or `AutoConfiguration.imports` in Spring Boot 3.x). When the application starts, Spring scans `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`. Each auto-configuration class uses conditional annotations like `@ConditionalOnClass`, `@ConditionalOnMissingBean`, and `@ConditionalOnProperty`. This means Spring only registers a default bean (like HikariDataSource) if the developer has not already declared a custom DataSource bean."

### Level 3 — Senior Engineer Answer
> "Under the hood, `@SpringBootApplication` triggers component scanning via `ComponentScanAnnotationParser` while `AutoConfigurationImportSelector` evaluates auto-configuration classes in a deferred import phase. In Spring Boot 3.2, conditional evaluation happens using bytecode inspection before bean definition registration to minimize startup overhead. By using `@SpringBootApplication`, we get sensible defaults, but in production, we explicitly fine-tune property sources (`application.yml`), configure custom connection pools (HikariCP parameters), and disable unused auto-configurations using `@SpringBootApplication(exclude = {...})` to optimize container memory footprint."

---

## Q2: How do Virtual Threads in Java 21 change concurrency handling in Spring Boot 3.2?

### Level 1 — Campus Placement Answer
> "Virtual threads are lightweight threads introduced in Java 21. Unlike traditional platform threads that consume a lot of memory, virtual threads consume very little memory, allowing Spring Boot to process thousands of requests simultaneously without running out of RAM."

### Level 2 — Product Company Answer
> "Traditional Java web servers (like Tomcat in Spring Boot) assign one OS platform thread per incoming HTTP request. Platform threads consume ~1MB of stack memory and stall when doing database or network I/O. Java 21 Virtual Threads (Project Loom) are managed by the JVM rather than the OS kernel. When a virtual thread blocks on I/O, the JVM unmounts it from the carrier OS thread, allowing that carrier thread to execute other virtual threads. This dramatically increases throughput for I/O-bound web applications without changing synchronous code style."

### Level 3 — Senior Engineer Answer
> "In Spring Boot 3.2, enabling `spring.threads.virtual.enabled=true` replaces Tomcat's standard platform thread pool with an executor that spawns a virtual thread for each task (`Executors.newVirtualThreadPerTaskExecutor()`). This eliminates thread-pool sizing bottlenecks for I/O-heavy enterprise APIs. However, as senior engineers, we must be cautious of **pinning carrier threads** when using `synchronized` blocks or native methods blocking I/O inside virtual threads. We must audit legacy code to replace `synchronized` with `ReentrantLock` and ensure database connection pool limits (like HikariCP) are sized appropriately so thousands of concurrent virtual threads don't overwhelm PostgreSQL."
