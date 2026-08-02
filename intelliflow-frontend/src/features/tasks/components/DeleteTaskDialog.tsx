import React from 'react';
import { TaskItem } from '../types/task';
import { AlertTriangle } from 'lucide-react';

interface Props {
  task: TaskItem | null;
  isOpen: boolean;
  onClose: () => void;
  onConfirm: (id: string) => void;
}

export const DeleteTaskDialog: React.FC<Props> = ({ task, isOpen, onClose, onConfirm }) => {
  if (!isOpen || !task) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/70 backdrop-blur-sm animate-in fade-in">
      <div className="w-full max-w-md glass-card border border-red-500/30 rounded-2xl shadow-2xl p-6 relative">
        <div className="flex items-center space-x-3 mb-4">
          <div className="w-10 h-10 rounded-xl bg-red-500/20 border border-red-500/40 flex items-center justify-center text-red-400">
            <AlertTriangle className="w-6 h-6" />
          </div>
          <div>
            <h3 className="text-base font-bold text-zinc-100">Delete Workflow Task?</h3>
            <p className="text-xs text-zinc-400">This action will soft-delete the task item from the active database.</p>
          </div>
        </div>

        <div className="p-3 rounded-xl bg-zinc-900/80 border border-white/5 text-xs text-zinc-300 font-semibold mb-6">
          "{task.title}"
        </div>

        <div className="flex items-center justify-end space-x-3 text-xs">
          <button
            onClick={onClose}
            className="px-4 py-2 rounded-xl text-zinc-400 hover:text-zinc-200 hover:bg-white/5 font-semibold"
          >
            Cancel
          </button>
          <button
            onClick={() => {
              onConfirm(task.id);
              onClose();
            }}
            className="px-5 py-2 rounded-xl font-bold bg-red-500 text-white shadow-lg hover:bg-red-600 transition-colors"
          >
            Confirm Delete
          </button>
        </div>
      </div>
    </div>
  );
};
