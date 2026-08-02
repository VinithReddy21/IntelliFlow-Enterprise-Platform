import React from 'react';
import { DocumentItem } from '../types/document';
import { Cpu, ShieldCheck } from 'lucide-react';

interface Props {
  document: DocumentItem;
}

export const VectorInfoPanel: React.FC<Props> = ({ document }) => {
  return (
    <div className="space-y-4">
      <div className="p-4 rounded-xl glass-card-gold border border-amber-500/30 space-y-3">
        <div className="flex items-center space-x-2 text-amber-400 font-bold text-xs">
          <Cpu className="w-4 h-4" />
          <span>pgvector HNSW Engine Metadata</span>
        </div>

        <div className="grid grid-cols-2 gap-3 text-xs">
          <div className="p-3 rounded-lg bg-zinc-950/60 border border-white/5">
            <span className="text-zinc-500 text-[10px] block mb-0.5">Embedding Model</span>
            <span className="font-mono font-bold text-zinc-200">{document.embeddingModel || 'text-embedding-3-small'}</span>
          </div>

          <div className="p-3 rounded-lg bg-zinc-950/60 border border-white/5">
            <span className="text-zinc-500 text-[10px] block mb-0.5">Vector Dimensions</span>
            <span className="font-mono font-bold text-amber-400">{document.vectorDimensions || 1536} Vector Ops</span>
          </div>

          <div className="p-3 rounded-lg bg-zinc-950/60 border border-white/5">
            <span className="text-zinc-500 text-[10px] block mb-0.5">Index Operator</span>
            <span className="font-mono font-bold text-zinc-200">vector_cosine_ops</span>
          </div>

          <div className="p-3 rounded-lg bg-zinc-950/60 border border-white/5">
            <span className="text-zinc-500 text-[10px] block mb-0.5">HNSW Params</span>
            <span className="font-mono font-bold text-zinc-200">m=16, ef_construction=64</span>
          </div>
        </div>
      </div>

      <div className="p-4 rounded-xl glass-card border border-white/10 space-y-2">
        <div className="flex items-center space-x-2 text-zinc-300 font-bold text-xs">
          <ShieldCheck className="w-4 h-4 text-emerald-400" />
          <span>Cryptographic Integrity & SHA-256</span>
        </div>
        <div className="p-3 rounded-lg bg-zinc-950/80 border border-white/5 font-mono text-[10px] text-zinc-400 break-all">
          {document.checksum}
        </div>
      </div>
    </div>
  );
};
