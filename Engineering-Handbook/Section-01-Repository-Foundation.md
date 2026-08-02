# Engineering Handbook: Section 01 — Repository Foundation & Software Governance

---

## 1. Introduction

Repository Foundation is the cornerstone of professional software engineering. In academic settings or college projects, code repositories are often treated as temporary scratchpads—single-branch folders containing unformatted source files, missing documentation, hardcoded passwords, and zero governance controls. In production software engineering, a repository is an **operational system**, a **legal boundary**, and a **communication medium** for engineers across distributed global teams.

This section covers the architectural design, security safeguards, governance protocols, and documentation frameworks required to transform a raw directory into an enterprise-grade software platform. Mastering repository foundation ensures your software is maintainable, secure, compliant, and understandable by engineers, auditors, and technical interviewers alike.

---

## 2. Learning Objectives

After mastering this section, you will understand:
- **Enterprise Repository Anatomy**: Why real-world software platforms follow strict multi-service folder structures.
- **Repository Metadata**: The architectural purpose of `.gitignore`, `README.md`, `CONTRIBUTING.md`, `CHANGELOG.md`, and `CODE_OF_CONDUCT.md`.
- **Architecture Decision Records (ADRs)**: How top tech companies record, review, and defend complex engineering trade-offs over time.
- **Multi-Tier Interview Defense**: How to present software engineering concepts at three distinct competency levels: Campus Placement (Level 1), Product Company (Level 2), and Senior Engineer (Level 3).
- **Governance Controls**: How branch protection rules, open-source licenses, and conventional commits prevent code rot and security breaches.

---

## 3. Files Created & Detailed Analysis

### 1. `.gitignore`
- **Purpose**: Instructs Git which files, directories, compile outputs, and secret files to ignore.
- **Why It Exists**: Prevents polluting version control with OS metadata (`.DS_Store`), IDE settings (`.idea/`), build binaries (`/target/`, `*.jar`), and sensitive environment files (`.env`).
- **Industry Usage**: Essential in 100% of professional Git repositories.
- **Best Practices**: Group patterns by category (OS, Security, Language, IDE). Never commit secrets.
- **Common Mistakes**: Accidentally committing `.env` files or committing compiled `.class` files.
- **Future Improvements**: Use automated pre-commit hooks (`git-leaks`) to detect secret patterns before commit execution.

### 2. `README.md`
- **Purpose**: Serves as the primary entry point and high-level landing page for the project.
- **Why It Exists**: Provides new team members, open-source contributors, and hiring managers with immediate clarity on what the project does, how to run it, and its architecture.
- **Industry Usage**: Standardized project dashboard across GitHub, GitLab, and Bitbucket.
- **Best Practices**: Use build status badges, visual architecture diagrams, single-command quickstarts (`docker-compose up`), API endpoints table, and product roadmap.
- **Common Mistakes**: Leaving placeholder text, omitting prerequisites, or failing to explain how to test the application.

### 3. `CONTRIBUTING.md`
- **Purpose**: Defines development guidelines, code formatting standards, branch strategies, and Pull Request (PR) rules.
- **Why It Exists**: Ensures code consistency across multiple developers without requiring manual code review corrections.
- **Industry Usage**: Mandatory for open-source projects and internal enterprise repositories ("InnerSource").
- **Best Practices**: Document git branch naming (`feature/TASK-123`), Conventional Commit message rules (`feat(scope): message`), and PR checklists.
- **Common Mistakes**: Writing vague guidelines without explicit syntax rules or formatting expectations.

