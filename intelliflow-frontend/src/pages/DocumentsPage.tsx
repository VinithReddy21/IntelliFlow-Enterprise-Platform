import React, { useState } from 'react';
import { useDocuments, useUploadDocument, useDeleteDocument } from '../features/documents/hooks/useDocuments';
import { DocumentItem, UploadQueueItem, DocumentStatus } from '../features/documents/types/document';
import { UploadZone } from '../features/documents/components/UploadZone';
import { UploadQueue } from '../features/documents/components/UploadQueue';
import { DocumentCard } from '../features/documents/components/DocumentCard';
import { DocumentTable } from '../features/documents/components/DocumentTable';
import { DocumentDetailsDrawer } from '../features/documents/components/DocumentDetailsDrawer';
import { LayoutGrid, Table, Search, Sparkles, Filter, Database } from 'lucide-react';

export const DocumentsPage: React.FC = () => {
  const { data: documents = [], isLoading } = useDocuments();
  const uploadMutation = useUploadDocument();
  const deleteMutation = useDeleteDocument();

  const [viewMode, setViewMode] = useState<'grid' | 'table'>('grid');
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState<DocumentStatus | 'ALL'>('ALL');

  const [selectedDoc, setSelectedDoc] = useState<DocumentItem | null>(null);
  const [uploadQueue, setUploadQueue] = useState<UploadQueueItem[]>([]);

  const handleFilesSelected = (files: File[]) => {
    const newItems: UploadQueueItem[] = files.map((file) => ({
      id: crypto.randomUUID(),
      file,
      progress: 0,
      status: 'UPLOADING',
    }));

    setUploadQueue((prev) => [...newItems, ...prev]);

    files.forEach((file) => {
      uploadMutation.mutate(file, {
        onSuccess: () => {
          setUploadQueue((prev) =>
            prev.map((q) => (q.file.name === file.name ? { ...q, status: 'COMPLETED', progress: 100 } : q))
          );
        },
        onError: (err) => {
          setUploadQueue((prev) =>
            prev.map((q) => (q.file.name === file.name ? { ...q, status: 'ERROR', error: String(err) } : q))
          );
        },
      });
    });
  };

  const handleCancelUpload = (id: string) => {
    setUploadQueue((prev) => prev.filter((q) => q.id !== id));
  };

  const handleRetryUpload = (id: string) => {
    const item = uploadQueue.find((q) => q.id === id);
    if (item) {
      uploadMutation.mutate(item.file);
    }
  };

  const filteredDocs = documents.filter((doc) => {
    const matchesSearch = doc.fileName.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesStatus = statusFilter === 'ALL' || doc.status === statusFilter;
    return matchesSearch && matchesStatus;
  });

  return (
    <div className="space-y-6">
      {/* Header & Controls */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <div className="inline-flex items-center space-x-2 text-amber-400 text-xs font-semibold uppercase tracking-wider mb-1">
            <Sparkles className="w-3.5 h-3.5" />
            <span>Document Ingestion Vault</span>
          </div>
          <h1 className="text-2xl font-bold text-zinc-100">Enterprise Vector Document Storage</h1>
        </div>

        {/* View Mode Toggle */}
        <div className="flex p-1 rounded-xl glass-card border border-white/10 text-xs font-medium self-start md:self-auto">
          <button
            onClick={() => setViewMode('grid')}
            className={`flex items-center space-x-1.5 px-3 py-1.5 rounded-lg transition-all ${
              viewMode === 'grid'
                ? 'bg-amber-500/20 text-amber-300 border border-amber-500/30 shadow-gold-sm'
                : 'text-zinc-400 hover:text-zinc-200'
            }`}
          >
            <LayoutGrid className="w-3.5 h-3.5" />
            <span>Grid</span>
          </button>
          <button
            onClick={() => setViewMode('table')}
            className={`flex items-center space-x-1.5 px-3 py-1.5 rounded-lg transition-all ${
              viewMode === 'table'
                ? 'bg-amber-500/20 text-amber-300 border border-amber-500/30 shadow-gold-sm'
                : 'text-zinc-400 hover:text-zinc-200'
            }`}
          >
            <Table className="w-3.5 h-3.5" />
            <span>Table</span>
          </button>
        </div>
      </div>

      {/* Upload Zone & Queue */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2">
          <UploadZone onFilesSelected={handleFilesSelected} />
        </div>
        <div>
          <UploadQueue queue={uploadQueue} onCancel={handleCancelUpload} onRetry={handleRetryUpload} />
          {uploadQueue.length === 0 && (
            <div className="p-6 rounded-2xl glass-card border border-white/10 flex flex-col items-center justify-center text-center h-full">
              <Database className="w-8 h-8 text-amber-400/80 mb-2" />
              <p className="text-xs font-bold text-zinc-200">pgvector HNSW Store Active</p>
              <p className="text-[11px] text-zinc-500 mt-1">Uploaded files will be processed via Apache Tika and chunked into 384-dim vector embeddings.</p>
            </div>
          )}
        </div>
      </div>

      {/* Search & Filters */}
      <div className="p-4 rounded-2xl glass-card border border-white/10 flex flex-col md:flex-row items-center justify-between gap-4">
        <div className="relative w-full md:w-80">
          <Search className="w-4 h-4 text-zinc-400 absolute left-3 top-1/2 -translate-y-1/2" />
          <input
            type="text"
            placeholder="Search document name or checksum..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full pl-9 pr-4 py-2 bg-zinc-900/80 border border-white/10 rounded-xl text-xs text-zinc-200 placeholder-zinc-500 focus:outline-none focus:border-amber-500/60"
          />
        </div>

        <div className="flex items-center space-x-3 text-xs w-full md:w-auto">
          <span className="text-zinc-400 flex items-center gap-1">
            <Filter className="w-3.5 h-3.5" /> Status Filter:
          </span>
          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value as any)}
            className="px-3 py-2 bg-zinc-900 border border-white/10 rounded-xl text-zinc-300 focus:outline-none focus:border-amber-500/60"
          >
            <option value="ALL">All Statuses</option>
            <option value="ACTIVE">Active RAG Ready</option>
            <option value="EMBEDDED">384 Vector Embedded</option>
            <option value="CHUNKED">Text Chunked</option>
            <option value="PARSING">Apache Tika Parsing</option>
            <option value="UPLOADED">Uploaded</option>
          </select>
        </div>
      </div>

      {/* Main View Area */}
      {isLoading ? (
        <div className="h-64 flex items-center justify-center">
          <div className="w-10 h-10 border-4 border-amber-400 border-t-transparent rounded-full animate-spin"></div>
        </div>
      ) : viewMode === 'grid' ? (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
          {filteredDocs.map((doc) => (
            <DocumentCard
              key={doc.id}
              document={doc}
              onSelect={setSelectedDoc}
              onDelete={(id) => deleteMutation.mutate(id)}
            />
          ))}
        </div>
      ) : (
        <DocumentTable
          documents={filteredDocs}
          onSelect={setSelectedDoc}
          onDelete={(id) => deleteMutation.mutate(id)}
        />
      )}

      {/* Details Drawer */}
      <DocumentDetailsDrawer document={selectedDoc} onClose={() => setSelectedDoc(null)} />
    </div>
  );
};
