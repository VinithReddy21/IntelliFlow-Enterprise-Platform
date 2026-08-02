# Section 01: Repository Foundation & Software Governance

---

## 1. Prerequisites

Before reading this section, you should have a basic understanding of:
- Basic terminal / command-line commands (`cd`, `ls`, `mkdir`).
- Basic understanding of version control concepts (what Git is).
- High-level awareness of web applications (client-server concept).

---

## 2. Learning Objectives

After completing Section 01, you will master:
- **Repository Architecture**: How production enterprise repositories are structured for polyglot systems.
- **Repository Metadata Files**: The explicit engineering purpose of `.gitignore`, `README.md`, `CONTRIBUTING.md`, `CHANGELOG.md`, and `CODE_OF_CONDUCT.md`.
- **Architecture Decision Records (ADRs)**: How to write and defend immutable records for tech stack choices.
- **C4 Architecture Diagrams**: Reading and writing System Context, Entity-Relationship, Deployment, and Sequence diagrams.
- **Live Demo Scripting**: How to present a dual-service repository during technical interviews.

---

## 3. Implementation Checklist

Verify that you understand and can explain each implemented item:
- [x] **Root Metadata**: `.gitignore`, `LICENSE`, `CONTRIBUTING.md`, `CHANGELOG.md`, `CODE_OF_CONDUCT.md`.
- [x] **Master Documentation**: Root `README.md` with badges, setup instructions, API list, and roadmap.
- [x] **Engineering Decision Records**: `ADR-001` through `ADR-006` in `docs/engineering-decisions/`.
- [x] **Architecture Diagrams**: Context, ERD, Deployment, and Sequence diagrams in `docs/architecture/diagrams/`.
- [x] **Interview Defense Kit**: Live demo script in `demo/DemoScript.md`.

---

## 4. Deep Engineering Concepts (Implemented Impartation)

### Concept 1: Enterprise `.gitignore`
- **WHAT**: Configuration file specifying untracked files Git must exclude.
- **WHY**: Protects security credentials (`.env`), prevents repository bloat (`/target/`, `*.jar`), and isolates developer IDE configs (`.idea/`).
- **WHEN**: Created during initial repository setup before staging code.
- **HOW**: Git matches pattern rules sequentially during execution.
- **ADVANTAGES**: Secures secrets, speeds up git status/clone, preserves RAM.
- **LIMITATIONS**: Does not automatically untrack previously committed files.
- **REAL-WORLD EXAMPLE**: An engineer commits an AWS secret in `.env`. Automated bots scan GitHub public feeds within 10 seconds and compromise the cloud account. A proper `.gitignore` prevents this leak.

---

### Concept 2: Architecture Decision Records (ADRs)
- **WHAT**: Short, version-controlled markdown documents capturing significant architectural decisions.
- **WHY**: Preserves tribal knowledge and explains *why* choices were made so future developers don't re-debate past decisions.
- **WHEN**: Created whenever a major tech choice, data model, or framework selection is finalized.
- **HOW**: Structured with Title, Status (ACCEPTED/PROPOSED), Context, Decision, Consequences, and Alternatives.
- **ADVANTAGES**: Accelerates developer onboarding and defends system design in technical interviews.
- **LIMITATIONS**: Requires developer discipline to write during fast-paced development.
- **REAL-WORLD EXAMPLE**: A new developer asks why PostgreSQL with `pgvector` was chosen over Pinecone. Pointing them to `ADR-002-why-postgresql.md` resolves the question immediately.

---

## 5. Technology Choices Justification Matrix (Section 01 Stack)

| Tech Selection | Why Implemented? | Alternatives Considered | Core Advantage | Limitation |
| :--- | :--- | :--- | :--- | :--- |
| **Java Spring Boot 3.2** | Enterprise-grade static typing & `@Transactional` management | Node.js, Go | Vast enterprise security & JPA ecosystem | Higher RAM footprint (~200MB) |
| **PostgreSQL 16 + pgvector** | Unified relational data + 1536-dim vector search in 1 DB | MongoDB, Pinecone | Single ACID database roundtrip for hybrid queries | Scaling past 10M vectors requires sharding |
| **Python FastAPI** | Asynchronous ASGI microservice for isolated ML workloads | Flask, Django | Direct Python AI/ML ecosystem access | Dynamic typing requires Pydantic v2 |
| **Redis 7.2** | In-memory distributed cache, token store, & rate limiter | Memcached, JVM Local Cache | Sub-millisecond operations shared across pods | RAM capacity bound |
| **Docker Compose** | Single-command local infrastructure containerization | Host installation, Full VMs | 100% environment parity across dev/prod | Requires Docker runtime knowledge |
| **Modular Monolith** | Strict package isolation (`com.intelliflow.modules.*`) | Distributed Microservices | High development speed without saga overhead | Requires developer package discipline |

---

## 6. Industry Perspective

- **Startups**: Focus on rapid delivery via Modular Monoliths and Docker Compose. Single-command setup keeps developer onboarding under 15 minutes.
- **Product Companies**: Enforce Flyway migrations, conventional commit guidelines, and RFC 7807 error standards. ADRs become mandatory for engineering alignment.
- **FAANG Enterprises**: Require strict compliance auditing, automated secret scanning (`git-leaks`), C4 architecture diagrams, and formal ADR review committees.

---

## 7. Common Student Mistakes & Professional Fixes

1. **Hardcoding secrets in source code or `.env`**: Use `.env.example` templates and exclude `.env` via `.gitignore`.
2. **Relying on ORM auto-schema generation (`ddl-auto: update`)**: Use version-controlled Flyway SQL migration scripts (`V1__init_schema.sql`).
3. **Omitting engineering decision context**: Record decisions in ADRs to avoid architectural amnesia.
