# Section 01 Interview Questions: Intermediate Tier

## Q1: Why choose a Modular Monolith over Microservices for Version 1.0?
- **Ideal Answer**: Premature microservices introduce distributed network latency, saga transaction complexity, and service mesh overhead. A Modular Monolith packages feature modules (`com.intelliflow.modules.*`) inside a single Spring Boot application while maintaining strict package boundaries. This gives high delivery speed today while keeping extraction paths open.
- **Common Wrong Answer**: *"Because microservices are obsolete."*
- **Follow-up Question**: How do you prevent cross-module coupling in Spring Boot?
- **Interview Tip**: Balance development speed against operational complexity.

## Q2: Why use PostgreSQL `pgvector` instead of a standalone Vector DB like Pinecone?
- **Ideal Answer**: Standalone vector DBs require dual-write sync across traditional databases and vector stores, introducing network latency and sync bugs. `pgvector` stores embeddings directly in PostgreSQL alongside relational metadata. This allows hybrid SQL queries (filtering by tenant and permissions while ranking vector similarity) in a single ACID transaction.
- **Common Wrong Answer**: *"Because Pinecone is too slow for all applications."*
- **Follow-up Question**: When would you extract vectors to a dedicated Qdrant/Pinecone cluster? (Past ~10M+ vectors).
- **Interview Tip**: Highlight single ACID database roundtrips.

## Q3: What is an Architecture Decision Record (ADR), and why is it important?
- **Ideal Answer**: An ADR is a short markdown document capturing a technical decision, its context, trade-offs, and alternatives considered. It preserves tribal knowledge, speeds up developer onboarding, and prevents repeated debates on past architecture decisions.
- **Common Wrong Answer**: *"It is a code comment inside Java files."*
- **Follow-up Question**: Where should ADRs be stored? (`docs/engineering-decisions/`).
- **Interview Tip**: Emphasize long-term technical clarity and team communication.
