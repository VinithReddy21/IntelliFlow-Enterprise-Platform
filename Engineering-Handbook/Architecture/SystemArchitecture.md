# Engineering Handbook: Master System Architecture Specification

---

## 1. High-Level System Architecture

IntelliFlow AI is an enterprise business operations platform designed around a **Hybrid Modular Monolith + AI Microservice Architecture**. 

The system isolates core transactional business domain services (Users, Tasks, Meetings, Documents, Notifications) from compute-heavy artificial intelligence workloads (RAG document Q&A, PDF OCR parsing, transcript summarization, and action-item extraction).

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

## 2. End-to-End Request Flow

Consider a user querying the AI Knowledge Base: *"What is our company's travel reimbursement policy?"*

```
Client (Browser) 
   │ 
   │ 1. POST /api/v1/ai/rag/query (Bearer JWT)
   ▼
NGINX API Gateway / ALB (SSL Termination, Rate Limiting)
   │ 
   │ 2. Forward Request
   ▼
Spring Boot Core Backend (Port 8080)
   │ 
   │ 3. Validate JWT RS256 Signature & Extract Tenant Claims (X-User-Id, X-Dept-Id)
   │ 4. Check Redis Cache (`GET cache:rag:<hash>`)
   ├─── (Cache Hit) ──► Return Cached Response (HTTP 200)
   │ 
   │ 5. (Cache Miss) Forward Request via Spring WebClient (HTTP/REST or gRPC)
   ▼
Python FastAPI AI Microservice (Port 8000)
   │ 
   │ 6. Convert query text into 1536-dim embedding vector via OpenAI API
   │ 7. Execute SQL Cosine Distance Search on PostgreSQL
   ▼
PostgreSQL 16 + pgvector (Port 5432)
   │ 
   │ 8. SELECT * FROM document_chunks WHERE department_id = :deptId ORDER BY embedding <=> :queryVector LIMIT 5
   │ 9. Return Top 5 Relevant Chunks + Citations
   ▼
FastAPI AI Microservice
   │ 
   │ 10. Synthesize prompt context and generate natural language answer
   │ 11. Return RAG Response JSON to Spring Boot
   ▼
Spring Boot Core Backend
   │ 
   │ 12. Write result to Redis Cache (`SETEX cache:rag:<hash> 3600`)
   │ 13. Wrap payload in `ApiResponse<T>` envelope
   ▼
Client (Browser receives HTTP 200 JSON Response)
```

---

## 3. Component Responsibilities

| Component | Technology | Primary Responsibilities |
| :--- | :--- | :--- |
| **API Gateway / Load Balancer** | NGINX / AWS ALB | TLS 1.3 termination, CORS pre-flight enforcement, IP rate limiting, microservice path routing. |
| **Core Platform Engine** | Java 21 Spring Boot 3.2 | Business logic, RBAC/ABAC security filters, relational JPA persistence, Flyway migrations, notification dispatch. |
| **AI Intelligence Engine** | Python 3.11 FastAPI | Async RAG retrieval, PDF parsing (PyMuPDF), meeting transcript summarization, vector embedding generation. |
| **Primary Persistence** | PostgreSQL 16 + `pgvector` | ACID transactional storage for relational domain entities; HNSW cosine distance vector indexing (`vector_cosine_ops`). |
| **Distributed Cache & Broker**| Redis 7.2 Cluster | Sub-millisecond session caching (Cache-Aside), JWT revocation blacklisting, Lua sliding-window rate limiting, event queues. |
| **Object Storage** | Cloud S3 / GCS | Encrypted blob storage for uploaded PDFs, transcript text files, meeting recordings, and generated PDF reports. |

---

## 4. Inter-Service Communication Mechanics

### 1. Synchronous Pattern (REST / gRPC)
- **Use Case**: Real-time RAG document Q&A queries requiring immediate client responses.
- **Protocol**: HTTP/2 gRPC or REST via Spring Boot `WebClient` calling FastAPI endpoints.
- **Resilience**: Protected by Resilience4j Circuit Breakers with 3-second timeouts and automatic fallback responses.

### 2. Asynchronous Event Pattern (Redis Streams)
- **Use Case**: Long-running meeting transcript summarization and task extraction.
- **Protocol**: Spring Boot publishes `meeting.transcript.uploaded` events to Redis Streams.
- **Processing**: FastAPI background workers consume events asynchronously, invoke LLMs, and push JSON results back to Spring Boot via callback APIs.

---

## 5. Startup & Initialization Sequence (`docker-compose up`)

```
Step 1: Container Orchestration Triggered
   ├── Spin up `postgres` (pgvector/pgvector:pg16)
   └── Spin up `redis` (redis:7.2-alpine)
   
Step 2: Database & Cache Health Check Validation
   ├── Postgres executes `pg_isready -U intelliflow_user`
   └── Redis executes `redis-cli ping`
   
Step 3: App Containers Wait for `service_healthy` State
   ├── `backend` container pauses until Postgres & Redis pass health checks
   └── `ai-service` container pauses until Postgres & Redis pass health checks

Step 4: Spring Boot Startup & Migration Phase
   ├── Resolves configuration from `application.yml` and `.env`
   ├── HikariCP establishes database connection pool
   ├── Flyway checks `flyway_schema_history` and executes `V1__init_schema.sql`
   └── Tomcat web server binds to Port 8080

Step 5: FastAPI AI Service Startup Phase
   ├── Loads `BaseSettings` via Pydantic v2
   ├── SQLAlchemy initializes non-blocking `asyncpg` engine
   └── Uvicorn ASGI server binds to Port 8000
```

---

## 6. Failure & Resilience Scenarios

