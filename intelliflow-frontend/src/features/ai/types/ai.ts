export interface SourceCitation {
  id: string;
  documentId: string;
  documentTitle: string;
  chunkIndex: number;
  similarityScore: number; // e.g. 0.94
  contentSnippet: string;
  mimeType?: string;
}

export interface ChatMessage {
  id: string;
  sender: 'user' | 'assistant';
  content: string;
  timestamp: string;
  citations?: SourceCitation[];
  confidenceScore?: number; // e.g. 0.96
  latencyMs?: number;
  isStreaming?: boolean;
}

export interface Conversation {
  id: string;
  title: string;
  createdAt: string;
  updatedAt: string;
  isPinned?: boolean;
  messages: ChatMessage[];
}

export interface VectorSearchParams {
  query: string;
  similarityThreshold: number; // 0.0 to 1.0
  topK: number; // 1 to 20
  departmentId?: string;
}

export interface VectorSearchResultItem {
  id: string;
  documentId: string;
  documentTitle: string;
  chunkIndex: number;
  similarityScore: number;
  content: string;
  tokenCount: number;
  mimeType: string;
}
