import React, { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { TaskItem, UpdateTaskInput } from '../types/task';
import { X, Edit2 } from 'lucide-react';

interface Props {
  task: TaskItem | null;
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (id: string, data: UpdateTaskInput) => void;
}

export const EditTaskModal: React.FC<Props> = ({ task, isOpen, onClose, onSubmit }) => {
  const { register, handleSubmit, reset } = useForm<UpdateTaskInput>();

  useEffect(() => {
    if (task) {
      reset({
        title: task.title,
        description: task.description,
        priority: task.priority,
        status: task.status,
        dueDate: task.dueDate,
      });
    }
  }, [task, reset]);

  if (!isOpen || !task) return null;

  const handleFormSubmit = (data: UpdateTaskInput) => {
    onSubmit(task.id, data);
    onClose();
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/70 backdrop-blur-sm animate-in fade-in">
      <div className="w-full max-w-lg glass-card border border-white/10 rounded-2xl shadow-2xl p-6 relative">
        <div className="flex items-center justify-between border-b border-white/10 pb-4 mb-5">
          <div className="flex items-center space-x-2">
            <div className="w-8 h-8 rounded-lg bg-amber-500/10 border border-amber-500/30 flex items-center justify-center text-amber-400 font-bold">
              <Edit2 className="w-4 h-4" />
            </div>
            <h3 className="text-base font-bold text-zinc-100">Edit Task Specification</h3>
          </div>
          <button onClick={onClose} className="p-1.5 rounded-lg text-zinc-400 hover:text-zinc-100 hover:bg-white/10">
            <X className="w-5 h-5" />
          </button>
        </div>

        <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-4 text-xs">
          <div>
            <label className="block font-semibold text-zinc-300 mb-1">Task Title</label>
            <input
              type="text"
              {...register('title')}
              className="w-full px-3.5 py-2.5 bg-zinc-900/80 border border-white/10 rounded-xl text-zinc-100 focus:outline-none focus:border-amber-500/60"
            />
          </div>

          <div>
            <label className="block font-semibold text-zinc-300 mb-1">Description</label>
            <textarea
              rows={3}
              {...register('description')}
              className="w-full px-3.5 py-2.5 bg-zinc-900/80 border border-white/10 rounded-xl text-zinc-100 focus:outline-none focus:border-amber-500/60"
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block font-semibold text-zinc-300 mb-1">Status</label>
              <select
                {...register('status')}
                className="w-full px-3 py-2.5 bg-zinc-900 border border-white/10 rounded-xl text-zinc-200 focus:outline-none focus:border-amber-500/60"
              >
                <option value="BACKLOG">Backlog</option>
                <option value="TODO">To Do</option>
                <option value="IN_PROGRESS">In Progress</option>
                <option value="BLOCKED">Blocked</option>
                <option value="IN_REVIEW">In Review</option>
                <option value="COMPLETED">Completed</option>
                <option value="ARCHIVED">Archived</option>
              </select>
            </div>

            <div>
              <label className="block font-semibold text-zinc-300 mb-1">Priority Level</label>
              <select
                {...register('priority')}
                className="w-full px-3 py-2.5 bg-zinc-900 border border-white/10 rounded-xl text-zinc-200 focus:outline-none focus:border-amber-500/60"
              >
                <option value="LOW">Low</option>
                <option value="MEDIUM">Medium</option>
                <option value="HIGH">High</option>
                <option value="CRITICAL">Critical</option>
              </select>
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
              Save Changes
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
