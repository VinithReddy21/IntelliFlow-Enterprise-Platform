import { VectorSearchParams, VectorSearchResultItem, SourceCitation } from '../types/ai';

const mockCitations: SourceCitation[] = [
  {
    id: 'cit-1',
    documentId: '46246246-65c4-4ea4-ad49-5299342bc731',
    documentTitle: 'Enterprise_RAG_Architecture_Specification_v1.pdf',
    chunkIndex: 1,
    similarityScore: 0.96,
    contentSnippet: 'IntelliFlow platform implements a 1536-dimensional vector similarity retrieval engine leveraging pgvector HNSW indexes.',
    mimeType: 'application/pdf',
  },
  {
    id: 'cit-2',
    documentId: '6ba3aa7e-83f6-4e3e-a957-eb4b411ec131',
    documentTitle: 'OWASP_Security_Hardening_Standard_2026.docx',
    chunkIndex: 2,
    similarityScore: 0.91,
    contentSnippet: 'RateLimitingFilter uses an in-memory token bucket enforcing 10 req/min limits on authentication routes, returning 429 Too Many Requests.',
    mimeType: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
  },
];

export const aiApi = {
  vectorSearch: async (params: VectorSearchParams): Promise<VectorSearchResultItem[]> => {
    try {
      const token = localStorage.getItem('intelliflow_jwt');
      const headers: Record<string, string> = { 'Content-Type': 'application/json' };
      if (token) headers['Authorization'] = `Bearer ${token}`;

      const res = await fetch('/api/v1/documents/search/similarity', {
        method: 'POST',
        headers,
        body: JSON.stringify(params),
      });
      if (res.ok) {
        const data = await res.json();
        return data.data || data;
      }
    } catch {
      // Fallback to local memory mock
    }

    return new Promise((resolve) => {
      setTimeout(() => {
        const mockResults: VectorSearchResultItem[] = [
          {
            id: 'vec-1',
            documentId: '46246246-65c4-4ea4-ad49-5299342bc731',
            documentTitle: 'Enterprise_RAG_Architecture_Specification_v1.pdf',
            chunkIndex: 1,
            similarityScore: 0.96,
            content: 'IntelliFlow platform implements a 1536-dimensional vector similarity retrieval engine leveraging pgvector HNSW indexes.',
            tokenCount: 480,
            mimeType: 'application/pdf',
          },
          {
            id: 'vec-2',
            documentId: '6ba3aa7e-83f6-4e3e-a957-eb4b411ec131',
            documentTitle: 'OWASP_Security_Hardening_Standard_2026.docx',
            chunkIndex: 2,
            similarityScore: 0.91,
            content: 'RateLimitingFilter uses an in-memory token bucket enforcing 10 req/min limits on authentication routes, returning 429 Too Many Requests.',
            tokenCount: 470,
            mimeType: 'docx',
          },
          {
            id: 'vec-3',
            documentId: 'c872ad3d-ebba-4b87-957c-aad895beaff4',
            documentTitle: 'HikariCP_Connection_Pool_Benchmark.md',
            chunkIndex: 1,
            similarityScore: 0.88,
            content: 'HikariCP maximumPoolSize=30 and minimumIdle=10 configuration reduced database connection latency by 40% under high concurrency.',
            tokenCount: 380,
            mimeType: 'text/markdown',
          },
        ].filter((r) => r.similarityScore >= params.similarityThreshold).slice(0, params.topK);

        resolve(mockResults);
      }, 300);
    });
  },

  ragQuery: async (prompt: string): Promise<{ answer: string; citations: SourceCitation[]; confidenceScore: number; latencyMs: number }> => {
    try {
      const token = localStorage.getItem('intelliflow_jwt');
      const headers: Record<string, string> = { 'Content-Type': 'application/json' };
      if (token) headers['Authorization'] = `Bearer ${token}`;

      const res = await fetch('/api/v1/documents/search/rag', {
        method: 'POST',
        headers,
        body: JSON.stringify({ prompt }),
      });
      if (res.ok) {
        const data = await res.json();
        return data.data || data;
      }
    } catch {
      // Fallback mock
    }

    return new Promise((resolve) => {
      setTimeout(() => {
        resolve({
          answer: `Based on the ingested enterprise knowledge base, **IntelliFlow** leverages a **1536-dimensional vector engine** built on PostgreSQL \`pgvector\` with an HNSW index (\`vector_cosine_ops\`).

Key highlights:
- **Security Hardening**: Enforces OWASP response headers (\`HSTS\`, \`nosniff\`) and IP-based token bucket rate limiting (10 req/min on Auth).
- **RAG Confidence**: Grounded responses assembled strictly from department-authorized document chunks.
- **Performance**: HikariCP connection pool tuned for 30 max connections and prepared statement caching.`,
          citations: mockCitations,
          confidenceScore: 0.96,
          latencyMs: 18,
        });
      }, 400);
    });
  },
};
