# Database Interview Knowledge Base

## Q1: Why do we use Flyway database migrations instead of JPA schema auto-generation (`ddl-auto: update`)?

### Level 1 — Campus Placement Answer
> "Flyway is a database migration tool that uses SQL scripts to create and update database tables. We don't use `ddl-auto: update` because it can accidentally delete data or make unwanted changes to the production database when entity classes are modified."

### Level 2 — Product Company Answer
> "JPA's `ddl-auto: update` is non-deterministic. It inspects entity annotations and attempts to modify the database, but it cannot rename columns, handle data type migrations safely, or manage complex foreign key constraints. Flyway enforces version-controlled SQL migration scripts (`V1__init_schema.sql`). It tracks executed scripts in a `flyway_schema_history` table using checksums, ensuring that all developers, staging environments, and production clusters execute identical schema mutations."

### Level 3 — Senior Engineer Answer
> "In enterprise production setups, database migrations must support blue-green zero-downtime deployments. Using `ddl-auto` breaks deployments because ORM schema alterations run at runtime without lock control. Flyway allows us to follow the **Expand-Contract Migration Pattern**: we write backward-compatible migration scripts (e.g., adding a new nullable column first, migrating data asynchronously, then dropping the old column in a later release). Additionally, Flyway scripts are stored in Git, enabling pull request reviews, auditability, and automated execution as part of our CI/CD deployment pipeline."

---

## Q2: How does PostgreSQL `pgvector` work and how does HNSW indexing compare to IVFFlat?

### Level 1 — Campus Placement Answer
> "pgvector is an extension for PostgreSQL that allows storing vector embeddings of text documents. HNSW is a graph-based indexing algorithm used to find the most similar document vectors very quickly using cosine similarity."

### Level 2 — Product Company Answer
> "pgvector extends PostgreSQL with a native `vector` data type and vector operators like `<=>` for cosine distance. Without an index, finding the nearest vector requires an exact sequential scan over all rows, which is slow for large datasets. `IVFFlat` (Inverted File Flat) divides vectors into clusters, but requires rebuilding the index as data grows. `HNSW` (Hierarchical Navigable Small World) builds a multi-layer graph where nodes are vectors. Searching traverses the graph from sparse top layers to dense bottom layers, offering sub-millisecond approximate nearest neighbor (ANN) search with higher recall."

### Level 3 — Senior Engineer Answer
> "From an architectural perspective, `pgvector` with HNSW graph indexing (`USING hnsw (embedding vector_cosine_ops)`) gives us sub-10ms semantic search directly inside PostgreSQL. Unlike IVFFlat, HNSW does not require a pre-training step on existing data and supports incremental vector inserts without degrading search recall. By choosing `pgvector` over dedicated vector DBs (Pinecone/Milvus), we maintain ACID transactional consistency across relational metadata and vector embeddings in a single database roundtrip, eliminating dual-write consistency issues."