### 4. `CHANGELOG.md`
- **Purpose**: Manages a chronological log of changes introduced in each software release.
- **Why It Exists**: Communicates breaking changes, new features, bug fixes, and security patches to downstream engineering teams and users.
- **Industry Usage**: Follows the [Keep a Changelog](https://keepachangelog.com/) standard paired with Semantic Versioning (`MAJOR.MINOR.PATCH`).
- **Best Practices**: Group changes under `Added`, `Changed`, `Deprecated`, `Removed`, `Fixed`, and `Security`.
- **Common Mistakes**: Copy-pasting raw git commit logs instead of human-readable release summaries.

### 5. `CODE_OF_CONDUCT.md`
- **Purpose**: Establishes community behavioral standards and enforcement procedures.
- **Why It Exists**: Promotes an inclusive, respectful environment for contributors and clarifies zero-tolerance policies for harassment.
- **Industry Usage**: Standard open-source governance file based on Contributor Covenant 2.1.
- **Best Practices**: Provide clear contact information for conduct enforcement leads.

### 6. `docs/engineering-decisions/ADR-001` through `ADR-006`
- **Purpose**: Immutable Architecture Decision Records capturing context, alternatives, decisions, trade-offs, and interview defenses for tech stack selections.
- **Why They Exist**: Prevents "architecture amnesia" where engineers forget why a past decision was made and re-debate settled architecture choices.
- **Industry Usage**: Adopted by Google, AWS, ThoughtWorks, and Netflix.

---

## 4. Deep Engineering Concepts

### Concept 1: `.gitignore`
- **WHAT**: A plain-text configuration file containing glob patterns matching files Git should exclude from tracking.
- **WHY**: Unchecked binaries bloat repository size, while unchecked environment files leak production credentials.
- **WHEN**: Must be created in the initial commit of any project before adding source code.
- **HOW**: Git checks `.gitignore` rules sequentially using pattern matching before staging files.
- **ADVANTAGES**: Secures secrets, preserves RAM and disk space, speeds up `git status` and `git clone`.
- **LIMITATIONS**: Does not un-track files that were already committed before being added to `.gitignore`.
- **REAL WORLD EXAMPLE**: An engineer accidentally commits an AWS access key inside `.env`. Automated bots scan GitHub public feeds within 10 seconds and compromise the cloud account. A proper `.gitignore` prevents this catastrophic leak.

---

### Concept 2: Architecture Decision Records (ADRs)
- **WHAT**: Short, version-controlled markdown documents documenting a significant software architecture decision.
- **WHY**: Architecture evolves over years. Without ADRs, technical debt accumulates because engineers don't understand past constraints.
- **WHEN**: Created whenever a major tech choice, data model, or framework selection is made.
- **HOW**: Format includes Title, Status (ACCEPTED/PROPOSED), Context, Decision, Consequences, and Alternatives.
- **ADVANTAGES**: Preserves tribal knowledge, speeds up onboarding, provides interview defense material.
- **LIMITATIONS**: Requires developer discipline to write and update during fast-paced sprints.
- **REAL WORLD EXAMPLE**: A new engineer asks: *"Why are we using PostgreSQL with `pgvector` instead of Pinecone?"* Instead of an hour-long meeting, the senior engineer points them to `ADR-002-why-postgresql.md`.

---

## 5. Engineering Decisions Breakdown

| Tech Selection | Why This? | Why Not Alternatives? | Main Advantage | Main Limitation |
| :--- | :--- | :--- | :--- | :--- |
| **Java Spring Boot 3.2** | Enterprise-grade static typing, `@Transactional` boundaries, Java 21 Virtual Threads | Node.js (single-threaded CPU bottleneck); Go (lacks high-level enterprise security & JPA tooling) | Unmatched enterprise ecosystem and multi-threaded throughput | Higher memory footprint (~200MB RSS) |
| **PostgreSQL 16 + pgvector** | Unified relational database + 1536-dim vector similarity search in a single ACID engine | MongoDB (weak relational integrity); Pinecone (dual-write network & sync issues) | Atomic hybrid SQL + vector queries in one DB roundtrip | Requires sharding past 10M+ vectors |
| **Python FastAPI** | Asynchronous ASGI framework for isolated ML / LLM RAG pipelines | Flask (synchronous WSGI); Embedded Python in Java (slow memory leaks) | Native integration with PyMuPDF, LangChain, OpenAI | Dynamic typing requires Pydantic enforcement |
| **Redis 7.2** | Multi-purpose low-latency memory store for refresh tokens, rate limiting, and caching | Memcached (lacks data structures and Lua execution); JVM Local Cache (cannot scale multi-pod) | Sub-millisecond operations across all application pods | Memory-bound; requires strict LRU eviction |
| **Docker & Docker Compose** | Kernel-level containerization ensuring 100% environment parity across systems | Native Host Installation ("works on my machine" bugs); Full VMs (heavy RAM overhead) | Single command infrastructure setup (`docker-compose up`) | Requires container runtime knowledge |
| **Modular Monolith** | Strict package boundaries (`com.intelliflow.modules.*`) in a single deployable artifact | Distributed Microservices from Day 1 (unnecessary saga and network complexity) | High development velocity with clean migration paths | Requires developer package discipline |

---

## 6. Industry Perspective

- **Startups (Seed to Series A)**: Focus on rapid iteration using Modular Monoliths and Docker Compose. Every engineer touches full-stack features. Documentation is kept lean but essential (README + single-command local setup).
- **Product Companies (Series B to Unicorns)**: Enforce Flyway database migrations, strict CONTRIBUTING rules, and RFC 7807 global exception standards. ADRs become mandatory to manage growing engineering teams (30–200 engineers).
- **FAANG / Large Enterprises (Google, Meta, AWS)**: Operates under strict compliance policies. Monorepos or multi-repo microservices utilize automated security scanners (`git-leaks`, SonarQube), standardized C4 architecture diagrams, and formal ADR approval committees.

---

## 7. Common Beginner Mistakes & Professional Avoidance

1. **Mistake**: Using JPA `ddl-auto: update` in production.
   - **Professional Fix**: Set `ddl-auto: validate` and manage 100% of database changes using versioned Flyway scripts (`V1__init_schema.sql`).
2. **Mistake**: Committing secrets or local database connection strings in `.env` or Java source code.
   - **Professional Fix**: Add `.env` to `.gitignore` and use `.env.example` templates with environment variable overrides.
3. **Mistake**: Returning raw unhandled exceptions or stack traces to API clients.
   - **Professional Fix**: Intercept all runtime exceptions using `@RestControllerAdvice` and format responses using the RFC 7807 Problem Details standard.
4. **Mistake**: Writing monolithic unindexed SQL queries.
   - **Professional Fix**: Add B-Tree indexes on foreign keys and HNSW graph indexes on vector embedding columns.

---

## 8. Comprehensive Interview Preparation Question Bank (60 Questions)

### Part A: 30 Beginner Questions

#### Q1: What is the purpose of a `.gitignore` file?
- **Ideal Answer**: A `.gitignore` file specifies intentionally untracked files that Git should ignore. It prevents committing binaries, OS metadata, IDE configurations, and security secrets.
- **Common Wrong Answer**: *"It deletes unwanted files from your computer."*
- **Follow-up Question**: How do you ignore a file that has already been committed in Git history?
- **Interview Tip**: Mention security (`.env` files) first, then build artifacts.

#### Q2: What is the difference between Git and GitHub?
- **Ideal Answer**: Git is a distributed version control software tool running locally. GitHub is a cloud-based hosting service for Git repositories providing collaboration tools (Pull Requests, Actions, Issue Tracking).
- **Common Wrong Answer**: *"They are the same thing."*
- **Follow-up Question**: Name two alternative Git hosting platforms (GitLab, Bitbucket).
- **Interview Tip**: Emphasize that Git works completely offline without GitHub.

#### Q3: What is a Pull Request (PR)?
- **Ideal Answer**: A Pull Request is a proposed code change submitted by a developer to merge code from a feature branch into a target branch (e.g. `main`), allowing team review and CI/CD checks before integration.
- **Common Wrong Answer**: *"It's a command to download code from GitHub."*
- **Follow-up Question**: What is code review?
- **Interview Tip**: Mention automated CI checks alongside human code reviews.

#### Q4: Why do we use `README.md` in Markdown format?
- **Ideal Answer**: Markdown is a lightweight markup language rendered natively by platforms like GitHub. It provides structured formatting (headers, code blocks, tables) to document project installation and architecture cleanly.
- **Common Wrong Answer**: *"Because GitHub doesn't open PDF files."*
- **Follow-up Question**: How do you add images or mermaid diagrams to a Markdown file?
- **Interview Tip**: Keep formatting simple and mention badges and code blocks.

#### Q5: What is Docker?
- **Ideal Answer**: Docker is an open-source containerization platform that packages applications and their dependencies into lightweight, isolated containers that run consistently across any host operating system.
- **Common Wrong Answer**: *"Docker is a lightweight Virtual Machine."*
- **Follow-up Question**: How does Docker differ from a Virtual Machine?
- **Interview Tip**: Clarify that containers share the host OS kernel.

#### Q6: What is Docker Compose?
- **Ideal Answer**: Docker Compose is a tool for defining and running multi-container Docker applications using a single `docker-compose.yml` configuration file.
- **Common Wrong Answer**: *"It is used to install Docker."*
- **Follow-up Question**: Which command starts all containers in detached mode? (`docker-compose up -d`).
- **Interview Tip**: Emphasize single-command environment orchestration.

#### Q7: What is PostgreSQL?
- **Ideal Answer**: PostgreSQL is an enterprise-grade, open-source object-relational database management system (ORDBMS) known for SQL compliance, ACID transactions, and extensibility.
- **Common Wrong Answer**: *"It's a NoSQL document database."*
- **Follow-up Question**: What extension enables vector similarity search in PostgreSQL? (`pgvector`).
- **Interview Tip**: Highlight strict data integrity and extensibility.

#### Q8: What is Redis?
- **Ideal Answer**: Redis is an in-memory data structure store used as a high-speed distributed cache, session store, message broker, and rate limiter.
- **Common Wrong Answer**: *"Redis is a replacement for relational databases."*
- **Follow-up Question**: What is the default port for Redis? (`6379`).
- **Interview Tip**: Mention in-memory execution and data structures (Hashes, Sets, Streams).

#### Q9: What is Spring Boot?
- **Ideal Answer**: Spring Boot is an opinionated framework built on top of the Spring Framework that simplifies creating stand-alone, production-grade Java applications with embedded servers (like Tomcat).
- **Common Wrong Answer**: *"Spring Boot is a programming language."*
- **Follow-up Question**: What annotation starts a Spring Boot application? (`@SpringBootApplication`).
- **Interview Tip**: Emphasize auto-configuration and rapid production readiness.

#### Q10: What is FastAPI?
- **Ideal Answer**: FastAPI is a modern, high-performance Python web framework for building APIs with Python 3.8+ based on standard Python type hints, Starlette (for async), and Pydantic (for data validation).
- **Common Wrong Answer**: *"FastAPI is a frontend JavaScript framework."*
- **Follow-up Question**: How does FastAPI generate interactive API docs automatically? (Via OpenAPI specification).
- **Interview Tip**: Mention async/await execution and automatic OpenAPI documentation.

#### Q11: What is an API (Application Programming Interface)?
- **Ideal Answer**: An API is a defined contract allowing two software applications to communicate over protocols like HTTP/REST, gRPC, or WebSockets using standardized request/response formats (like JSON).
- **Common Wrong Answer**: *"An API is a database."*
- **Follow-up Question**: What HTTP method is used to create a new resource? (`POST`).
- **Interview Tip**: Emphasize contract-based communication.

#### Q12: What does REST stand for?
- **Ideal Answer**: Representational State Transfer, an architectural style for designing networked applications using stateless HTTP methods (`GET`, `POST`, `PUT`, `DELETE`).
- **Common Wrong Answer**: *"Real-Time Embedded Storage Technology."*
- **Follow-up Question**: What HTTP status code represents a resource creation success? (`201 Created`).
- **Interview Tip**: Highlight statelessness and standard HTTP semantics.

#### Q13: What is JSON?
- **Ideal Answer**: JavaScript Object Notation, a lightweight data-interchange format that is easy for humans to read and write and easy for machines to parse and generate.
- **Common Wrong Answer**: *"Java System Object Network."*
- **Follow-up Question**: What data types are supported in JSON? (String, Number, Object, Array, Boolean, Null).
- **Interview Tip**: Mention universal language compatibility.

#### Q14: What is Flyway?
- **Ideal Answer**: Flyway is an open-source database migration tool that applies version-controlled SQL scripts to guarantee schema consistency across development, staging, and production environments.
- **Common Wrong Answer**: *"Flyway is a router for web traffic."*
- **Follow-up Question**: Where does Flyway record executed migrations? (`flyway_schema_history` table).
- **Interview Tip**: Mention zero schema drift.

#### Q15: What is Maven?
- **Ideal Answer**: Apache Maven is a build automation and dependency management tool for Java projects defined by a `pom.xml` (Project Object Model) file.
- **Common Wrong Answer**: *"Maven is a Java compiler."*
- **Follow-up Question**: What command compiles and packages a Spring Boot app skipping tests? (`mvn package -DskipTests`).
- **Interview Tip**: Differentiate dependency resolution from compilation.

#### Q16: What is a DTO (Data Transfer Object)?
- **Ideal Answer**: A design pattern object used to carry data between software layers (e.g., API controller to service) without exposing domain entity models or internal database structures.
- **Common Wrong Answer**: *"A table in the database."*
- **Follow-up Question**: Why should domain entities not be returned directly from REST controllers?
- **Interview Tip**: Security (prevents over-posting/data leakage) and decoupling.

#### Q17: What is Lombok?
- **Ideal Answer**: A Java library that automatically plugs into your editor and build tools to reduce boilerplate code (like getters, setters, constructors, builders) via annotations.
- **Common Wrong Answer**: *"A database ORM tool."*
- **Follow-up Question**: Name three common Lombok annotations (`@Getter`, `@Setter`, `@Builder`, `@RequiredArgsConstructor`).
- **Interview Tip**: Mention cleaner source code readability.

#### Q18: What is Swagger / OpenAPI?
- **Ideal Answer**: OpenAPI is a specification for describing RESTful APIs. Swagger UI is a tool that renders OpenAPI specs into interactive browser documentation.
- **Common Wrong Answer**: *"A testing framework for database speed."*
- **Follow-up Question**: What URL usually hosts Swagger in Spring Boot? (`/swagger-ui.html`).
- **Interview Tip**: Highlight interactive client testing and automated documentation.

#### Q19: What is an Environment Variable?
- **Ideal Answer**: A dynamic-named value stored on the operating system that can affect the way running processes behave (e.g., DB credentials, API keys).
- **Common Wrong Answer**: *"A variable declared inside a Java class."*
- **Follow-up Question**: Why are environment variables preferred over hardcoded strings for DB passwords?
- **Interview Tip**: Mention 12-Factor App principles and security isolation.

#### Q20: What is `.env.example`?
- **Ideal Answer**: A template file committed to version control containing non-sensitive variable keys and default values, showing developers which environment variables are required to run the app.
- **Common Wrong Answer**: *"A backup copy of your real production secrets."*
- **Follow-up Question**: Should `.env.example` contain real production passwords? (Never).
- **Interview Tip**: Emphasize team developer experience.

#### Q21: What is UTF-8 Encoding?
- **Ideal Answer**: A variable-width character encoding capable of encoding all 1,112,064 valid character code points in Unicode, guaranteeing cross-platform text compatibility.
- **Common Wrong Answer**: *"A password hashing algorithm."*
- **Follow-up Question**: Why should all source code and HTTP responses default to UTF-8?
- **Interview Tip**: Internationalization and emoji support.

#### Q22: What is UTC Timezone?
- **Ideal Answer**: Coordinated Universal Time, the primary time standard by which the world regulates clocks and time.
- **Common Wrong Answer**: *"Universal Time in California."*
- **Follow-up Question**: Why do servers store timestamps in UTC instead of local time?
- **Interview Tip**: Prevents timezone skew and daylight saving time bugs.

#### Q23: What is HTTP Status Code 404?
- **Ideal Answer**: `404 Not Found`, indicating the server cannot find the requested resource endpoint or database record.
- **Common Wrong Answer**: *"Internal Server Crash."*
- **Follow-up Question**: What status code represents a permission denial? (`403 Forbidden`).
- **Interview Tip**: Know your standard 2xx, 4xx, and 5xx status codes.

#### Q24: What is HTTP Status Code 500?
- **Ideal Answer**: `500 Internal Server Error`, indicating an unhandled exception occurred on the server while processing the request.
- **Common Wrong Answer**: *"Invalid Client URL."*
- **Follow-up Question**: Should a 500 error return raw Java stack traces to the client? (No, security risk).
- **Interview Tip**: Emphasize RFC 7807 sanitization.

#### Q25: What is a Health Check endpoint?
- **Ideal Answer**: An HTTP endpoint (e.g. `/actuator/health`) that monitoring systems or load balancers ping to verify whether an application pod is operational.
- **Common Wrong Answer**: *"A database backup endpoint."*
- **Follow-up Question**: Name two Spring Boot Actuator health states (`UP`, `DOWN`).
- **Interview Tip**: Kubernetes liveness/readiness probes.

#### Q26: What is a Container Image?
- **Ideal Answer**: An immutable, executable file package containing code, runtime, libraries, environment variables, and config files needed to run an application container.
- **Common Wrong Answer**: *"A screenshot of your code."*
- **Follow-up Question**: What command builds a Docker image? (`docker build -t name .`).
- **Interview Tip**: Immutable blueprint vs running container instance.

#### Q27: What is an HNSW Index in vector search?
- **Ideal Answer**: Hierarchical Navigable Small World, a multi-layer graph indexing algorithm that allows fast approximate nearest neighbor (ANN) similarity search across vector embeddings.
- **Common Wrong Answer**: *"A relational primary key index."*
- **Follow-up Question**: What distance operator is used for cosine similarity in pgvector? (`<=>`).
- **Interview Tip**: Compare graph traversal vs brute-force distance calculation.

#### Q28: What is unit testing?
- **Ideal Answer**: A software testing method where individual units or components of code (like isolated classes or methods) are tested independently using mocks for dependencies.
- **Common Wrong Answer**: *"Testing the entire website by clicking buttons."*
- **Follow-up Question**: Name the standard Java testing library (JUnit 5).
- **Interview Tip**: Test isolation and fast execution speed.

#### Q29: What is Mockito?
- **Ideal Answer**: A popular Java mocking framework used in unit tests to create dummy implementations of dependent classes (like repositories or external APIs) and verify behavior.
- **Common Wrong Answer**: *"A database migration tool."*
- **Follow-up Question**: What annotation mocks a bean in Spring Boot tests? (`@MockBean` or `@Mock`).
- **Interview Tip**: Behavioral verification (`when(...).thenReturn(...)`).

#### Q30: What is Pytest?
- **Ideal Answer**: A mature, full-featured Python testing framework that makes it easy to write small, readable tests for Python applications and FastAPI endpoints.
- **Common Wrong Answer**: *"A compiler for Python code."*
- **Follow-up Question**: How does Pytest handle test setup and teardown? (Using fixtures).
- **Interview Tip**: Fixtures and concise assertion syntax.

---

### Part B: 20 Intermediate Questions

#### Q31: What is a Modular Monolith architecture, and how does it differ from a Microservice architecture?
- **Ideal Answer**: A Modular Monolith packages all feature domains inside a single deployable application unit while maintaining strict, isolated package boundaries (`com.intelliflow.modules.auth`, `com.intelliflow.modules.task`). A Microservices architecture breaks these domains into separate physical network services with independent databases. Modular Monoliths eliminate network latency and distributed saga overhead while preserving clean migration paths to microservices if needed.
- **Common Wrong Answer**: *"A Modular Monolith is just bad code written in a single file."*
- **Follow-up Question**: How do you prevent cross-module direct entity coupling in Spring Boot?
- **Interview Tip**: Frame this around balancing developer velocity with domain boundaries.

#### Q32: Explain RFC 7807 (Problem Details for HTTP APIs). Why is it better than custom error formats?
- **Ideal Answer**: RFC 7807 is an IETF specification defining a standard JSON payload format for HTTP API errors (`title`, `status`, `detail`, `type`, `instance`, `code`). It provides machine-readable error standards across enterprise microservices, allowing frontend applications, API gateways, and external integrators to handle errors uniformly without guessing error field names.
- **Common Wrong Answer**: *"It is just a fancy string message format."*
- **Follow-up Question**: How does Spring Boot 3 natively support RFC 7807? (Via `ProblemDetail` class).
- **Interview Tip**: Emphasize API contract standardization across teams.

#### Q33: How does Flyway track database migrations, and what happens if an executed migration script is edited afterwards?
- **Ideal Answer**: Flyway creates a `flyway_schema_history` table in PostgreSQL. When a script (e.g. `V1__init.sql`) executes, Flyway records its script name, execution timestamp, and a SHA-256 checksum. If a developer edits a previously executed migration script, Flyway detects a checksum mismatch on application startup, throws a `FlywayException`, and halts deployment to prevent database corruption.
- **Common Wrong Answer**: *"Flyway automatically updates the database when old scripts change."*
- **Follow-up Question**: How do you safely modify an existing production table? (Create a new version script `V2__modify.sql`).
- **Interview Tip**: Highlight immutability and checksum verification.

#### Q34: What is the 12-Factor App methodology, and how does Section 1 adhere to it?
- **Ideal Answer**: The 12-Factor App is a methodology for building SaaS applications. Section 1 adheres to:
  1. **Codebase**: Single repository tracked in Git.
  2. **Dependencies**: Explicitly declared via `pom.xml` and `requirements.txt`.
  3. **Config**: Environment variables isolated in `.env` and injected into `application.yml`.
  4. **Backing Services**: PostgreSQL and Redis treated as attached network resources via Docker Compose.
- **Common Wrong Answer**: *"It's 12 rules about writing clean Java code."*
- **Follow-up Question**: Name 3 other 12-Factor principles (Dev/Prod Parity, Logs, Stateless Processes).
- **Interview Tip**: Memorize at least 4-5 principles and link them directly to project files.

#### Q35: Explain the difference between `ddl-auto: create`, `ddl-auto: update`, and `ddl-auto: validate` in Hibernate.
- **Ideal Answer**:
  - `create`: Drops existing tables and recreates schema on application startup (causes total data loss).
  - `update`: Inspects entities and mutates database tables automatically (non-deterministic, unsafe for production).
  - `validate`: Checks if entity mappings match existing database tables; throws an exception if discrepancies exist without altering schema.
  Professional applications use `validate` alongside Flyway.
- **Common Wrong Answer**: *"Use `update` in production so you don't have to write SQL."*
- **Follow-up Question**: Why is `ddl-auto: update` dangerous in multi-instance pod deployments?
- **Interview Tip**: State clearly that `validate` is the only production-safe option.

#### Q36: How does Java 21 Virtual Threads (Project Loom) improve HTTP request throughput in Spring Boot 3.2?
- **Ideal Answer**: Traditional Java platform threads map 1:1 to OS kernel threads, consuming ~1MB stack memory per thread. Under heavy concurrent blocking I/O (e.g. DB queries), platform threads stall OS threads. Java 21 Virtual Threads are managed by the JVM. When a virtual thread blocks on DB I/O, the JVM unmounts it from the carrier thread, freeing the carrier thread to execute other virtual threads. This handles tens of thousands of concurrent requests using simple synchronous code.
- **Common Wrong Answer**: *"Virtual threads make CPU calculations 10 times faster."*
- **Follow-up Question**: What is thread pinning in Virtual Threads?
- **Interview Tip**: Emphasize I/O-bound concurrency scaling vs CPU bound execution.

#### Q37: What is the purpose of multi-stage Docker builds, and how do they enhance security?
- **Ideal Answer**: Multi-stage Docker builds use multiple `FROM` instructions in a single `Dockerfile`. The builder stage utilizes a full SDK image (e.g. `maven:3.9-jdk-21`) to compile code. The final stage copies only compiled binaries (`.jar`) into a minimal runtime image (`eclipse-temurin:21-jre-alpine`). This reduces container image size from >800MB to ~200MB, eliminates compiler security vulnerabilities (CVEs), and leaves source code out of the production container.
- **Common Wrong Answer**: *"Multi-stage builds allow you to run two websites in one container."*
- **Follow-up Question**: Why should production Docker containers run with `USER nonroot`?
- **Interview Tip**: Contrast build-time tools vs minimal runtime footprint.

#### Q38: How does ASGI (Asynchronous Server Gateway Interface) in FastAPI differ from WSGI (Web Server Gateway Interface) in Flask?
- **Ideal Answer**: WSGI is a synchronous standard; each request blocks a server worker until completion, making concurrent streaming or async I/O difficult. ASGI (built on Uvicorn/Starlette) supports Python `async/await` coroutines, handling thousands of concurrent non-blocking HTTP requests, WebSockets, and long-polling AI LLM stream calls on a single event loop.
- **Common Wrong Answer**: *"WSGI is for Python 2 and ASGI is for Python 3."*
- **Follow-up Question**: What event loop library powers ASGI under the hood? (uvloop / asyncio).
- **Interview Tip**: Explain event-loop async execution vs thread blocking.

#### Q39: What is `pgvector`, and how does it execute cosine distance vector similarity search?
- **Ideal Answer**: `pgvector` is a PostgreSQL extension enabling storage and vector math for dense embeddings (`vector(1536)`). It adds vector operators such as `<=>` (cosine distance), `<->` (Euclidean distance), and `<#>` (inner product). Combined with HNSW indexing, PostgreSQL converts text embeddings into multi-layer graph structures for sub-10ms approximate nearest neighbor (ANN) retrieval.
- **Common Wrong Answer**: *"It turns PostgreSQL into a NoSQL database."*
- **Follow-up Question**: Why is Cosine distance preferred over Euclidean distance for text embeddings?
- **Interview Tip**: Explain the relationship between vector dimension and distance operators.

#### Q40: What is an Architecture Decision Record (ADR), and why is it important in large engineering teams?
- **Ideal Answer**: An ADR is a short document capturing a significant technical choice, its rationale, context, trade-offs, and alternatives considered. In large or distributed engineering teams, ADRs document tribal knowledge, prevent repeated debates on past decisions, speed up developer onboarding, and provide legal/compliance traceability for architectural choices.
- **Common Wrong Answer**: *"It's a meeting transcript document."*
- **Follow-up Question**: Where should ADRs be stored? (Inside version control `docs/engineering-decisions/`).
- **Interview Tip**: Focus on team communication and long-term technical clarity.

#### Q41: Explain the Cache-Aside pattern using Redis and Spring Boot.
- **Ideal Answer**: In the Cache-Aside pattern:
  1. The application receives a request for data (e.g. `getTask(id)`).
  2. It first queries Redis (`GET cache:task:123`).
  3. If a **Cache Hit** occurs, data is returned immediately.
  4. If a **Cache Miss** occurs, the app queries PostgreSQL, writes the result to Redis with a TTL (`SETEX cache:task:123 3600`), and returns data to the client.
- **Common Wrong Answer**: *"The database automatically pushes updates to Redis."*
- **Follow-up Question**: How do you prevent Cache Stampede?
- **Interview Tip**: Detail read flow vs write invalidation flow.

#### Q42: What is open-source governance, and why do repositories include `LICENSE` and `CODE_OF_CONDUCT.md`?
- **Ideal Answer**: Open-source governance defines legal rights and behavioral rules for a repository. The `LICENSE` file (e.g. MIT, Apache 2.0) defines legal terms for software copying, modification, and commercial distribution. `CODE_OF_CONDUCT.md` establishes community interaction standards, preventing harassment and defining enforcement steps.
- **Common Wrong Answer**: *"Without a license, code is automatically free for anyone to steal without limits."*
- **Follow-up Question**: What is the main difference between MIT and GPL licenses? (Permissive vs Copyleft).
- **Interview Tip**: Distinguish legal intellectual property permissions from community standards.

#### Q43: What is Pydantic v2 in FastAPI, and how does it differ from standard Python dictionaries?
- **Ideal Answer**: Pydantic v2 is a data validation and parsing library built using Rust core bindings (`pydantic-core`). Unlike dynamic Python dictionaries, Pydantic models enforce static type hints, validate incoming JSON types at runtime, sanitize inputs, and generate OpenAPI JSON Schema definitions automatically.
- **Common Wrong Answer**: *"Pydantic is a Python database ORM like SQLAlchemy."*
- **Follow-up Question**: How does Pydantic improve FastAPI execution speed? (Rust backend parsing).
- **Interview Tip**: Emphasize runtime data validation and schema generation.

#### Q44: What is the purpose of Spring Boot Actuator, and how do custom `HealthIndicator` beans work?
- **Ideal Answer**: Spring Boot Actuator provides production-ready operational endpoints (health, metrics, environment, thread dumps). A custom `HealthIndicator` bean implements the `health()` method, performing custom checks (like JVM free memory or downstream service ping) and returning `Health.up()` or `Health.down()` details consumed by Kubernetes probes.
- **Common Wrong Answer**: *"Actuator is a framework for writing HTML UI pages."*
- **Follow-up Question**: Which property controls endpoint exposure in `application.yml`? (`management.endpoints.web.exposure.include`).
- **Interview Tip**: Mention Kubernetes liveness and readiness probe integration.

#### Q45: What is CORS (Cross-Origin Resource Sharing), and how is it configured in Spring Boot and FastAPI?
- **Ideal Answer**: CORS is a browser security mechanism that blocks web applications running on one domain (e.g. `http://localhost:3000`) from making HTTP requests to a different domain (e.g. `http://localhost:8080`) unless the server includes specific `Access-Control-Allow-Origin` headers. It is configured in Spring Boot via `WebMvcConfigurer` and in FastAPI via `CORSMiddleware`.
- **Common Wrong Answer**: *"CORS is a virus scanner for API backends."*
- **Follow-up Question**: Does CORS block server-to-server HTTP API calls? (No, browser security only).
- **Interview Tip**: Clarify browser pre-flight `OPTIONS` requests vs backend enforcement.

#### Q46: What is Lombok's `@Builder` annotation, and what design pattern does it implement?
- **Ideal Answer**: `@Builder` implements the **Builder Design Pattern**. It generates a fluent builder API for constructing complex immutable objects (`ApiResponse.builder().status(...).data(...).build()`) without creating telescoping constructors or mutating setter calls.
- **Common Wrong Answer**: *"It creates database tables automatically."*
- **Follow-up Question**: Why are immutable objects preferred in multi-threaded application logic?
- **Interview Tip**: Compare telescoping constructors vs fluent builder syntax.

#### Q47: Why do we enforce UTC timezone at server startup using `TimeZone.setDefault(TimeZone.getTimeZone("UTC"))`?
- **Ideal Answer**: Application servers deployed across global cloud regions (e.g., AWS US-East-1 vs Tokyo) will default to their host OS local time if unconfigured. Hardcoding UTC on startup guarantees that all timestamp creations, JWT expiration checks, and database `TIMESTAMPTZ` writes remain consistent, preventing daylight saving time bugs and time skew across microservices.
- **Common Wrong Answer**: *"Because databases cannot store non-UTC time."*
- **Follow-up Question**: What PostgreSQL column type should be used for timestamps? (`TIMESTAMPTZ`).
- **Interview Tip**: Frame this as preventing distributed system time skew.

#### Q48: What is Semantic Versioning (SemVer), and how does `CHANGELOG.md` support it?
- **Ideal Answer**: SemVer uses a `MAJOR.MINOR.PATCH` versioning format (e.g., `1.2.3`).
  - `MAJOR`: Incremented on incompatible breaking API changes.
  - `MINOR`: Incremented on backward-compatible new functionality.
  - `PATCH`: Incremented on backward-compatible bug fixes.
  `CHANGELOG.md` maps version tags to human-readable feature summaries.
- **Common Wrong Answer**: *"SemVer is just random numbers assigned to code releases."*
- **Follow-up Question**: What version number format indicates initial development? (`0.y.z`).
- **Interview Tip**: Explain client impact of breaking vs non-breaking releases.

#### Q49: What is the C4 Model for software architecture visualization?
- **Ideal Answer**: The C4 Model (Context, Containers, Components, Code) is a hierarchical framework for visual software architecture documentation.
  - **Context**: High-level view showing users and system boundaries.
  - **Containers**: Shows high-level runnable applications/databases (Spring Boot, FastAPI, Postgres).
  - **Components**: Internal modular breakdown.
  - **Code**: Class/entity diagrams.
- **Common Wrong Answer**: *"C4 is a programming language written in C."*
- **Follow-up Question**: Which diagram level did we create in `context-diagram.md`? (System Context).
- **Interview Tip**: Mention visual abstraction levels for technical vs non-technical stakeholders.

#### Q50: What are Conventional Commits, and why are they recommended in enterprise Git repositories?
- **Ideal Answer**: Conventional Commits is a specification for commit messages formatted as `type(scope): description` (e.g. `feat(auth): add refresh token rotation`). Standardized commit types (`feat`, `fix`, `docs`, `refactor`, `test`) allow automated changelog creation, automated semver version tagging, and clean repository history navigation.
- **Common Wrong Answer**: *"It requires writing 100 words in every commit message."*
- **Follow-up Question**: How can conventional commits be enforced automatically in a team? (Git commit-msg hooks via Husky).
- **Interview Tip**: Connect commit standards to automated CI/CD pipelines.

---

### Part C: 10 Advanced Questions

#### Q51: How do you design a database migration pipeline in PostgreSQL that supports Zero-Downtime Deployments (Blue-Green) when renaming a column?
- **Ideal Answer**: Direct column renames (`ALTER TABLE users RENAME COLUMN old_name TO new_name`) cause immediate downtime because old application instances fail during the deployment window. We follow the **Expand-Contract Migration Pattern**:
  1. **Phase 1 (Expand)**: Add the new column (`V2__add_new_column.sql`) as nullable.
  2. **Phase 2 (Dual Write)**: Deploy application version containing code that writes to *both* old and new columns while reading from the old column.
  3. **Phase 3 (Data Backfill)**: Run an async database backfill script copying historical data.
  4. **Phase 4 (Switch Read)**: Deploy application version that reads exclusively from the new column.
  5. **Phase 5 (Contract)**: Drop old column in a final migration (`V3__drop_old_column.sql`).
- **Common Wrong Answer**: *"Just rename the column during off-peak hours."*
- **Follow-up Question**: How do database locks affect `ALTER TABLE` in PostgreSQL under high traffic?
- **Interview Tip**: Emphasize backward compatibility and multi-stage release cycles.

#### Q52: Explain carrier thread pinning in Java 21 Virtual Threads. How do you detect and fix it in Spring Boot 3.2?
- **Ideal Answer**: Carrier thread pinning occurs when a virtual thread enters a `synchronized` block/method or invokes native JNI code before performing a blocking I/O operation. Instead of unmounting, the virtual thread remains pinned to its underlying OS carrier thread, stalling other virtual threads. We detect pinning by passing `-Djdk.traceVirtualThreadLocals=true` or `-Djdk.tracePinnedThreads=full` JVM flags. To fix pinning, we refactor legacy `synchronized` blocks to use `java.util.concurrent.locks.ReentrantLock`.
- **Common Wrong Answer**: *"Virtual threads pin CPU usage to 100% automatically."*
- **Follow-up Question**: Does `ReentrantLock` unmount virtual threads safely? (Yes).
- **Interview Tip**: Show deep knowledge of JVM internals and concurrency pitfalls.

#### Q53: How does the HNSW graph algorithm inside `pgvector` optimize high-dimensional vector search? What are its memory & recall trade-offs?
- **Ideal Answer**: HNSW (Hierarchical Navigable Small World) creates a multi-layer skip-list graph. Top layers contain long-range connections between distant vectors; bottom layers contain dense short-range connections. Search begins at sparse top layers for rapid routing, descending layers to fine-tune nearest-neighbor discovery (`vector_cosine_ops`).
  - **Trade-offs**: HNSW requires significantly more RAM memory to store graph node pointers compared to `IVFFlat`. However, it provides sub-10ms search latency without requiring global index retraining when new vectors are inserted.
- **Common Wrong Answer**: *"HNSW checks every single vector in the database one by one."*
- **Follow-up Question**: What parameter controls search accuracy vs speed at query time? (`hnsw.ef_search`).
- **Interview Tip**: Frame memory consumption vs query latency and index updates.

#### Q54: How would you prevent JWT Token Replay Attacks and enforce Instant Revocation in a distributed system using Redis?
- **Ideal Answer**: Since JWT access tokens are stateless, instant revocation requires a distributed memory store.
  1. We embed a unique `jti` (JWT ID) claim in every access token upon issuance.
  2. When a user logs out or experiences a security event, we write the `jti` to Redis with a TTL matching the token's remaining lifespan (`SETEX blacklist:jti:<id> <remaining_seconds> "revoked"`).
  3. API Gateway or Spring Security filter checks Redis (`EXISTS blacklist:jti:<id>`). If blacklisted, HTTP 401 Unauthorized is returned immediately.
  4. Refresh tokens use **Refresh Token Rotation**: using a refresh token revokes it and issues a new pair. Reuse of an old refresh token flags session theft, invalidating the entire token family hierarchy in Redis.
- **Common Wrong Answer**: *"JWT tokens cannot be revoked until they expire naturally."*
- **Follow-up Question**: How does Redis key eviction policy (`volatile-ttl`) protect the token blacklist?
- **Interview Tip**: Explain access token blacklisting alongside refresh token family rotation.

#### Q55: How do you design an atomic sliding-window API Rate Limiter in Redis using Lua scripts? Why are Lua scripts necessary?
- **Ideal Answer**: Rate limiting requires checking and incrementing request counters per user IP atomically. Executing multiple individual Redis commands (`MULTI/EXEC` or separate calls) introduces race conditions under high concurrent request bursts.
  We write a Redis Lua script (`EVAL`) that executes atomically on the single-threaded Redis engine:
  1. The script takes `key`, `current_timestamp`, `window_size`, and `limit`.
  2. It removes timestamps older than `current_timestamp - window_size` using `ZREMRANGEBYSCORE`.
  3. It counts remaining elements using `ZCARD`.
  4. If count < limit, it adds current timestamp using `ZADD` and returns allowed (1). Else returns blocked (0).
  Because Lua scripts execute atomically, no race conditions can occur across application pod replicas.
- **Common Wrong Answer**: *"Use a standard Java `for` loop to check Redis keys."*
- **Follow-up Question**: What is the memory complexity of storing timestamps in a Sorted Set (`ZSET`)?
- **Interview Tip**: Highlight single-threaded atomic execution in Redis via Lua.

#### Q56: How do multi-stage Docker builds interact with layer caching, and how do you optimize a `Dockerfile` for minimal build times?
- **Ideal Answer**: Docker caches build layers based on file hash changes. Instructions placed higher in a `Dockerfile` invalidate all subsequent layer caches if their inputs change.
  To optimize:
  1. Copy dependency manifest files (`pom.xml` / `requirements.txt`) *first*.
  2. Run dependency download commands (`mvn dependency:go-offline` / `pip install`).
  3. Copy application source code (`COPY src ./src`) *after* dependency resolution.
  Since source code changes frequently while dependencies change rarely, Docker reuses cached dependency layers, reducing build times from minutes to seconds.
- **Common Wrong Answer**: *"Put `COPY . .` on line 1 of the Dockerfile."*
- **Follow-up Question**: How do `.dockerignore` files prevent cache invalidation?
- **Interview Tip**: Structure Dockerfile commands from least-frequently changed to most-frequently changed.

#### Q57: How would you scale this dual-service architecture (Spring Boot + FastAPI) to handle 100,000 requests/minute while maintaining sub-100ms response SLAs?
- **Ideal Answer**:
  1. **Stateless Tier**: Scale Spring Boot core pods horizontally on Kubernetes using HPA based on CPU/RAM metrics.
  2. **AI Microservice**: Autoscale FastAPI pods using KEDA based on Redis Stream queue depth (scaling compute up during transcript processing bursts).
  3. **Database Tier**: Separate read/write DB operations using HikariCP routing (`@Transactional(readOnly = true)`) to PostgreSQL Read Replicas. Partition heavy audit/notification tables by range (`created_at`).
  4. **Caching Layer**: Enforce Cache-Aside pattern in Redis for hotspot entity lookups and rate limiting.
  5. **Edge Tier**: Route incoming traffic through Cloudflare CDN + AWS Application Load Balancer with TLS 1.3 termination.
- **Common Wrong Answer**: *"Buy a bigger database server with 128 cores."*
- **Follow-up Question**: At what scale should we migrate from Redis Streams to Apache Kafka?
- **Interview Tip**: Break response down tier-by-tier (Edge, App, AI, DB, Cache).

#### Q58: Explain the difference between Role-Based Access Control (RBAC) and Attribute-Based Access Control (ABAC). How are both implemented in Spring Security?
- **Ideal Answer**:
  - **RBAC**: Grants permissions based on user roles (`ROLE_ADMIN`, `ROLE_MANAGER`). In Spring Security: `@PreAuthorize("hasRole('MANAGER')")`.
  - **ABAC**: Grants access based on attributes of the user, resource, and environment (e.g. *"Can this user edit this task?"* -> Check if `user.id == task.assigneeId` AND `user.departmentId == task.departmentId`).
  In Spring Security, ABAC is implemented using custom Spring Bean expressions inside `@PreAuthorize`:
  `@PreAuthorize("@securityService.canAccessTask(authentication, #taskId)")`.
- **Common Wrong Answer**: *"RBAC is for frontend and ABAC is for backend."*
- **Follow-up Question**: How do you prevent BOLA (Broken Object Level Authorization) using ABAC?
- **Interview Tip**: Provide concrete `@PreAuthorize` SpEL code examples.

#### Q59: How do you design an audit logging system for compliance (SOC2/GDPR) without polluting business domain services?
- **Ideal Answer**: We use **Aspect-Oriented Programming (AOP)** via Spring `@Aspect`.
  1. Define a custom annotation `@AuditLog(action = "TASK_DELETED")`.
  2. Create an aspect (`AuditAspect`) that intercepts methods annotated with `@AuditLog`.
  3. Using `Around` or `AfterReturning` advice, extract user claims from `SecurityContextHolder`, client IP from `HttpServletRequest`, target resource ID, and method parameters.
  4. Publish an asynchronous `AuditLogEvent` to an in-memory event bus or Redis Stream to persist the log in a range-partitioned `audit_logs` database table.
  This completely decouples audit logging from domain business logic.
- **Common Wrong Answer**: *"Write `repository.save(new AuditLog())` inside every controller method."*
- **Follow-up Question**: Why should audit log database tables be append-only?
- **Interview Tip**: Emphasize Aspect-Oriented Programming (AOP) and async event publishing.

#### Q60: Describe a strategy for transitioning from a Modular Monolith to independent Microservices as a company grows from 50 to 500 engineers.
- **Ideal Answer**: We follow the **Strangler Fig Pattern**:
  1. Because our Spring Boot application enforces strict module package boundaries (`com.intelliflow.modules.<domain>`) with no direct cross-module table joins, domain modules are already decoupled.
  2. Select a high-demand module (e.g. `notification` or `document`).
  3. Create a new repository and microservice deployment pipeline for that module.
  4. Refactor direct Java method calls to asynchronous event publishing over Kafka/Redis Streams or HTTP/gRPC.
  5. Migrate the target domain's tables to a dedicated database schema.
  6. Route API Gateway traffic to the new microservice, gradually choking off old monolith paths until the target module is completely extracted.
- **Common Wrong Answer**: *"Rewrite the entire application from scratch in Go over one weekend."*
- **Follow-up Question**: How do you manage distributed transactions across extracted microservices? (Saga Pattern / Outbox Pattern).
- **Interview Tip**: Name-drop Strangler Fig Pattern and Outbox Pattern.

---

## 9. Revision Notes

1. **Repository Foundation**: Software repositories are legal, operational, and communication systems. They require structure, governance metadata, and documentation.
2. **Metadata Files**: `.gitignore` secures secrets; `README.md` provides installation clarity; `CONTRIBUTING.md` defines git rules; `CHANGELOG.md` tracks releases via SemVer.
3. **ADRs**: Document architectural decisions, context, alternatives, and trade-offs to prevent technical debt and re-debating settled designs.
4. **Dual-Service Baseline**: Java 21 Spring Boot handles core transactional business logic; Python 3.11 FastAPI runs async AI ML pipelines.
5. **PostgreSQL + pgvector**: Unified relational store + HNSW graph indexing for 1536-dimensional vector search; enables hybrid SQL + vector queries without dual-write bugs.
6. **Redis 7.2**: In-memory operational store handling token blacklisting, atomic Lua sliding-window rate limiting, and Cache-Aside data caching.
7. **Flyway**: Version-controlled SQL database migration engine ensuring deterministic schema transitions without ORM auto-generation risks.
8. **Java 21 Virtual Threads**: Lightweight JVM-managed threads providing high-concurrency throughput for blocking I/O without async code complexity.
9. **RFC 7807**: Industry standard JSON problem details error format (`status`, `title`, `detail`, `code`, `instance`, `timestamp`).
10. **Multi-Stage Docker**: Separates heavy build SDK stages from lightweight JRE runtime images, reducing container size and attack surfaces.

---

## 10. One Page Cheat Sheet

```
+---------------------------------------------------------------------------------------------------+
|                               INTELLIFLOW AI PLATFORM CHEAT SHEET                                 |
+---------------------------------------------------------------------------------------------------+
| TECH STACK          | Core: Java 21 Spring Boot 3.2 | AI: Python 3.11 FastAPI                      |
| PERSISTENCE         | PostgreSQL 16 + pgvector (HNSW Indexing) | Migrations: Flyway (V1__init.sql)     |
| CACHE / SECURITY    | Redis 7.2 (Lua Rate Limiting, Token Blacklist) | JWT Dual-Token (RS256)          |
+---------------------------------------------------------------------------------------------------+
| CORE COMMANDS       | docker-compose up -d                   | Spin up Postgres, Redis, App Services|
|                     | mvn clean package -DskipTests          | Build Spring Boot Jar Artifact        |
|                     | pytest                                 | Run FastAPI Python Test Suite         |
+---------------------------------------------------------------------------------------------------+
| GOVERNANCE FILES    | .gitignore                             | Exclude secrets (.env), binaries, IDE |
|                     | README.md                              | Quickstart, badges, architecture diagrams|
|                     | CONTRIBUTING.md                        | Branch naming, conventional commits   |
|                     | CHANGELOG.md                           | Keep a Changelog + SemVer (MAJOR.MINOR)|
|                     | docs/engineering-decisions/            | Architecture Decision Records (ADRs)  |
+---------------------------------------------------------------------------------------------------+
| ERROR FRAMEWORK     | RFC 7807 ProblemDetails                | @RestControllerAdvice + ProblemDetail |
| SWAGGER / OPENAPI   | /swagger-ui.html                       | Interactive OpenAPI 3.0 + Bearer JWT  |
+---------------------------------------------------------------------------------------------------+
```

---

## 11. Personal Notes & Observations

*(Use this section to record your personal learnings, interview experiences, and custom observations as you build the IntelliFlow AI Platform)*

- **Observation 1**: 
- **Observation 2**: 
- **Observation 3**: 

---

## 12. Key Takeaways to Remember Forever

1. **Never commit `.env` files or hardcoded passwords** to Git; always isolate secrets in environment variables.
2. **Never use `ddl-auto: update` in production**; manage 100% of database changes using Flyway migrations.
3. **Always document major technical decisions using ADRs** to preserve architectural context.
4. **Use `pgvector` for initial/mid-scale vector embeddings** to avoid dual-write transactional bugs across separate vector databases.
5. **Java 21 Virtual Threads eliminate thread-pool bottlenecks** for I/O-heavy web applications.
6. **Stateless API design requires dual-token JWTs** (short-lived Access Tokens + Redis Refresh Token rotation).
7. **Always return standardized RFC 7807 Problem Details** for HTTP API errors.
8. **Multi-stage Docker builds dramatically reduce image sizes** and eliminate build-time security vulnerabilities.
9. **Redis Lua scripts guarantee atomic execution** for rate limiters across multi-pod clusters.
10. **Modular Monoliths provide high development speed** with clean migration paths to microservices via strict package boundaries.
11. **Always default application servers and databases to UTC timezone** to prevent time skew.
12. **Use Aspect-Oriented Programming (AOP)** to decouple cross-cutting concerns (audit logging, security) from core business logic.
13. **Conventional Commits (`feat:`, `fix:`) enable automated release versioning** and automated changelogs.
14. **Use `pgvector` HNSW graph indexing** for fast approximate nearest neighbor similarity search.
15. **A well-structured repository is a communication tool** that proves your engineering maturity to teammates and technical interviewers.
