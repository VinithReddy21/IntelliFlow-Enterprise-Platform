# IntelliFlow AI – Enterprise Business Operations Platform

![Build Status](https://img.shields.io/badge/build-passing-brightgreen.svg)
![Java](https://img.shields.io/badge/Java-21%20LTS-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.3-green.svg)
![FastAPI](https://img.shields.io/badge/FastAPI-0.110.0-blue.svg)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16%20%2B%20pgvector-blue.svg)
![Redis](https://img.shields.io/badge/Redis-7.2-red.svg)
![License](https://img.shields.io/badge/License-MIT-yellow.svg)

**IntelliFlow AI** is an enterprise-grade business operations platform designed for mid-sized organizations (50–100 employees, scalable to 100,000+ users). It integrates AI into everyday organizational workflows—managing employees, tasks, meetings, documents, automated reports, and notifications from a single centralized system.

---

## 🏗️ Architecture Overview

IntelliFlow AI utilizes a **Hybrid Modular Monolith + AI Microservice** pattern:
- **Core Backend Engine**: Java 21 Spring Boot 3.2 managing transactional business logic, RBAC/ABAC security, task lifecycles, and database persistence.
- **AI Intelligence Microservice**: Python 3.11 FastAPI orchestrating Retrieval-Augmented Generation (RAG), PDF parsing, meeting transcript summarization, and action-item extraction.

```
                                 +--------------------------------+
                                 |    NGINX API Gateway / ALB     |
                                 +---------------+----------------+
                                                 |
                                 +---------------+----------------+
                                 |  Spring Security Filter Chain  |
                                 +---------------+----------------+
                                                 |
                +--------------------------------+--------------------------------+
                | (Spring Boot Core Engine)                                       | (Python FastAPI AI Microservice)
                v                                                                 v
+-------------------------------+                               +-------------------------------+
|  Modules:                     |                               |  Modules:                     |
|  - Auth & Security            |    Sync REST / gRPC           |  - Document RAG Engine        |
|  - User & Department Mgmt     |------------------------------>|  - Transcript Summarizer      |
|  - Task Lifecycle             |                               |  - Action Item Extractor      |
|  - Document Metadata          |<------------------------------|  - Vector Embedder            |
|  - Notifications              |    Async Callback/Events      +---------------+---------------+
+---------------+---------------+                                               |
                |                                                               |
                +--------------------------------+------------------------------+
                                                 |
                                                 v
                                 +---------------+----------------+
                                 |  PostgreSQL 16 DB + pgvector   |
                                 |  & Redis Distributed Cache     |
                                 +--------------------------------+
```

---

## ⚡ Quick Start & Setup

### Prerequisites
- [Docker & Docker Compose](https://www.docker.com/) installed
- Java 21 JDK
- Python 3.11+

### 1. Launch Infrastructure
```bash
# Copy environment secrets template
cp .env.example .env

# Spin up PostgreSQL (with pgvector), Redis, Backend, and AI Service
docker-compose up -d
```

---

## 📖 API Documentation & Endpoints

| Service | Protocol | Base URL | Swagger / Documentation |
| :--- | :--- | :--- | :--- |
| **Spring Boot Core API** | HTTP/REST | `http://localhost:8080` | `http://localhost:8080/swagger-ui.html` |
| **FastAPI AI Microservice** | HTTP/REST | `http://localhost:8000` | `http://localhost:8000/docs` |
| **Health Observability** | HTTP/REST | `http://localhost:8080/actuator/health` | Standard RFC JSON |

---

## 🗺️ Product Roadmap

- [x] **Phase 1: Project Baseline & Architecture Setup** (Docker, PostgreSQL + pgvector, Flyway, ADRs, Swagger)
- [x] **Phase 2 — Section 4.1 & 4.2: Authentication Engine** (RS256 Dual-Token JWT, BCrypt Cost 12, Redis Refresh Token Rotation & Blacklist)
- [ ] **Phase 2 — Section 4.3: User Domain & Lifecycle Governance** (State Machine, Account Locking, Soft Delete, Profile APIs)
- [ ] **Phase 2 — Section 4.4: Role & Attribute-Based Authorization** (RBAC / ABAC, `@PreAuthorize`)
- [ ] **Phase 3: Core Domain Modules** (Task Management, Document Storage, Meeting Workflows)

---

## 📚 Technical Documentation & Handbooks

- **Architecture Decision Records**: Inspect [docs/engineering-decisions/](file:///c:/PROJECT_1/docs/engineering-decisions/ADR-007-why-bcrypt-over-argon2id.md) (ADR-001 through ADR-007)
- **Modular Engineering Handbook**:
  - [Section 01: Repository Foundation](file:///c:/PROJECT_1/Engineering-Handbook/Section-01-Repository-Foundation/README.md)
  - [Section 03: FastAPI AI Service](file:///c:/PROJECT_1/Engineering-Handbook/Section-03-FastAPI-AI-Service-Infrastructure/README.md)
  - [Section 04: Authentication Engineering](file:///c:/PROJECT_1/Engineering-Handbook/Section-04-Authentication/README.md)
  - [Section 04: User Domain Design](file:///c:/PROJECT_1/Engineering-Handbook/Section-04-User-Domain-Design/README.md)
