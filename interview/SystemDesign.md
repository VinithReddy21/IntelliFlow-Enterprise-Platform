# System Design Interview Knowledge Base

## Q1: How would you design a Retrieval-Augmented Generation (RAG) system to answer queries over millions of enterprise documents securely?

### Level 1 — Campus Placement Answer
> "We split PDF documents into smaller chunks of text, generate vector embeddings using an embedding model, and store them in PostgreSQL with pgvector. When a user asks a question, we convert the question into a vector, find the most similar chunks, and pass them to OpenAI to generate an answer."

### Level 2 — Product Company Answer
> "1. **Ingestion Pipeline**: Uploaded PDFs are parsed via PyMuPDF in FastAPI, split using RecursiveCharacterTextSplitter (chunk size 1000, overlap 200), and converted to 1536-dimensional vectors via OpenAI `text-embedding-3-small`.
> 2. **Storage**: Vector chunks are persisted in PostgreSQL `document_chunks` with HNSW cosine distance indexing (`vector_cosine_ops`).
> 3. **Retrieval & Generation**: When queried, pgvector returns the top 5 chunks. FastAPI constructs a contextual prompt and calls GPT-4o to synthesize the final response with source citations."

### Level 3 — Senior Engineer Answer
> "To scale enterprise RAG to millions of documents securely, we address three critical constraints:
> - **Tenant Data Isolation**: Document chunks include `department_id` and `owner_id`. Vector queries run filtered search: `WHERE department_id = :deptId ORDER BY embedding <=> :queryVector LIMIT 5`. This renders cross-tenant document leaking impossible.
> - **Cache Layering**: Repeated semantic queries are hashed and cached in Redis with a 1-hour TTL, saving expensive LLM API calls.
> - **Async Worker Scaling**: PDF chunking and vectorization run asynchronously via Redis Streams and FastAPI background workers, preventing upload HTTP endpoint blocking."
