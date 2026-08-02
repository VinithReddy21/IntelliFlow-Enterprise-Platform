# Section 01: 5-Minute Summary

## Key Concepts Implemented

1. **Enterprise Repository Structure**:
   - `intelliflow-backend`: Java 21 Spring Boot core transactional engine.
   - `intelliflow-ai-service`: Python 3.11 FastAPI microservice for AI workloads.
   - `docker-compose.yml`: Multi-container orchestration (Postgres 16 + pgvector + Redis 7.2).

2. **Repository Metadata**:
   - `.gitignore`: Excludes `.env`, `/target/`, `.idea/`, `postgres_data/`.
   - `README.md`: Master dashboard with badges, architecture overview, setup, and roadmap.
   - `CONTRIBUTING.md`: Git branch strategy, code formatting, and conventional commit rules.
   - `CHANGELOG.md`: Version release log following Keep a Changelog & SemVer.
   - `CODE_OF_CONDUCT.md`: Contributor Covenant standards.

3. **Architecture Decision Records (ADRs)**:
   - `ADR-001`: Why Spring Boot 3.2 (Virtual Threads, `@Transactional`).
   - `ADR-002`: Why PostgreSQL 16 + `pgvector` (Unified ACID relational & vector search).
   - `ADR-003`: Why FastAPI (Async ASGI, ML ecosystem isolation).
   - `ADR-004`: Why Redis 7.2 (Token store, sliding window rate limiter, Cache-Aside).
   - `ADR-005`: Why Docker Compose (Dev/prod environment parity).
   - `ADR-006`: Why Modular Monolith (Avoid premature microservices overhead).

4. **Architecture Visualization**:
   - Context Diagram (C4), ERD, Deployment Diagram, Sequence Diagram under `docs/architecture/diagrams/`.