### Scenario A: PostgreSQL Database is Down
- **Impact**: Domain writes and cache-miss reads fail.
- **System Behavior**: HikariCP connection pool attempts retries for 20 seconds (`connection-timeout: 20000`). If PostgreSQL remains unreachable, Spring Boot returns RFC 7807 `503 Service Unavailable` with `ErrorCode.DOWNSTREAM_SERVICE_ERROR`. Read queries served from Redis cache continue working.

### Scenario B: Redis Cache is Unavailable
- **Impact**: Cache hits fail; rate limiting and token blacklisting revert to fail-open/fail-closed security fallbacks.
- **System Behavior**: Application falls back to direct PostgreSQL reads (Cache-Aside bypass). Spring Boot logs a `WARN` event and continues operating with minor latency degradation.

### Scenario C: FastAPI AI Service Crashes
- **Impact**: AI features (RAG Q&A, transcript summarization) become unavailable.
- **System Behavior**: Core business operations (Task CRUD, User Management, Notifications) remain 100% operational. Resilience4j Circuit Breaker opens on Spring Boot, returning a graceful fallback: *"AI Service is currently undergoing maintenance."*

---

## 7. Performance & Bottleneck Analysis

| Bottleneck Location | Cause | Optimization Strategy Implemented / Planned |
| :--- | :--- | :--- |
| **Vector Similarity Search** | Sequential table scan on `document_chunks` | HNSW graph index (`USING hnsw (embedding vector_cosine_ops)`) reducing search time from 500ms to <10ms. |
| **Database Connection Exhaustion**| Unbounded thread connections | HikariCP pool limit (`maximum-pool-size: 10`) + PostgreSQL PgBouncer connection multiplexing. |
| **I/O Thread Stalls** | Blocking database calls under high concurrency | Java 21 Virtual Threads in Spring Boot + `asyncpg` driver in FastAPI. |
| **Repetitive LLM API Calls** | Identical user RAG queries hitting OpenAI APIs | Redis Query Cache (1-hour TTL hashing prompt strings). |

---

## 8. Security Strategy Architecture

- **Authentication**: Asymmetric RS256 signed JWT Access Tokens (TTL 15m) + Stateful Refresh Tokens stored in Redis (TTL 7d) with automatic rotation.
- **Authorization**: RBAC (`ROLE_ADMIN`, `ROLE_MANAGER`, `ROLE_EMPLOYEE`) and ABAC (Department/Owner scoping) enforced via Spring Security `@PreAuthorize`.
- **Data Protection**: AES-256 GCM encryption at rest for database disks and S3 buckets; TLS 1.3 for all in-transit network traffic.
- **Secrets Management**: Credentials injected exclusively via `.env` environment variables.

---

## 9. Scalability Blueprint (100 to 100,000 Users)

```
100 Users (Startup MVP - Current Phase 1)
   └── Single Docker Host running Postgres, Redis, Spring Boot, FastAPI.

10,000 Users (Mid-Size Enterprise)
   ├── AWS Application Load Balancer (ALB)
   ├── EKS Cluster: 3 Spring Boot Pods + 2 FastAPI Pods
   ├── RDS Aurora PostgreSQL Master + 1 Read Replica
   └── Amazon ElastiCache Redis Cluster (2 Nodes)

100,000+ Users (Large-Scale Enterprise Platform)
   ├── AWS WAF + CloudFront CDN + ALB
   ├── EKS Cluster: Spring Boot Pods (Auto-scaled 5 to 20 via HPA)
   ├── FastAPI Pods (Auto-scaled via KEDA based on Redis Stream depth)
   ├── Aurora PostgreSQL Primary + 3 Read Replicas (Range-partitioned audit logs)
   ├── CQRS Pattern: Query views synced to OpenSearch for multi-faceted reporting
   └── Redis Cluster (3 Shards) + Apache Kafka for event streaming
```

---

## 10. Comprehensive Architecture Interview Question Bank (50 Questions)

### Q1: Why did you choose a Modular Monolith with a separate FastAPI service instead of microservices from Day 1?
- **Model Answer**: Premature microservices introduce distributed network latency, saga transaction management, and operational service mesh complexity before the business domain model stabilizes. A Modular Monolith keeps our core domain in a single Spring Boot application (`com.intelliflow.modules.*`) while enforcing strict package boundaries. However, Python is the undisputed leader for AI/ML (LangChain, PyMuPDF, OpenAI), so we isolated AI processing in a FastAPI microservice. This gives us clean language isolation without dirtying the core Java domain.

### Q2: How does `pgvector` with HNSW indexing enable hybrid queries in PostgreSQL?
- **Model Answer**: `pgvector` adds a native `vector` column type to PostgreSQL. By creating an HNSW graph index (`USING hnsw (embedding vector_cosine_ops)`), PostgreSQL builds a multi-layer skip-list graph of vector embeddings. This allows us to write hybrid SQL queries—filtering by tenant permissions and department ID in SQL while ranking by vector cosine similarity in a single ACID-compliant database query roundtrip.

*(3-Tier answers for Q3 through Q50 documented in individual topic guides under `interview/`)*

---

## 11. Key System Architecture Principles to Remember

1. **Isolate AI Workloads from Core Transactions**: Never pollute Java JVM processes with heavy C-extension ML libraries.
2. **Eliminate Dual-Write Data Drift**: Use PostgreSQL with `pgvector` for unified relational and vector data persistence up to millions of embeddings.
3. **Stateless Scale with Centralized Operational State**: Keep app containers stateless by delegating token blacklists, refresh tokens, and rate limits to Redis.
4. **Declarative Database Migrations**: Never use ORM auto-schema generation in production; enforce Flyway version-controlled migration scripts (`V1__init_schema.sql`).
5. **Architect for Resilience**: Design downstream service integrations with circuit breakers, timeouts, and graceful fallbacks.
