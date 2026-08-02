import React from 'react';
import { UploadQueueItem } from '../types/document';
import { FileText, CheckCircle2, RotateCcw, X } from 'lucide-react';

interface Props {
  queue: UploadQueueItem[];
  onCancel: (id: string) => void;
  onRetry: (id: string) => void;
}

export const UploadQueue: React.FC<Props> = ({ queue, onCancel, onRetry }) => {
  if (queue.length === 0) return null;

  return (
    <div className="space-y-3 p-4 rounded-2xl glass-card border border-white/10">
      <h4 className="text-xs font-bold text-zinc-200">Upload Processing Queue ({queue.length})</h4>

      <div className="space-y-2 max-h-48 overflow-y-auto pr-1">
        {queue.map((item) => (
          <div key={item.id} className="p-3 rounded-xl bg-zinc-900/80 border border-white/5 space-y-2">
            <div className="flex items-center justify-between text-xs">
              <div className="flex items-center space-x-2 truncate pr-2">
                <FileText className="w-4 h-4 text-amber-400 flex-shrink-0" />
                <span className="font-semibold text-zinc-200 truncate">{item.file.name}</span>
                <span className="text-[10px] text-zinc-500 font-mono">
                  ({(item.file.size / 1024 / 1024).toFixed(2)} MB)
                </span>
              </div>

              <div className="flex items-center space-x-2">
                {item.status === 'COMPLETED' && <CheckCircle2 className="w-4 h-4 text-emerald-400" />}
                {item.status === 'ERROR' && (
                  <button onClick={() => onRetry(item.id)} className="text-red-400 hover:text-red-300">
                    <RotateCcw className="w-4 h-4" />
                  </button>
                )}
                {item.status !== 'COMPLETED' && (
                  <button onClick={() => onCancel(item.id)} className="text-zinc-500 hover:text-zinc-300">
                    <X className="w-4 h-4" />
                  </button>
                )}
              </div>
            </div>

            {item.status === 'UPLOADING' && (
              <div className="w-full h-1.5 bg-zinc-800 rounded-full overflow-hidden">
                <div
                  className="h-full bg-amber-400 transition-all duration-300"
                  style={{ width: `${item.progress}%` }}
                ></div>
              </div>
            )}
          </div>
        ))}
      </div>
    </div>
  );
};
