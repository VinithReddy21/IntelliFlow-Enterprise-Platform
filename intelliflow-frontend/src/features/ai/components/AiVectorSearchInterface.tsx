import React, { useState } from 'react';
import { VectorSearchParams, VectorSearchResultItem } from '../types/ai';
import { useVectorSearch } from '../hooks/useAi';
import { VectorSearchFilterPanel } from './VectorSearchFilterPanel';
import { Search, Cpu, Sparkles, Hash, FileText } from 'lucide-react';

export const AiVectorSearchInterface: React.FC = () => {
  const vectorSearchMutation = useVectorSearch();

  const [searchParams, setSearchParams] = useState<VectorSearchParams>({
    query: '384 vector search HNSW pgvector',
    similarityThreshold: 0.8,
    topK: 5,
  });

  const [results, setResults] = useState<VectorSearchResultItem[]>([]);

  const handleExecuteSearch = (e?: React.FormEvent) => {
    if (e) e.preventDefault();
    if (!searchParams.query.trim()) return;

    vectorSearchMutation.mutate(searchParams, {
      onSuccess: (data) => setResults(data),
    });
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col gap-2">
        <div className="inline-flex items-center space-x-2 text-amber-400 text-xs font-semibold uppercase tracking-wider">
          <Sparkles className="w-3.5 h-3.5" />
          <span>Semantic Vector Engine</span>
        </div>
        <h1 className="text-2xl font-bold text-zinc-100">AI Vector Search</h1>
        <p className="text-sm text-zinc-300 font-medium">
          Find relevant enterprise knowledge by meaning, not just exact keywords.
        </p>
        <p className="text-xs text-zinc-400 leading-relaxed bg-zinc-900/80 p-3.5 rounded-xl border border-white/10 mt-1">
          Your query is converted into a 384-dimensional embedding and compared with document embeddings stored in PostgreSQL pgvector. The most semantically relevant document chunks are returned.
        </p>
      </div>

      {/* Query Bar */}
      <form onSubmit={handleExecuteSearch} className="flex gap-3">
        <div className="relative flex-1">
          <Search className="w-4 h-4 text-zinc-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
          <input
            type="text"
            placeholder="Enter natural language query or concept search..."
            value={searchParams.query}
            onChange={(e) => setSearchParams({ ...searchParams, query: e.target.value })}
            className="w-full pl-10 pr-4 py-3 bg-zinc-900 border border-white/10 rounded-xl text-xs text-zinc-100 placeholder-zinc-500 focus:outline-none focus:border-amber-500/60 font-mono"
          />
        </div>
        <button
          type="submit"
          disabled={vectorSearchMutation.isPending}
          className="px-6 py-3 rounded-xl font-bold text-xs gold-gradient-bg text-zinc-950 shadow-gold-sm hover:brightness-110 flex items-center space-x-2"
        >
          <Cpu className="w-4 h-4" />
          <span>{vectorSearchMutation.isPending ? 'Searching...' : 'Vector Search'}</span>
        </button>
      </form>

      {/* Filters Panel */}
      <VectorSearchFilterPanel params={searchParams} onChange={setSearchParams} />

      {/* Results Container */}
      <div className="space-y-4">
        <div className="flex items-center justify-between text-xs font-semibold text-zinc-300">
          <span>Vector Match Candidates ({results.length})</span>
          <span className="font-mono text-[11px] text-zinc-500">HNSW Cosine Distance Ops</span>
        </div>

        <div className="space-y-3">
          {results.map((res) => (
            <div
              key={res.id}
              className="p-5 rounded-2xl glass-card border border-white/10 hover:border-amber-500/40 transition-colors space-y-3"
            >
              <div className="flex items-center justify-between">
                <div className="flex items-center space-x-3">
                  <FileText className="w-4 h-4 text-amber-400" />
                  <span className="font-bold text-xs text-zinc-100">{res.documentTitle}</span>
                </div>

                <div className="flex items-center space-x-2">
                  <span className="px-2.5 py-0.5 rounded-full bg-amber-500/10 text-amber-400 border border-amber-500/30 text-[10px] font-mono font-bold">
                    {Math.round(res.similarityScore * 100)}% Similarity
                  </span>
                  <span className="px-2.5 py-0.5 rounded-full bg-zinc-800 text-zinc-400 border border-zinc-700 text-[10px] font-mono">
                    <Hash className="w-2.5 h-2.5 inline mr-1" /> Chunk #{res.chunkIndex}
                  </span>
                </div>
              </div>

              <p className="text-xs text-zinc-300 leading-relaxed font-mono bg-zinc-950/60 p-4 rounded-xl border border-white/5">
                {res.content}
              </p>

              <div className="flex items-center justify-between text-[10px] text-zinc-500 font-mono pt-1">
                <span>Tokens: {res.tokenCount}</span>
                <span>Document ID: {res.documentId}</span>
              </div>
            </div>
          ))}

          {results.length === 0 && (
            <div className="p-12 border-2 border-dashed border-white/5 rounded-2xl text-center text-xs text-zinc-500 font-mono">
              Execute a vector search query to retrieve grounded chunk matches.
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
