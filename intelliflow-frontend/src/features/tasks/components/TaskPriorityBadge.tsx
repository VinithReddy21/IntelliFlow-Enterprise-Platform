import React from 'react';
import { TaskPriority } from '../types/task';
import { AlertCircle, ArrowUp, ArrowDown, Flame } from 'lucide-react';

interface Props {
  priority: TaskPriority;
}

export const TaskPriorityBadge: React.FC<Props> = ({ priority }) => {
  const config: Record<TaskPriority, { label: string; className: string; icon: React.ElementType }> = {
    LOW: { label: 'Low', className: 'text-zinc-400 bg-zinc-800/60 border-zinc-700', icon: ArrowDown },
    MEDIUM: { label: 'Medium', className: 'text-amber-300 bg-amber-500/10 border-amber-500/30', icon: ArrowUp },
    HIGH: { label: 'High', className: 'text-orange-400 bg-orange-500/10 border-orange-500/30', icon: AlertCircle },
    CRITICAL: { label: 'Critical', className: 'text-red-400 bg-red-500/20 border-red-500/40 shadow-gold-sm', icon: Flame },
  };

  const item = config[priority] || config.MEDIUM;
  const Icon = item.icon;

  return (
    <span className={`inline-flex items-center space-x-1 px-2 py-0.5 rounded-md text-[10px] font-bold border ${item.className}`}>
      <Icon className="w-3 h-3" />
      <span>{item.label}</span>
    </span>
  );
};
