import React from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { CreateTaskInput } from '../types/task';
import { X, Plus, AlertCircle } from 'lucide-react';

const taskSchema = z.object({
  title: z.string().min(3, 'Title must be at least 3 characters').max(100),
  description: z.string().min(5, 'Description must be at least 5 characters'),
  priority: z.enum(['LOW', 'MEDIUM', 'HIGH', 'CRITICAL'] as const),
  assigneeId: z.string().optional(),
  dueDate: z.string().optional(),
});

interface Props {
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (data: CreateTaskInput) => void;
}

export const CreateTaskModal: React.FC<Props> = ({ isOpen, onClose, onSubmit }) => {
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<CreateTaskInput>({
    resolver: zodResolver(taskSchema),
    defaultValues: {
      priority: 'MEDIUM',
    },
  });

  if (!isOpen) return null;

  const handleFormSubmit = (data: CreateTaskInput) => {
    onSubmit(data);
    reset();
    onClose();
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/70 backdrop-blur-sm animate-in fade-in">
      <div className="w-full max-w-lg glass-card border border-white/10 rounded-2xl shadow-2xl p-6 relative">
        <div className="flex items-center justify-between border-b border-white/10 pb-4 mb-5">
          <div className="flex items-center space-x-2">
            <div className="w-8 h-8 rounded-lg gold-gradient-bg flex items-center justify-center text-zinc-950 font-bold">
              <Plus className="w-5 h-5 stroke-[3]" />
            </div>
            <h3 className="text-base font-bold text-zinc-100">Create Workflow Task</h3>
          </div>
          <button onClick={onClose} className="p-1.5 rounded-lg text-zinc-400 hover:text-zinc-100 hover:bg-white/10">
            <X className="w-5 h-5" />
          </button>
        </div>

        <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-4 text-xs">
          <div>
            <label className="block font-semibold text-zinc-300 mb-1">Task Title *</label>
            <input
              type="text"
              placeholder="e.g. Deploy pgvector HNSW Index Optimization"
              {...register('title')}
              className="w-full px-3.5 py-2.5 bg-zinc-900/80 border border-white/10 rounded-xl text-zinc-100 placeholder-zinc-500 focus:outline-none focus:border-amber-500/60"
            />
            {errors.title && (
              <p className="text-red-400 text-[10px] mt-1 flex items-center gap-1">
                <AlertCircle className="w-3 h-3" /> {errors.title.message}
              </p>
            )}
          </div>

          <div>
            <label className="block font-semibold text-zinc-300 mb-1">Description *</label>
            <textarea
              rows={3}
              placeholder="Detailed task instructions and technical scope..."
              {...register('description')}
              className="w-full px-3.5 py-2.5 bg-zinc-900/80 border border-white/10 rounded-xl text-zinc-100 placeholder-zinc-500 focus:outline-none focus:border-amber-500/60"
            />
            {errors.description && (
              <p className="text-red-400 text-[10px] mt-1 flex items-center gap-1">
                <AlertCircle className="w-3 h-3" /> {errors.description.message}
              </p>
            )}
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block font-semibold text-zinc-300 mb-1">Priority Level</label>
              <select
                {...register('priority')}
                className="w-full px-3 py-2.5 bg-zinc-900 border border-white/10 rounded-xl text-zinc-200 focus:outline-none focus:border-amber-500/60"
              >
                <option value="LOW">Low Priority</option>
                <option value="MEDIUM">Medium Priority</option>
                <option value="HIGH">High Priority</option>
                <option value="CRITICAL">Critical SLA</option>
              </select>
            </div>

            <div>
              <label className="block font-semibold text-zinc-300 mb-1">Due Date</label>
              <input
                type="date"
                {...register('dueDate')}
                className="w-full px-3 py-2.5 bg-zinc-900 border border-white/10 rounded-xl text-zinc-200 focus:outline-none focus:border-amber-500/60"
              />
            </div>
          </div>

          <div className="pt-4 border-t border-white/10 flex items-center justify-end space-x-3">
            <button
              type="button"
              onClick={onClose}
              className="px-4 py-2 rounded-xl text-zinc-400 hover:text-zinc-200 hover:bg-white/5 font-semibold"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="px-5 py-2 rounded-xl font-bold text-xs gold-gradient-bg text-zinc-950 shadow-gold-sm hover:brightness-110"
            >
              Create Task
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
