import React from 'react';
import { VectorSearchParams } from '../types/ai';
import { Sliders, Database } from 'lucide-react';

interface Props {
  params: VectorSearchParams;
  onChange: (params: VectorSearchParams) => void;
}

export const VectorSearchFilterPanel: React.FC<Props> = ({ params, onChange }) => {
  return (
    <div className="p-4 rounded-2xl glass-card border border-white/10 space-y-4">
      <div className="flex items-center space-x-2 text-xs font-bold text-zinc-200 border-b border-white/10 pb-3">
        <Sliders className="w-4 h-4 text-amber-400" />
        <span>Vector Search & Cosine Similarity Filters</span>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 text-xs">
        {/* Similarity Threshold Slider */}
        <div className="space-y-1.5">
          <div className="flex justify-between text-zinc-300">
            <span>Similarity Threshold</span>
            <span className="font-mono font-bold text-amber-400">{(params.similarityThreshold * 100).toFixed(0)}%</span>
          </div>
          <input
            type="range"
            min="0.5"
            max="0.99"
            step="0.01"
            value={params.similarityThreshold}
            onChange={(e) => onChange({ ...params, similarityThreshold: parseFloat(e.target.value) })}
            className="w-full accent-amber-400 cursor-pointer"
          />
        </div>

        {/* Top-K Results Selector */}
        <div className="space-y-1.5">
          <label className="block text-zinc-300">Top-K Result Limit</label>
          <select
            value={params.topK}
            onChange={(e) => onChange({ ...params, topK: parseInt(e.target.value, 10) })}
            className="w-full px-3 py-1.5 bg-zinc-900 border border-white/10 rounded-xl text-zinc-200 focus:outline-none focus:border-amber-500/60 font-mono"
          >
            {[3, 5, 10, 15, 20].map((k) => (
              <option key={k} value={k}>
                Top {k} Chunks
              </option>
            ))}
          </select>
        </div>

        {/* Department Scope */}
        <div className="space-y-1.5">
          <label className="block text-zinc-300 flex items-center gap-1">
            <Database className="w-3 h-3 text-amber-400" /> ABAC Department Scope
          </label>
          <select
            value={params.departmentId || 'ALL'}
            onChange={(e) => onChange({ ...params, departmentId: e.target.value === 'ALL' ? undefined : e.target.value })}
            className="w-full px-3 py-1.5 bg-zinc-900 border border-white/10 rounded-xl text-zinc-200 focus:outline-none focus:border-amber-500/60"
          >
            <option value="ALL">All Departments (Global)</option>
            <option value="dept-ai">AI & Data Engineering</option>
            <option value="dept-sec">Cybersecurity & SecOps</option>
            <option value="dept-core">Backend Core Platform</option>
          </select>
        </div>
      </div>
    </div>
  );
};
