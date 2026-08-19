import { DocumentItem } from '../types/document';

const initialMockDocuments: DocumentItem[] = [
  {
    id: '46246246-65c4-4ea4-ad49-5299342bc731',
    fileName: 'Enterprise_RAG_Architecture_Specification_v1.pdf',
    fileKey: 'docs/46246246_Enterprise_RAG.pdf',
    checksum: 'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855',
    mimeType: 'application/pdf',
    sizeBytes: 4259840, // 4.2 MB
    status: 'ACTIVE',
    uploaderId: 'usr-1',
    uploaderName: 'Alex Architect',
    departmentId: 'dept-ai',
    createdAt: '2026-08-01T14:20:00Z',
    vectorDimensions: 384,
    embeddingModel: 'sentence-transformers/all-MiniLM-L6-v2',
    chunks: [
      { id: 'chk-1', chunkIndex: 1, content: 'IntelliFlow platform implements a 384-dimensional vector similarity retrieval engine leveraging pgvector HNSW indexes.', tokenCount: 480 },
      { id: 'chk-2', chunkIndex: 2, content: 'Document ingestion pipelines process uploaded multi-part files via Apache Tika text parsing and recursive token chunking.', tokenCount: 495 },
      { id: 'chk-3', chunkIndex: 3, content: 'Document permission filtering guarantees department-level ABAC authorization prior to LLM prompt context assembly.', tokenCount: 460 },
    ],
  },
  {
    id: '6ba3aa7e-83f6-4e3e-a957-eb4b411ec131',
    fileName: 'OWASP_Security_Hardening_Standard_2026.docx',
    fileKey: 'docs/6ba3aa7e_OWASP_Standard.docx',
    checksum: '8f4e21a97d3012b704c995f00e932b1a50a12e34d6789012bc3456789def0123',
    mimeType: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
    sizeBytes: 2150400, // 2.1 MB
    status: 'ACTIVE',
    uploaderId: 'usr-3',
    uploaderName: 'Elena SecOps',
    departmentId: 'dept-sec',
    createdAt: '2026-08-02T09:10:00Z',
    vectorDimensions: 384,
    embeddingModel: 'sentence-transformers/all-MiniLM-L6-v2',
    chunks: [
      { id: 'chk-10', chunkIndex: 1, content: 'RateLimitingFilter uses an in-memory token bucket enforcing 10 req/min limits on authentication routes.', tokenCount: 410 },
      { id: 'chk-11', chunkIndex: 2, content: 'IdempotencyFilter caches 2xx HTTP POST execution payloads by Idempotency-Key header to prevent duplicate mutations.', tokenCount: 470 },
    ],
  },
  {
    id: 'c872ad3d-ebba-4b87-957c-aad895beaff4',
    fileName: 'HikariCP_Connection_Pool_Benchmark.md',
    fileKey: 'docs/c872ad3d_HikariCP_Benchmark.md',
    checksum: '5a4b3c2d1e0f9876543210fedcba9876543210abcdef9876543210fedcba9876',
    mimeType: 'text/markdown',
    sizeBytes: 153600, // 150 KB
    status: 'EMBEDDED',
    uploaderId: 'usr-2',
    uploaderName: 'David Lead',
    createdAt: '2026-08-02T11:45:00Z',
    vectorDimensions: 384,
    embeddingModel: 'sentence-transformers/all-MiniLM-L6-v2',
    chunks: [
      { id: 'chk-20', chunkIndex: 1, content: 'HikariCP maximumPoolSize=30 and minimumIdle=10 configuration reduced database latency by 40% under high concurrency.', tokenCount: 380 },
    ],
  },
  {
    id: '995cdf35-c3c2-4864-a8d9-8ea96bbab571',
    fileName: 'System_Audit_Log_Export_2026_Q3.json',
    fileKey: 'docs/995cdf35_Audit_Log.json',
    checksum: '11223344556677889900aabbccddeeff11223344556677889900aabbccddeeff',
    mimeType: 'application/json',
    sizeBytes: 819200, // 800 KB
    status: 'PARSING',
    uploaderId: 'usr-1',
    uploaderName: 'Alex Architect',
    createdAt: '2026-08-02T16:00:00Z',
  },
];

let memoryDocuments = [...initialMockDocuments];

