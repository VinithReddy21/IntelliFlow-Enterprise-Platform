import React, { useState } from 'react';
import { DocumentItem } from '../types/document';
import { ProcessingStatusBadge } from './ProcessingStatusBadge';
import { ProcessingTimeline } from './ProcessingTimeline';
import { ChunkViewer } from './ChunkViewer';
import { VectorInfoPanel } from './VectorInfoPanel';
import { X, FileText, Layers, Cpu, Activity, User, Calendar, HardDrive } from 'lucide-react';

interface Props {
  document: DocumentItem | null;
  onClose: () => void;
}

export const DocumentDetailsDrawer: React.FC<Props> = ({ document, onClose }) => {
  const [activeTab, setActiveTab] = useState<'metadata' | 'timeline' | 'chunks' | 'vector'>('metadata');

  if (!document) return null;

  return (
    <div className="fixed inset-0 z-50 overflow-hidden flex justify-end bg-black/60 backdrop-blur-sm animate-in fade-in">
      <div className="w-full max-w-xl h-full glass-card border-l border-white/10 shadow-2xl flex flex-col justify-between animate-in slide-in-from-right duration-300">
        {/* Drawer Header */}
        <div className="p-6 border-b border-white/10 flex items-start justify-between bg-zinc-900/60">
          <div className="flex items-start space-x-3">
            <div className="p-3 rounded-xl bg-amber-500/10 border border-amber-500/30 text-amber-400">
              <FileText className="w-6 h-6" />
            </div>
            <div>
              <h2 className="text-base font-bold text-zinc-100 line-clamp-1">{document.fileName}</h2>
              <div className="mt-1 flex items-center space-x-2">
                <ProcessingStatusBadge status={document.status} />
              </div>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-2 rounded-xl text-zinc-400 hover:text-zinc-100 hover:bg-white/10 transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Tab Navigation */}
        <div className="flex border-b border-white/10 px-6 bg-zinc-950/40 text-xs font-semibold text-zinc-400 overflow-x-auto">
          <button
            onClick={() => setActiveTab('metadata')}
            className={`py-3 px-4 border-b-2 transition-colors whitespace-nowrap flex items-center space-x-1.5 ${
              activeTab === 'metadata' ? 'border-amber-400 text-amber-400' : 'border-transparent hover:text-zinc-200'
            }`}
          >
            <FileText className="w-3.5 h-3.5" />
            <span>Metadata</span>
          </button>
          <button
            onClick={() => setActiveTab('timeline')}
            className={`py-3 px-4 border-b-2 transition-colors whitespace-nowrap flex items-center space-x-1.5 ${
              activeTab === 'timeline' ? 'border-amber-400 text-amber-400' : 'border-transparent hover:text-zinc-200'
            }`}
          >
            <Activity className="w-3.5 h-3.5" />
            <span>AI Pipeline</span>
          </button>
          <button
            onClick={() => setActiveTab('chunks')}
            className={`py-3 px-4 border-b-2 transition-colors whitespace-nowrap flex items-center space-x-1.5 ${
              activeTab === 'chunks' ? 'border-amber-400 text-amber-400' : 'border-transparent hover:text-zinc-200'
            }`}
          >
            <Layers className="w-3.5 h-3.5" />
            <span>Chunks ({document.chunks?.length || 0})</span>
          </button>
          <button
            onClick={() => setActiveTab('vector')}
            className={`py-3 px-4 border-b-2 transition-colors whitespace-nowrap flex items-center space-x-1.5 ${
              activeTab === 'vector' ? 'border-amber-400 text-amber-400' : 'border-transparent hover:text-zinc-200'
            }`}
          >
            <Cpu className="w-3.5 h-3.5" />
            <span>Vector Info</span>
          </button>
        </div>

        {/* Tab Body */}
        <div className="flex-1 p-6 overflow-y-auto space-y-6 text-xs text-zinc-300">
          {activeTab === 'metadata' && (
            <div className="space-y-4">
              <div className="grid grid-cols-2 gap-4 p-4 rounded-xl bg-zinc-900/60 border border-white/5">
                <div>
                  <span className="text-zinc-500 text-[10px] block mb-0.5 flex items-center gap-1">
                    <User className="w-3 h-3" /> Uploader
                  </span>
                  <span className="font-semibold text-zinc-200">{document.uploaderName || 'Alex Architect'}</span>
                </div>
                <div>
                  <span className="text-zinc-500 text-[10px] block mb-0.5 flex items-center gap-1">
                    <HardDrive className="w-3 h-3" /> File Size
                  </span>
                  <span className="font-mono text-zinc-200">{(document.sizeBytes / 1024 / 1024).toFixed(2)} MB</span>
                </div>
                <div>
                  <span className="text-zinc-500 text-[10px] block mb-0.5 flex items-center gap-1">
                    <Calendar className="w-3 h-3" /> Ingestion Date
                  </span>
                  <span className="font-mono text-zinc-200">{new Date(document.createdAt).toLocaleString()}</span>
                </div>
                <div>
                  <span className="text-zinc-500 text-[10px] block mb-0.5 flex items-center gap-1">
                    <FileText className="w-3 h-3" /> MIME Type
                  </span>
                  <span className="font-mono text-amber-400">{document.mimeType}</span>
                </div>
              </div>

              <div className="p-4 rounded-xl bg-zinc-900/40 border border-white/5 space-y-1">
                <span className="text-zinc-500 text-[10px] block">Object Storage FileKey</span>
                <span className="font-mono text-[11px] text-zinc-300 break-all">{document.fileKey}</span>
              </div>
            </div>
          )}

          {activeTab === 'timeline' && <ProcessingTimeline status={document.status} />}
          {activeTab === 'chunks' && <ChunkViewer chunks={document.chunks} />}
          {activeTab === 'vector' && <VectorInfoPanel document={document} />}
        </div>
      </div>
    </div>
  );
};
