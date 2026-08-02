# ADR-002: Selection of PostgreSQL 16 with pgvector extension

## Status
**ACCEPTED**

## Context
IntelliFlow AI requires a database capable of storing structured relational business data (users, departments, tasks, meetings, audit logs) with strict ACID transactional guarantees, while also supporting vector embedding similarity search for Retrieval-Augmented Generation (RAG) over company documents.

## Alternatives Considered

### 1. MongoDB (NoSQL Document Store)
- **Pros**: Flexible schema design, simple JSON document storage.
- **Cons**: Poor relational integrity enforcement for organizational hierarchies and task references; multi-document ACID transactions introduce performance penalties.

### 2. PostgreSQL + External Vector Database (Pinecone / Milvus)
- **Pros**: Dedicated vector databases offer specialized indexing algorithms for 100M+ vectors.
- **Cons**: Introducing a separate vector database requires dual-write synchronization logic, risk of data drift, network latency across boundaries, and additional infrastructure costs.

### 3. PostgreSQL 16 with `pgvector` extension (SELECTED)
- **Pros**: Single unified database for relational domain entities and 1536-dimensional vector embeddings; native support for HNSW graph indexing (`vector_cosine_ops`); enables hybrid SQL queries (e.g., filter by department ID and match semantic similarity in one atomic query).
- **Cons**: Scaling past 10 million vector embeddings may eventually require sharding or dedicated vector cluster extraction.

## Decision
We select **PostgreSQL 16 with `pgvector`** as our primary relational and vector storage engine.

## Interview Defense & Key Summary
> *"We chose PostgreSQL with `pgvector` to avoid distributed data synchronization bugs between a traditional database and a standalone vector database. Combining relational queries with semantic vector similarity in a single ACID-compliant database roundtrip reduces operational complexity and guarantees data consistency."*