export const documentApi = {
  fetchDocuments: async (): Promise<DocumentItem[]> => {
    try {
      const token = localStorage.getItem('intelliflow_jwt');
      const headers: Record<string, string> = {};
      if (token) headers['Authorization'] = `Bearer ${token}`;

      const res = await fetch('/api/v1/documents', { headers });
      if (res.ok) {
        const data = await res.json();
        const docs = data.data?.content || data.data || data;
        if (Array.isArray(docs)) {
          return docs.map((d: any) => ({
            id: d.id,
            fileName: d.title || d.fileName || 'Untitled',
            fileKey: d.fileKey || '',
            checksum: d.checksumSha256 || d.checksum || '',
            mimeType: d.mimeType || 'application/octet-stream',
            sizeBytes: d.fileSizeBytes || d.sizeBytes || 0,
            status: d.status || 'ACTIVE',
            uploaderId: d.uploaderId || d.uploader?.id || '',
            uploaderName: d.uploaderName || (d.uploader ? `${d.uploader.firstName} ${d.uploader.lastName}` : 'System User'),
            departmentId: d.departmentId || '',
            createdAt: d.createdAt || new Date().toISOString(),
            vectorDimensions: 384,
            embeddingModel: 'sentence-transformers/all-MiniLM-L6-v2',
            chunks: d.chunks || [],
          }));
        }
      }
    } catch {
      // Fallback to local memory mock
    }
    return new Promise((resolve) => setTimeout(() => resolve([...memoryDocuments]), 200));
  },

  uploadDocument: async (file: File): Promise<DocumentItem> => {
    try {
      const token = localStorage.getItem('intelliflow_jwt');
      const formData = new FormData();
      formData.append('file', file);
      const metadataBlob = new Blob([JSON.stringify({ title: file.name, description: 'Uploaded via Web UI' })], { type: 'application/json' });
      formData.append('data', metadataBlob);

      const headers: Record<string, string> = {};
      if (token) headers['Authorization'] = `Bearer ${token}`;

      const res = await fetch('/api/v1/documents', {
        method: 'POST',
        headers,
        body: formData,
      });
      if (res.ok) {
        const data = await res.json();
        const d = data.data || data;
        const uploaded: DocumentItem = {
          id: d.id,
          fileName: d.title || d.fileName || file.name,
          fileKey: d.fileKey || '',
          checksum: d.checksumSha256 || d.checksum || '',
          mimeType: d.mimeType || file.type || 'application/octet-stream',
          sizeBytes: d.fileSizeBytes || d.sizeBytes || file.size,
          status: d.status || 'ACTIVE',
          uploaderId: d.uploaderId || d.uploader?.id || '',
          uploaderName: d.uploaderName || 'System User',
          createdAt: d.createdAt || new Date().toISOString(),
          vectorDimensions: 384,
          embeddingModel: 'sentence-transformers/all-MiniLM-L6-v2',
          chunks: d.chunks || [],
        };
        memoryDocuments = [uploaded, ...memoryDocuments];
        return uploaded;
      }
    } catch {
      // Fallback to local state update
    }

    const newDoc: DocumentItem = {
      id: crypto.randomUUID(),
      fileName: file.name,
      fileKey: `docs/${crypto.randomUUID()}_${file.name}`,
      checksum: 'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855',
      mimeType: file.type || 'text/plain',
      sizeBytes: file.size,
      status: 'ACTIVE',
      uploaderId: 'usr-1',
      uploaderName: 'Alex Architect',
      createdAt: new Date().toISOString(),
      vectorDimensions: 384,
      embeddingModel: 'sentence-transformers/all-MiniLM-L6-v2',
      chunks: [
        { id: 'chk-new-1', chunkIndex: 1, content: `Extracted content preview from uploaded file: ${file.name}. Apache Tika text parser completed extraction.`, tokenCount: 420 },
      ],
    };
    memoryDocuments = [newDoc, ...memoryDocuments];
    return newDoc;
  },

  deleteDocument: async (id: string): Promise<void> => {
    try {
      const token = localStorage.getItem('intelliflow_jwt');
      const headers: Record<string, string> = {};
      if (token) headers['Authorization'] = `Bearer ${token}`;

      await fetch(`/api/v1/documents/${id}`, {
        method: 'DELETE',
        headers,
      });
    } catch {
      // Fallback
    }
    memoryDocuments = memoryDocuments.filter((d) => d.id !== id);
  },
};
