import React from 'react';
import { DocumentStatus } from '../types/document';
import { CheckCircle2, Circle, Sparkles } from 'lucide-react';

interface Props {
  status: DocumentStatus;
}

export const ProcessingTimeline: React.FC<Props> = ({ status }) => {
  const steps = [
    { title: 'Upload', desc: 'Stored via FileStorageService' },
    { title: 'Virus Scan', desc: 'Passed security verification' },
    { title: 'Apache Tika Parsing', desc: 'Extracted structured text' },
    { title: 'Text Extraction', desc: 'Cleaned metadata & encoding' },
    { title: 'Chunking', desc: '500-token sliding window' },
    { title: 'Embedding Generation', desc: '1536-dim OpenAI embedding' },
    { title: 'pgvector Storage', desc: 'HNSW index cosine ops' },
    { title: 'Ready for AI Search', desc: 'Grounded RAG retrieval active' },
  ];

  const getCompletedCount = (st: DocumentStatus) => {
    switch (st) {
      case 'UPLOADED': return 2;
      case 'PARSING': return 4;
      case 'CHUNKED': return 5;
      case 'EMBEDDED': return 7;
      case 'ACTIVE': return 8;
      default: return 8;
    }
  };

  const completedStepIndex = getCompletedCount(status);

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between text-xs font-semibold">
        <span className="text-zinc-300 flex items-center gap-1.5">
          <Sparkles className="w-4 h-4 text-amber-400" />
          <span>AI Pipeline Telemetry</span>
        </span>
        <span className="text-amber-400 font-mono font-bold">
          {completedStepIndex} / 8 Steps Complete
        </span>
      </div>

      <div className="relative pl-6 space-y-4 border-l border-white/10 ml-2">
        {steps.map((step, idx) => {
          const isDone = idx < completedStepIndex;

          return (
            <div key={idx} className="relative group">
              <div
                className={`absolute -left-[31px] top-0.5 w-5 h-5 rounded-full flex items-center justify-center transition-colors ${
                  isDone
                    ? 'bg-amber-400 text-zinc-950 shadow-gold-sm'
                    : 'bg-zinc-900 border border-white/20 text-zinc-600'
                }`}
              >
                {isDone ? <CheckCircle2 className="w-3.5 h-3.5 stroke-[2.5]" /> : <Circle className="w-3 h-3" />}
              </div>

              <div className="flex flex-col">
                <span className={`text-xs font-bold ${isDone ? 'text-zinc-100' : 'text-zinc-500'}`}>
                  Step {idx + 1}: {step.title}
                </span>
                <span className="text-[11px] text-zinc-400">{step.desc}</span>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};
