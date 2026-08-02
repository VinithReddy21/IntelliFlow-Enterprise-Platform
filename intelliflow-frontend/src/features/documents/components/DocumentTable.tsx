import React from 'react';
import { DocumentItem } from '../types/document';
import { ProcessingStatusBadge } from './ProcessingStatusBadge';
import { FileText, Download, Trash2, Cpu } from 'lucide-react';

interface Props {
  documents: DocumentItem[];
  onSelect: (doc: DocumentItem) => void;
  onDelete: (id: string) => void;
}

export const DocumentTable: React.FC<Props> = ({ documents, onSelect, onDelete }) => {
  return (
    <div className="rounded-2xl glass-card border border-white/10 overflow-hidden shadow-2xl">
      <div className="overflow-x-auto">
        <table className="w-full text-left border-collapse">
          <thead>
            <tr className="border-b border-white/10 bg-zinc-900/80 text-[11px] font-semibold text-zinc-400 uppercase tracking-wider">
              <th className="p-4">Document Name</th>
              <th className="p-4">Status</th>
              <th className="p-4">Dimensions</th>
              <th className="p-4">File Size</th>
              <th className="p-4">Uploaded At</th>
              <th className="p-4 text-right">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-white/5 text-xs text-zinc-300">
            {documents.map((doc) => (
              <tr
                key={doc.id}
                onClick={() => onSelect(doc)}
                className="hover:bg-amber-500/5 transition-colors cursor-pointer group"
              >
                <td className="p-4 max-w-xs">
                  <div className="flex items-center space-x-3">
                    <FileText className="w-4 h-4 text-amber-400 flex-shrink-0" />
                    <div>
                      <div className="font-bold text-zinc-100 group-hover:text-amber-300 transition-colors truncate">
                        {doc.fileName}
                      </div>
                      <div className="text-[10px] text-zinc-500 font-mono truncate">{doc.mimeType}</div>
                    </div>
                  </div>
                </td>
                <td className="p-4">
                  <ProcessingStatusBadge status={doc.status} />
                </td>
                <td className="p-4 font-mono text-[11px]">
                  {doc.vectorDimensions ? (
                    <span className="inline-flex items-center space-x-1 text-amber-400">
                      <Cpu className="w-3 h-3" />
                      <span>{doc.vectorDimensions} dims</span>
                    </span>
                  ) : (
                    <span className="text-zinc-600">N/A</span>
                  )}
                </td>
                <td className="p-4 font-mono text-zinc-400">
                  {(doc.sizeBytes / 1024 / 1024).toFixed(2)} MB
                </td>
                <td className="p-4 font-mono text-[11px] text-zinc-500">
                  {new Date(doc.createdAt).toLocaleDateString()}
                </td>
                <td className="p-4 text-right" onClick={(e) => e.stopPropagation()}>
                  <div className="flex items-center justify-end space-x-2">
                    <button
                      className="p-1.5 rounded-lg text-zinc-400 hover:text-amber-400 hover:bg-white/5 transition-colors"
                      title="Download"
                    >
                      <Download className="w-3.5 h-3.5" />
                    </button>
                    <button
                      onClick={() => onDelete(doc.id)}
                      className="p-1.5 rounded-lg text-zinc-400 hover:text-red-400 hover:bg-red-500/10 transition-colors"
                      title="Delete"
                    >
                      <Trash2 className="w-3.5 h-3.5" />
                    </button>
                  </div>
                </td>
              </tr>
            ))}

            {documents.length === 0 && (
              <tr>
                <td colSpan={6} className="p-8 text-center text-xs text-zinc-500 font-mono">
                  No documents found in vault.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};
