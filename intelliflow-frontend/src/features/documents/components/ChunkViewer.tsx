import React from 'react';
import { DocumentChunk } from '../types/document';
import { Layers, Hash, Code } from 'lucide-react';

interface Props {
  chunks?: DocumentChunk[];
}

export const ChunkViewer: React.FC<Props> = ({ chunks }) => {
  if (!chunks || chunks.length === 0) {
    return (
      <div className="p-8 border-2 border-dashed border-white/5 rounded-2xl text-center text-xs text-zinc-500 font-mono">
        No chunks generated yet for this document.
      </div>
    );
  }

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between text-xs font-semibold text-zinc-300">
        <div className="flex items-center space-x-2">
          <Layers className="w-4 h-4 text-amber-400" />
          <span>Extracted Semantic Chunks ({chunks.length})</span>
        </div>
        <span className="text-zinc-500 font-mono text-[11px]">Recursive Token Chunking (500 tokens / 50 overlap)</span>
      </div>

      <div className="space-y-3">
        {chunks.map((chk) => (
          <div key={chk.id} className="p-4 rounded-xl glass-card border border-white/10 space-y-2 hover:border-amber-500/30 transition-colors">
            <div className="flex items-center justify-between text-[11px] text-zinc-400 border-b border-white/5 pb-2">
              <span className="flex items-center gap-1 font-bold text-amber-400">
                <Hash className="w-3 h-3" /> Chunk #{chk.chunkIndex}
              </span>
              <span className="flex items-center gap-1 font-mono text-zinc-400">
                <Code className="w-3 h-3" /> {chk.tokenCount} Tokens
              </span>
            </div>
            <p className="text-xs text-zinc-300 leading-relaxed font-mono bg-zinc-950/60 p-3 rounded-lg border border-white/5">
              {chk.content}
            </p>
          </div>
        ))}
      </div>
    </div>
  );
};
