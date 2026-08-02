import React from 'react';
import { DocumentStatus } from '../types/document';

interface Props {
  status: DocumentStatus;
}

export const ProcessingStatusBadge: React.FC<Props> = ({ status }) => {
  const config: Record<DocumentStatus, { label: string; className: string }> = {
    UPLOADED: { label: 'Uploaded', className: 'bg-zinc-800 text-zinc-400 border-zinc-700' },
    PARSING: { label: 'Apache Tika Parsing', className: 'bg-blue-500/10 text-blue-400 border-blue-500/30 animate-pulse' },
    CHUNKED: { label: 'Text Chunked', className: 'bg-purple-500/10 text-purple-400 border-purple-500/30' },
    EMBEDDED: { label: '1536 Vector Embedded', className: 'bg-amber-500/10 text-amber-400 border-amber-500/30' },
    ACTIVE: { label: 'Active RAG Ready', className: 'bg-emerald-500/10 text-emerald-400 border-emerald-500/30 shadow-gold-sm' },
    FAILED: { label: 'Processing Failed', className: 'bg-red-500/15 text-red-400 border-red-500/40' },
  };

  const item = config[status] || config.ACTIVE;

  return (
    <span className={`px-2.5 py-0.5 rounded-full text-[10px] font-bold border transition-colors ${item.className}`}>
      {item.label}
    </span>
  );
};
