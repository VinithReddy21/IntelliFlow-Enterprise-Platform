# Advanced Reading & Future AI Infrastructure Concepts

*Note: These concepts will be implemented in future AI phases. They serve as optional advanced reading.*

---

## 1. RAG Document Ingestion & PyMuPDF Chunking Pipeline
Splitting unstructured PDF files into contextual text chunks using `RecursiveCharacterTextSplitter` (chunk size 1000, overlap 200) to prepare text for vector embedding generation.

---

## 2. OpenAI Embedding Generation & Cosine Search
Using `text-embedding-3-small` to convert document chunks into 1536-dimensional vectors and storing them in PostgreSQL `document_chunks` for HNSW similarity ranking.

---

## 3. Streaming LLM Responses via SSE (Server-Sent Events)
Using FastAPI `StreamingResponse` to stream token-by-token LLM output back to the client interface for responsive chat experience.
