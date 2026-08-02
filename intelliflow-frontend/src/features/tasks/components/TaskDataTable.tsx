import React, { useState } from 'react';
import { TaskItem } from '../types/task';
import { TaskStatusBadge } from './TaskStatusBadge';
import { TaskPriorityBadge } from './TaskPriorityBadge';
import { TaskAvatarGroup } from './TaskAvatarGroup';
import { Edit2, Trash2, ArrowUpDown, ChevronLeft, ChevronRight } from 'lucide-react';

interface Props {
  tasks: TaskItem[];
  onSelectTask: (task: TaskItem) => void;
  onEditTask: (task: TaskItem) => void;
  onDeleteTask: (task: TaskItem) => void;
}

export const TaskDataTable: React.FC<Props> = ({
  tasks,
  onSelectTask,
  onEditTask,
  onDeleteTask,
}) => {
  const [sortField, setSortField] = useState<'title' | 'priority' | 'status' | 'createdAt'>('createdAt');
  const [sortOrder, setSortOrder] = useState<'asc' | 'desc'>('desc');
  const [currentPage, setCurrentPage] = useState(1);
  const pageSize = 5;

  const handleSort = (field: 'title' | 'priority' | 'status' | 'createdAt') => {
    if (sortField === field) {
      setSortOrder(sortOrder === 'asc' ? 'desc' : 'asc');
    } else {
      setSortField(field);
      setSortOrder('asc');
    }
  };

  const sortedTasks = [...tasks].sort((a, b) => {
    const valA = a[sortField] || '';
    const valB = b[sortField] || '';
    if (valA < valB) return sortOrder === 'asc' ? -1 : 1;
    if (valA > valB) return sortOrder === 'asc' ? 1 : -1;
    return 0;
  });

  const totalPages = Math.ceil(sortedTasks.length / pageSize) || 1;
  const paginatedTasks = sortedTasks.slice((currentPage - 1) * pageSize, currentPage * pageSize);

  return (
    <div className="space-y-4">
      <div className="rounded-2xl glass-card border border-white/10 overflow-hidden shadow-2xl">
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="border-b border-white/10 bg-zinc-900/80 text-[11px] font-semibold text-zinc-400 uppercase tracking-wider">
                <th className="p-4 cursor-pointer hover:text-amber-400 transition-colors" onClick={() => handleSort('title')}>
                  <div className="flex items-center space-x-1">
                    <span>Task Title & Context</span>
                    <ArrowUpDown className="w-3 h-3" />
                  </div>
                </th>
                <th className="p-4 cursor-pointer hover:text-amber-400 transition-colors" onClick={() => handleSort('status')}>
                  <div className="flex items-center space-x-1">
                    <span>Status</span>
                    <ArrowUpDown className="w-3 h-3" />
                  </div>
                </th>
                <th className="p-4 cursor-pointer hover:text-amber-400 transition-colors" onClick={() => handleSort('priority')}>
                  <div className="flex items-center space-x-1">
                    <span>Priority</span>
                    <ArrowUpDown className="w-3 h-3" />
                  </div>
                </th>
                <th className="p-4">Department</th>
                <th className="p-4">Assignee</th>
                <th className="p-4 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-white/5 text-xs text-zinc-300">
              {paginatedTasks.map((task) => (
                <tr
                  key={task.id}
                  onClick={() => onSelectTask(task)}
                  className="hover:bg-amber-500/5 transition-colors cursor-pointer group"
                >
                  <td className="p-4 max-w-xs">
                    <div className="font-bold text-zinc-100 group-hover:text-amber-300 transition-colors truncate">
                      {task.title}
                    </div>
                    <div className="text-[11px] text-zinc-500 truncate mt-0.5">{task.description}</div>
                  </td>
                  <td className="p-4">
                    <TaskStatusBadge status={task.status} />
                  </td>
                  <td className="p-4">
                    <TaskPriorityBadge priority={task.priority} />
                  </td>
                  <td className="p-4 font-mono text-[11px] text-zinc-400">
                    {task.departmentName || 'General'}
                  </td>
                  <td className="p-4">
                    <TaskAvatarGroup assignee={task.assignee} creator={task.creator} />
                  </td>
                  <td className="p-4 text-right" onClick={(e) => e.stopPropagation()}>
                    <div className="flex items-center justify-end space-x-2">
                      <button
                        onClick={() => onEditTask(task)}
                        className="p-1.5 rounded-lg text-zinc-400 hover:text-amber-400 hover:bg-white/5 transition-colors"
                        title="Edit Task"
                      >
                        <Edit2 className="w-3.5 h-3.5" />
                      </button>
                      <button
                        onClick={() => onDeleteTask(task)}
                        className="p-1.5 rounded-lg text-zinc-400 hover:text-red-400 hover:bg-red-500/10 transition-colors"
                        title="Delete Task"
                      >
                        <Trash2 className="w-3.5 h-3.5" />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}

              {paginatedTasks.length === 0 && (
                <tr>
                  <td colSpan={6} className="p-8 text-center text-xs text-zinc-500 font-mono">
                    No matching tasks found.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

        <div className="p-4 border-t border-white/10 bg-zinc-900/40 flex items-center justify-between text-xs text-zinc-400">
          <span>
            Showing <strong className="text-zinc-200">{paginatedTasks.length}</strong> of{' '}
            <strong className="text-zinc-200">{tasks.length}</strong> tasks
          </span>

          <div className="flex items-center space-x-2">
            <button
              disabled={currentPage === 1}
              onClick={() => setCurrentPage((p) => Math.max(1, p - 1))}
              className="p-1.5 rounded-lg glass-card border border-white/10 hover:border-amber-500/30 disabled:opacity-40 transition-colors"
            >
              <ChevronLeft className="w-4 h-4" />
            </button>
            <span className="font-mono text-xs text-zinc-300">
              Page {currentPage} of {totalPages}
            </span>
            <button
              disabled={currentPage === totalPages}
              onClick={() => setCurrentPage((p) => Math.min(totalPages, p + 1))}
              className="p-1.5 rounded-lg glass-card border border-white/10 hover:border-amber-500/30 disabled:opacity-40 transition-colors"
            >
              <ChevronRight className="w-4 h-4" />
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
