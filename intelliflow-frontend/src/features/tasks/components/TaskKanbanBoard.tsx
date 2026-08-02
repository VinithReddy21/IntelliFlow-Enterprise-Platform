import React from 'react';
import { TaskItem, TaskStatus } from '../types/task';
import { TaskCard } from './TaskCard';

interface Props {
  tasks: TaskItem[];
  onSelectTask: (task: TaskItem) => void;
  onEditTask: (task: TaskItem) => void;
  onDeleteTask: (task: TaskItem) => void;
  onUpdateStatus: (id: string, status: TaskStatus) => void;
}

const columns: { status: TaskStatus; title: string; color: string }[] = [
  { status: 'BACKLOG', title: 'Backlog', color: 'border-zinc-700 text-zinc-400' },
  { status: 'TODO', title: 'To Do', color: 'border-amber-500/40 text-amber-400' },
  { status: 'IN_PROGRESS', title: 'In Progress', color: 'border-blue-500/40 text-blue-400' },
  { status: 'BLOCKED', title: 'Blocked', color: 'border-red-500/50 text-red-400' },
  { status: 'IN_REVIEW', title: 'In Review', color: 'border-purple-500/40 text-purple-400' },
  { status: 'COMPLETED', title: 'Completed', color: 'border-emerald-500/40 text-emerald-400' },
  { status: 'ARCHIVED', title: 'Archived', color: 'border-zinc-800 text-zinc-500' },
];

export const TaskKanbanBoard: React.FC<Props> = ({
  tasks,
  onSelectTask,
  onEditTask,
  onDeleteTask,
  onUpdateStatus,
}) => {
  const [draggedTaskId, setDraggedTaskId] = React.useState<string | null>(null);

  const handleDragStart = (e: React.DragEvent, id: string) => {
    e.dataTransfer.setData('text/plain', id);
    setDraggedTaskId(id);
  };

  const handleDrop = (e: React.DragEvent, status: TaskStatus) => {
    e.preventDefault();
    const id = e.dataTransfer.getData('text/plain') || draggedTaskId;
    if (id) {
      onUpdateStatus(id, status);
      setDraggedTaskId(null);
    }
  };

  const handleDragOver = (e: React.DragEvent) => {
    e.preventDefault();
  };

  return (
    <div className="flex gap-4 overflow-x-auto pb-6 pt-2">
      {columns.map((col) => {
        const colTasks = tasks.filter((t) => t.status === col.status);
        return (
          <div
            key={col.status}
            onDragOver={handleDragOver}
            onDrop={(e) => handleDrop(e, col.status)}
            className="w-72 flex-shrink-0 rounded-2xl glass-card border border-white/10 p-3.5 flex flex-col min-h-[550px]"
          >
            {/* Column Header */}
            <div className={`flex items-center justify-between pb-3 border-b border-white/10 mb-3 ${col.color}`}>
              <div className="flex items-center space-x-2">
                <span className="w-2 h-2 rounded-full bg-current"></span>
                <h3 className="text-xs font-bold uppercase tracking-wider">{col.title}</h3>
              </div>
              <span className="px-2 py-0.5 rounded-full bg-white/5 border border-white/10 text-[10px] font-mono font-bold text-zinc-300">
                {colTasks.length}
              </span>
            </div>

            {/* Cards Container */}
            <div className="flex-1 space-y-3 overflow-y-auto">
              {colTasks.map((task) => (
                <div
                  key={task.id}
                  draggable
                  onDragStart={(e) => handleDragStart(e, task.id)}
                  className="cursor-grab active:cursor-grabbing"
                >
                  <TaskCard
                    task={task}
                    onSelectTask={onSelectTask}
                    onEditTask={onEditTask}
                    onDeleteTask={onDeleteTask}
                  />
                </div>
              ))}

              {colTasks.length === 0 && (
                <div className="h-32 border-2 border-dashed border-white/5 rounded-xl flex items-center justify-center text-[11px] text-zinc-600 font-mono">
                  Drop tasks here
                </div>
              )}
            </div>
          </div>
        );
      })}
    </div>
  );
};
