# Detailed Model Answers for Technical Interviews

## Category 1 Answers: Architecture & Frameworks

### Q1.1: Why Modular Monolith + Python AI Microservice?
> **Answer**: "We selected a Modular Monolith for our backend core because premature microservices introduce distributed system operational overhead—such as network latency, distributed transaction sagas, and service mesh management. By organizing our Spring Boot backend into clean feature modules (`com.intelliflow.modules.*`), we keep domain deployment simple while enforcing strict package boundaries. However, Python is the clear leader for AI processing (LangChain, PyMuPDF, embeddings), so we isolated AI processing in a lightweight FastAPI microservice. This gives us clean language isolation without dirtying the core Java domain."

### Q1.3: What is the benefit of Java 21 Virtual Threads?
> **Answer**: "Traditional Java threads map 1:1 to OS threads, which consume ~1MB stack memory per thread. Under heavy concurrent blocking I/O (e.g., database queries or downstream HTTP calls), platform threads stall OS threads. Java 21 Virtual Threads are lightweight threads managed by the JVM that cost mere bytes of RAM. When a virtual thread blocks on DB I/O, the underlying OS thread is unmounted to process other tasks, enabling us to handle tens of thousands of concurrent requests using simple synchronous code."

---

## Category 2 Answers: Database & Vector Search

### Q2.1: Why PostgreSQL + `pgvector` over standalone Pinecone/Milvus?
> **Answer**: "For enterprise SaaS platforms managing both relational data (users, tasks) and vector embeddings (document chunks), a standalone vector DB creates dual-write transaction problems. If a document is updated, syncing data across Postgres and Pinecone can fail or suffer network latency. With `pgvector`, we store document vectors alongside relational metadata in PostgreSQL. This allows us to write hybrid queries—filtering by tenant and permission in SQL while ranking by vector cosine similarity in a single ACID-compliant database query."

---

## Category 3 Answers: Security & Redis Caching

### Q3.2: How does Redis enable Rate Limiting & Token Revocation?
> **Answer**: "Because JWT access tokens are stateless, revoking them before expiration requires a central blacklist. When a user logs out, we write the JWT's unique identifier (`jti`) to Redis with a TTL matching the token's remaining lifespan. For rate limiting, we use Redis Lua scripts to execute atomic sliding-window algorithm checks, tracking API request counts per user IP with sub-millisecond overhead."
