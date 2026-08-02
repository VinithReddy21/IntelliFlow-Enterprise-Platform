export type DocumentStatus = 
  | 'UPLOADED' 
  | 'PARSING' 
  | 'CHUNKED' 
  | 'EMBEDDED' 
  | 'ACTIVE' 
  | 'FAILED';

export interface DocumentChunk {
  id: string;
  chunkIndex: number;
  content: string;
  tokenCount: number;
  embeddingDimensions?: number;
}

export interface DocumentItem {
  id: string;
  fileName: string;
  fileKey: string;
  checksum: string;
  mimeType: string;
  sizeBytes: number;
  status: DocumentStatus;
  uploaderId: string;
  uploaderName?: string;
  departmentId?: string;
  createdAt: string;
  updatedAt?: string;
  chunks?: DocumentChunk[];
  vectorDimensions?: number;
  embeddingModel?: string;
}

export interface UploadQueueItem {
  id: string;
  file: File;
  progress: number;
  status: 'QUEUED' | 'UPLOADING' | 'COMPLETED' | 'ERROR';
  error?: string;
}

export interface DocumentFilterState {
  searchQuery: string;
  status?: DocumentStatus | 'ALL';
  mimeType?: string | 'ALL';
}
