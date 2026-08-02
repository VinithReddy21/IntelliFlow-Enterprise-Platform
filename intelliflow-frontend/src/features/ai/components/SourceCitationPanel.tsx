import React from 'react';
import { SourceCitation } from '../types/ai';
import { FileText, ExternalLink, Hash } from 'lucide-react';

interface Props {
  citations?: SourceCitation[];
}

export const SourceCitationPanel: React.FC<Props> = ({ citations }) => {
  if (!citations || citations.length === 0) return null;

  return (
    <div className="space-y-3 pt-3 border-t border-white/10">
      <div className="flex items-center space-x-2 text-[11px] font-bold text-amber-400">
        <FileText className="w-3.5 h-3.5" />
        <span>Grounded Knowledge Base Citations ({citations.length})</span>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
        {citations.map((cit) => (
          <div
            key={cit.id}
            className="p-3 rounded-xl glass-card border border-white/10 hover:border-amber-500/40 transition-colors space-y-2 group"
          >
            <div className="flex items-center justify-between text-[11px]">
              <span className="font-bold text-zinc-200 group-hover:text-amber-300 transition-colors truncate max-w-[180px]">
                {cit.documentTitle}
              </span>
              <span className="px-2 py-0.5 rounded-full bg-amber-500/10 text-amber-400 border border-amber-500/20 text-[10px] font-mono font-bold">
                {Math.round(cit.similarityScore * 100)}% match
              </span>
            </div>

            <p className="text-[11px] text-zinc-400 leading-relaxed line-clamp-2 font-mono bg-zinc-950/60 p-2 rounded-lg border border-white/5">
              "{cit.contentSnippet}"
            </p>

            <div className="flex items-center justify-between text-[10px] text-zinc-500 pt-1">
              <span className="flex items-center gap-1 font-mono">
                <Hash className="w-3 h-3 text-amber-400" /> Chunk #{cit.chunkIndex}
              </span>
              <span className="flex items-center gap-1 text-amber-400 hover:underline cursor-pointer">
                View Source <ExternalLink className="w-2.5 h-2.5" />
              </span>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
