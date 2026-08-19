import React, { useState } from 'react';
import { UploadCloud, FileText } from 'lucide-react';

interface Props {
  onFilesSelected: (files: File[]) => void;
}

export const UploadZone: React.FC<Props> = ({ onFilesSelected }) => {
  const [isDragOver, setIsDragOver] = useState(false);

  const handleDragOver = (e: React.DragEvent) => {
    e.preventDefault();
    setIsDragOver(true);
  };

  const handleDragLeave = () => {
    setIsDragOver(false);
  };

  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault();
    setIsDragOver(false);
    if (e.dataTransfer.files && e.dataTransfer.files.length > 0) {
      onFilesSelected(Array.from(e.dataTransfer.files));
    }
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files.length > 0) {
      onFilesSelected(Array.from(e.target.files));
    }
  };

  return (
    <div
      onDragOver={handleDragOver}
      onDragLeave={handleDragLeave}
      onDrop={handleDrop}
      className={`p-8 border-2 border-dashed rounded-2xl transition-all text-center cursor-pointer relative overflow-hidden group ${
        isDragOver
          ? 'border-amber-400 bg-amber-500/10 shadow-gold-glow'
          : 'border-white/10 glass-card hover:border-amber-500/40'
      }`}
    >
      <input
        type="file"
        multiple
        onChange={handleFileChange}
        className="absolute inset-0 opacity-0 cursor-pointer z-10"
      />
      <div className="w-12 h-12 rounded-2xl gold-gradient-bg mx-auto flex items-center justify-center text-zinc-950 shadow-gold-glow mb-4 group-hover:scale-110 transition-transform">
        <UploadCloud className="w-6 h-6 stroke-[2.5]" />
      </div>

      <h3 className="text-sm font-bold text-zinc-100">
        Drag & Drop files here or <span className="text-amber-400 underline">browse</span>
      </h3>
      <p className="text-xs text-zinc-400 mt-1">
        Supports PDF, DOCX, TXT, Markdown, and JSON up to 50 MB per file.
      </p>

      <div className="mt-4 inline-flex items-center space-x-2 text-[10px] text-zinc-500 font-mono">
        <FileText className="w-3 h-3 text-amber-400" />
        <span>Automated Apache Tika Text Extraction & 384-dim Embedding</span>
      </div>
    </div>
  );
};
