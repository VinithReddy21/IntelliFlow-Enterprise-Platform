# Section 01 Interview Questions: Advanced Tier

## Q1: How do Java 21 Virtual Threads change concurrency execution in Spring Boot 3.2?
- **Ideal Answer**: Traditional Java platform threads map 1:1 to OS kernel threads, consuming ~1MB memory per thread and stalling during blocking I/O. Java 21 Virtual Threads are lightweight JVM-managed threads. When a virtual thread blocks on DB I/O, the JVM unmounts it from the carrier thread, allowing that carrier thread to process other virtual threads. This provides high concurrency for I/O-bound web applications using simple synchronous code.
- **Common Wrong Answer**: *"Virtual threads make CPU-bound mathematical processing 10x faster."*
- **Follow-up Question**: What is thread pinning in Virtual Threads, and how do `synchronized` blocks cause it?
- **Interview Tip**: Emphasize I/O-bound concurrency scaling vs CPU execution.

## Q2: How does the HNSW graph index work in `pgvector` for vector similarity search?
- **Ideal Answer**: HNSW (Hierarchical Navigable Small World) builds a multi-layer graph where nodes are vector embeddings. Sparse top layers allow rapid long-range graph routing, while dense bottom layers fine-tune nearest-neighbor discovery (`vector_cosine_ops`). It provides sub-10ms approximate nearest neighbor (ANN) retrieval without requiring global index retraining when new vectors are inserted.
- **Common Wrong Answer**: *"HNSW checks every vector sequentially in a flat list."*
- **Follow-up Question**: What parameter controls search accuracy vs speed at query time? (`hnsw.ef_search`).
- **Interview Tip**: Contrast graph traversal against flat brute-force scanning.
