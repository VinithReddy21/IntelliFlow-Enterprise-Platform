import React from 'react';
import { DocumentItem } from '../types/document';
import { ProcessingStatusBadge } from './ProcessingStatusBadge';
import { FileText, FileCode, File, Trash2, Cpu } from 'lucide-react';

interface Props {
  document: DocumentItem;
  onSelect: (doc: DocumentItem) => void;
  onDelete: (id: string) => void;
}

export const DocumentCard: React.FC<Props> = ({ document, onSelect, onDelete }) => {
  const getFileIcon = (mime: string) => {
    if (mime.includes('pdf')) return <FileText className="w-6 h-6 text-red-400" />;
    if (mime.includes('json') || mime.includes('markdown')) return <FileCode className="w-6 h-6 text-amber-400" />;
    return <File className="w-6 h-6 text-blue-400" />;
  };

  return (
    <div
      onClick={() => onSelect(document)}
      className="p-5 rounded-2xl glass-card border border-white/10 hover:border-amber-500/40 transition-all cursor-pointer group flex flex-col justify-between space-y-4 shadow-sm hover:shadow-gold-glow"
    >
      <div>
        <div className="flex items-start justify-between gap-2 mb-3">
          <div className="p-2.5 rounded-xl bg-white/5 border border-white/10 group-hover:border-amber-500/30 transition-colors">
            {getFileIcon(document.mimeType)}
          </div>
          <button
            onClick={(e) => {
              e.stopPropagation();
              onDelete(document.id);
            }}
            className="p-1.5 rounded-lg text-zinc-500 hover:text-red-400 hover:bg-red-500/10 transition-colors opacity-0 group-hover:opacity-100"
            title="Delete Document"
          >
            <Trash2 className="w-4 h-4" />
          </button>
        </div>

        <h4 className="text-xs font-bold text-zinc-100 group-hover:text-amber-300 transition-colors truncate">
          {document.fileName}
        </h4>
        <p className="text-[10px] text-zinc-500 font-mono mt-1 truncate">
          {(document.sizeBytes / 1024 / 1024).toFixed(2)} MB • {document.mimeType}
        </p>
      </div>

      <div className="pt-3 border-t border-white/5 flex items-center justify-between">
        <ProcessingStatusBadge status={document.status} />

        {document.vectorDimensions && (
          <span className="flex items-center space-x-1 text-[10px] font-mono text-amber-400">
            <Cpu className="w-3 h-3" />
            <span>1536d</span>
          </span>
        )}
      </div>
    </div>
  );
};
