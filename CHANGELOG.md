# Changelog

All notable changes to the IntelliFlow AI Platform will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0-MVP] - 2026-08-01

### Added
- **Infrastructure**: Polyglot architecture with Java 21 Spring Boot core engine and Python 3.11 FastAPI AI microservice.
- **Persistence**: PostgreSQL 16 schema with `pgvector` extension for 1536-dimensional HNSW cosine similarity vector search.
- **Database Migrations**: Flyway migration pipeline with versioned `V1__init_schema.sql` script.
- **Caching & Rate Limiting**: Redis 7.2 container integration for sub-millisecond caching, token blacklisting, and rate limiting.
- **Containerization**: Multi-stage Dockerfiles and root `docker-compose.yml` for unified local stack orchestration.
- **Documentation & ADRs**: Created Architecture Decision Records (`ADR-001` through `ADR-006`), C4 architecture diagrams, and 3-tier interview knowledge base.
