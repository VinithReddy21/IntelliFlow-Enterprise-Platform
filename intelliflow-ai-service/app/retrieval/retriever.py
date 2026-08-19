import math
import logging
from typing import List, Dict, Any, Optional
from sqlalchemy import text
from app.db.session import async_engine
from app.services.embedding_service import EmbeddingService

logger = logging.getLogger("intelliflow.retrieval.pgvector")

# Global in-memory document chunk registry for offline / local-first operation
IN_MEMORY_CHUNK_REGISTRY: List[Dict[str, Any]] = [
    {
        "chunk_id": "c1-architecture-overview",
        "document_id": "d1-platform-spec",
        "document_title": "IntelliFlow Platform Architecture & Technology Stack",
        "chunk_index": 0,
        "content": "IntelliFlow AI is an enterprise business operations platform built on a Hybrid Modular Monolith + AI Microservice architecture. The Core Backend Engine is written in Java 21 Spring Boot 3.2 managing transactional business logic, task lifecycles, and RBAC/ABAC security. The AI Intelligence Microservice is implemented in Python 3.11/3.12 FastAPI orchestrating Document Ingestion, PDF/DOCX parsing, 384-dimensional dense vector embeddings, and RAG retrieval.",
        "embedding": None,
        "token_count": 82
    },
    {
        "chunk_id": "c2-ai-vector-engine",
        "document_id": "d1-platform-spec",
        "document_title": "IntelliFlow AI & pgvector RAG Engine Specification",
        "chunk_index": 1,
        "content": "IntelliFlow's AI microservice utilizes sentence-transformers/all-MiniLM-L6-v2 to encode text into 384-dimensional dense float vector embeddings. Vectors are indexed in PostgreSQL using pgvector HNSW (Hierarchical Navigable Small World) cosine distance index (<=> operator). High-speed LLM inference is powered by Groq LPU hardware with zero hallucinations through grounded 1:1 citations.",
        "embedding": None,
        "token_count": 76
    },
    {
        "chunk_id": "c3-security-compliance",
        "document_id": "d2-security-standard",
        "document_title": "Enterprise Security, Rate Limiting & Auth Standard",
        "chunk_index": 0,
        "content": "Security is enforced via Stateless RS256/HS256 JWT dual-tokens, BCrypt password hashing (Cost 12), and an IP-based sliding window token bucket RateLimitingFilter (10 req/min on Auth endpoints). Department-level Attribute-Based Access Control (ABAC) guarantees multi-tenant document isolation.",
        "embedding": None,
        "token_count": 58
    },
    {
        "chunk_id": "c4-frontend-workspace",
        "document_id": "d3-frontend-spec",
        "document_title": "React 19 Frontend & Enterprise Kanban Workspace",
        "chunk_index": 0,
        "content": "The user interface is built with React 19, TypeScript, Tailwind CSS, and Vite. It features a real-time Kanban Task Board, Document Vault with drag-and-drop ingestion, and an interactive ChatGPT-style AI Copilot Canvas with source citation panels and telemetry meters.",
        "embedding": None,
        "token_count": 52
    }
]

def cosine_similarity(v1: List[float], v2: List[float]) -> float:
    dot = sum(a * b for a, b in zip(v1, v2))
    mag1 = math.sqrt(sum(a * a for a in v1))
    mag2 = math.sqrt(sum(b * b for b in v2))
    if mag1 == 0 or mag2 == 0:
        return 0.0
    return dot / (mag1 * mag2)

def register_in_memory_chunk(chunk: Dict[str, Any]):
    IN_MEMORY_CHUNK_REGISTRY.append(chunk)

class PgVectorRetriever:
    """
    Production pgvector Similarity Search & Candidate Ranking Retriever.
    Supports native PostgreSQL pgvector HNSW queries and in-memory cosine fallback.
    """

    def __init__(self, embedding_service: EmbeddingService):
        self.embedding_service = embedding_service

    async def retrieve_similar_chunks(
        self,
        query: str,
        top_k: int = 5,
        department_id: Optional[str] = None
    ) -> List[Dict[str, Any]]:
        query_vector = self.embedding_service.encode_query(query)
        vector_str = f"[{','.join(str(v) for v in query_vector)}]"

        logger.info(f"Executing pgvector HNSW similarity search for query: '{query[:40]}...' | TopK: {top_k}")

        sql_query = text("""
            SELECT 
                dc.id AS chunk_id,
                dc.document_id,
                COALESCE(d.title, 'Corporate Document') AS document_title,
                dc.chunk_index,
                dc.content,
                dc.token_count,
                (1.0 - (dc.embedding <=> :vector_str::vector)) AS similarity_score
            FROM document_chunks dc
            JOIN documents d ON dc.document_id = d.id
            WHERE d.deleted_at IS NULL
              AND (:dept_id IS NULL OR d.department_id = :dept_id::uuid)
            ORDER BY dc.embedding <=> :vector_str::vector
            LIMIT :top_k
        """)

        if async_engine is not None:
            try:
                async with async_engine.connect() as conn:
                    result = await conn.execute(
                        sql_query,
                        {
                            "vector_str": vector_str,
                            "dept_id": department_id,
                            "top_k": top_k
                        }
                    )
                    rows = result.fetchall()
                    if rows:
                        chunks = []
                        for r in rows:
                            chunks.append({
                                "chunk_id": str(r.chunk_id),
                                "document_id": str(r.document_id),
                                "document_title": r.document_title,
                                "chunk_index": r.chunk_index,
                                "content": r.content,
                                "similarity_score": round(float(r.similarity_score), 4),
                                "token_count": r.token_count
                            })
                        logger.info(f"Retrieved {len(chunks)} real pgvector candidate chunks from database.")
                        return chunks
            except Exception as e:
                logger.warning(f"Database pgvector query failed ({str(e)}). Utilizing dynamic in-memory vector store.")

        # In-Memory Dynamic Vector Cosine Ranking
        scored_chunks = []
        for chunk in IN_MEMORY_CHUNK_REGISTRY:
            chunk_emb = chunk.get("embedding")
            if chunk_emb is None:
                chunk_emb = self.embedding_service.encode_query(chunk["content"])
                chunk["embedding"] = chunk_emb

            score = cosine_similarity(query_vector, chunk_emb)
            scored_chunks.append({
                "chunk_id": chunk["chunk_id"],
                "document_id": chunk["document_id"],
                "document_title": chunk["document_title"],
                "chunk_index": chunk["chunk_index"],
                "content": chunk["content"],
                "similarity_score": round(float(score), 4),
                "token_count": chunk.get("token_count", len(chunk["content"].split()))
            })

        # Sort descending by cosine similarity
        scored_chunks.sort(key=lambda x: x["similarity_score"], reverse=True)
        top_results = scored_chunks[:top_k]
        logger.info(f"Retrieved {len(top_results)} in-memory candidate chunks with top similarity {top_results[0]['similarity_score'] if top_results else 0.0}")
        return top_results
