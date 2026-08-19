import React from 'react';
import { Zap } from 'lucide-react';

interface Props {
  confidenceScore?: number;
  latencyMs?: number;
  citationCount?: number;
  modelName?: string;
}

export const AiConfidenceMeter: React.FC<Props> = ({
  confidenceScore = 0.96,
  latencyMs = 18,
  citationCount = 2,
  modelName = 'llama-3.3-70b-versatile',
}) => {
  const percentage = Math.round(confidenceScore * 100);

  return (
    <div className="p-4 rounded-xl glass-card-gold border border-amber-500/30 space-y-3">
      <div className="flex items-center justify-between">
        <div className="flex items-center space-x-2 text-amber-400 font-bold text-xs">
          <Zap className="w-4 h-4" />
          <span>Grounded RAG Confidence Telemetry</span>
        </div>
        <span className="px-2.5 py-0.5 rounded-full bg-emerald-500/10 text-emerald-400 border border-emerald-500/30 text-[10px] font-mono font-bold">
          HIGH GROUNDING
        </span>
      </div>

      <div className="space-y-1">
        <div className="flex justify-between text-xs text-zinc-300">
          <span>Retrieval Relevance Score</span>
          <span className="font-mono font-bold text-amber-400">{percentage}%</span>
        </div>
        <div className="w-full h-2 bg-zinc-800 rounded-full overflow-hidden">
          <div
            className="h-full gold-gradient-bg rounded-full transition-all duration-500"
            style={{ width: `${percentage}%` }}
          ></div>
        </div>
      </div>

      <div className="grid grid-cols-3 gap-2 text-[10px] font-mono text-zinc-400 pt-1">
        <div className="p-2 rounded-lg bg-zinc-950/60 border border-white/5 text-center">
          <span className="text-zinc-500 block text-[9px]">Model</span>
          <span className="font-bold text-zinc-200 truncate block max-w-[120px]" title={modelName}>{modelName}</span>
        </div>
        <div className="p-2 rounded-lg bg-zinc-950/60 border border-white/5 text-center">
          <span className="text-zinc-500 block text-[9px]">Latency</span>
          <span className="font-bold text-amber-400">{latencyMs} ms</span>
        </div>
        <div className="p-2 rounded-lg bg-zinc-950/60 border border-white/5 text-center">
          <span className="text-zinc-500 block text-[9px]">Sources</span>
          <span className="font-bold text-zinc-200">{citationCount} Chunks</span>
        </div>
      </div>
    </div>
  );
};
