import React from 'react';
import { TaskStatus } from '../types/task';

interface Props {
  status: TaskStatus;
}

export const TaskStatusBadge: React.FC<Props> = ({ status }) => {
  const config: Record<TaskStatus, { label: string; className: string }> = {
    BACKLOG: { label: 'Backlog', className: 'bg-zinc-800 text-zinc-400 border-zinc-700' },
    TODO: { label: 'To Do', className: 'bg-amber-500/10 text-amber-400 border-amber-500/30' },
    IN_PROGRESS: { label: 'In Progress', className: 'bg-blue-500/10 text-blue-400 border-blue-500/30' },
    BLOCKED: { label: 'Blocked', className: 'bg-red-500/15 text-red-400 border-red-500/40 animate-pulse' },
    IN_REVIEW: { label: 'In Review', className: 'bg-purple-500/10 text-purple-400 border-purple-500/30' },
    COMPLETED: { label: 'Completed', className: 'bg-emerald-500/10 text-emerald-400 border-emerald-500/30' },
    ARCHIVED: { label: 'Archived', className: 'bg-zinc-900 text-zinc-500 border-zinc-800' },
  };

  const item = config[status] || config.TODO;

  return (
    <span className={`px-2.5 py-0.5 rounded-full text-[10px] font-bold border transition-colors ${item.className}`}>
      {item.label}
    </span>
  );
};
