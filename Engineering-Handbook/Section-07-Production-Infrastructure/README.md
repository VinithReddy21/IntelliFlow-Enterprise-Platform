# Section 07: Enterprise Production Infrastructure (Module 8 CI/CD & DevOps Infrastructure)

---

## 1. Prerequisites

Before reading this section, you should understand:
- Containerization best practices (Docker multi-stage builds).
- Continuous Integration pipelines (GitHub Actions syntax).
- Database migrations with Flyway (`V1__init_schema.sql`).
- Container orchestration using `docker-compose.yml`.

---

## 2. Learning Objectives

After completing Module 8, you will master:
- **Multi-Stage Dockerfiles**: Building lightweight (<250 MB) production-hardened container images executing under a non-root system user (`intelliflow`).
- **GitHub Actions Pipeline (`ci-pipeline.yml`)**: Automated multi-stage GitHub workflow running unit tests, packaging Spring Boot JARs, and building Docker images.
- **Flyway Database Migrations (`V1__init_schema.sql`)**: Declarative schema migrations enabling `pgvector` HNSW vector indexes.
- **Docker Compose Orchestration (`docker-compose.yml`)**: Production compose stack with strict healthchecks for PostgreSQL (`pg_isready`), Redis (`redis-cli ping`), Spring Boot Backend, and FastAPI AI Service.

---

## 3. DevOps & CI/CD Pipeline Matrix

| DevOps Component | Implementation | Production Standard | Benefit |
| :--- | :--- | :--- | :--- |
| **Spring Boot Dockerfile** | `maven:3.9-eclipse-temurin-21` -> `eclipse-temurin:21-jre-alpine` | Non-root `intelliflow` user, 75% MaxRAMPercentage | Hardened runtime image (< 250 MB) |
| **AI FastAPI Dockerfile** | `python:3.11-slim` multi-stage wheel build | Non-root `aiservice` user | Lightweight Python environment |
| **GitHub Actions** | `.github/workflows/ci-pipeline.yml` | Automated build, test, and container packaging | Zero-friction continuous integration |
| **Docker Compose** | `docker-compose.yml` | Healthcheck dependency graph (`service_healthy`) | Deterministic startup order |
| **Flyway Migrations** | `V1__init_schema.sql` | Automated schema creation & pgvector extension | Safe schema evolution |
