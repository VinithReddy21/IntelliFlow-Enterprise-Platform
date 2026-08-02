# Architecture Defense & High-Level System Design

## 1. Executive Summary
IntelliFlow AI is built using a **hybrid modular monolith + specialized microservice design pattern**. The main business domain engine is powered by Java 21 Spring Boot 3.2, providing multi-tenant role-based access control, task orchestration, document governance, and transactional integrity. The AI intelligence engine runs on Python FastAPI to interface directly with PyMuPDF, LangChain, and OpenAI/Local LLM pipelines.

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

## 2. Key Architectural Guarantees
1. **Zero Schema Drift**: Ensured by Flyway database migrations.
2. **Stateless Scale**: Authentication via asymmetric RS256 JWTs and Redis refresh token rotation.
3. **Sub-100ms Domain Response**: Enabled by Redis caching (Cache-Aside pattern) and JPA connection pool tuning.
4. **Isolated ML Compute**: Heavy Python document parsing does not block main Java API threads.
