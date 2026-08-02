# Technical Architecture Trade-offs Analysis

| Architectural Decision | Chosen Strategy | Alternative Strategy | Trade-off / Why We Chose This |
| :--- | :--- | :--- | :--- |
| **Backend Framework** | Java Spring Boot 3.2 | Node.js / Express | Spring Boot provides compile-time safety and declarative transaction boundaries at the expense of higher initial RAM consumption (~200MB vs ~50MB). |
| **Vector Database** | PostgreSQL `pgvector` | Standalone Pinecone / Qdrant | `pgvector` guarantees ACID data consistency and eliminates dual-writes, though dedicated vector DBs scale better past 50M+ vectors. |
| **Database Migrations**| Flyway Versioned Scripts | JPA `ddl-auto: update` | Flyway provides deterministic production migrations, avoiding silent schema bugs and data corruption caused by ORM auto-generation. |
| **AI Service Communication** | REST API / gRPC | Embedded Python Process | Microservice network calls introduce minor network latency (~5ms) but protect the core Java JVM from heavy Python ML dependencies. |
| **Caching Layer** | Redis Cluster | Local JVM In-Memory Cache | Centralized Redis introduces network hops but enables stateless scaling across multiple backend application pod replicas. |
