import React from 'react';
import { TaskItem } from '../types/task';
import { TaskPriorityBadge } from './TaskPriorityBadge';
import { TaskAvatarGroup } from './TaskAvatarGroup';
import { CheckSquare, MessageSquare, Paperclip, Calendar, MoreVertical, Edit2, Trash2 } from 'lucide-react';

interface Props {
  task: TaskItem;
  onSelectTask: (task: TaskItem) => void;
  onEditTask: (task: TaskItem) => void;
  onDeleteTask: (task: TaskItem) => void;
}

export const TaskCard: React.FC<Props> = ({ task, onSelectTask, onEditTask, onDeleteTask }) => {
  const [menuOpen, setMenuOpen] = React.useState(false);

  return (
    <div
      onClick={() => onSelectTask(task)}
      className="p-4 rounded-xl glass-card border border-white/10 hover:border-amber-500/40 transition-all shadow-sm hover:shadow-gold-glow cursor-pointer group relative flex flex-col justify-between space-y-3"
    >
      {/* Header */}
      <div>
        <div className="flex items-center justify-between gap-2 mb-2">
          <TaskPriorityBadge priority={task.priority} />
          <div className="relative" onClick={(e) => e.stopPropagation()}>
            <button
              onClick={() => setMenuOpen(!menuOpen)}
              className="p-1 rounded-lg text-zinc-400 hover:text-zinc-200 hover:bg-white/5 transition-colors"
            >
              <MoreVertical className="w-3.5 h-3.5" />
            </button>
            {menuOpen && (
              <div className="absolute right-0 mt-1 w-32 glass-card border border-white/10 rounded-lg py-1 text-xs z-30 shadow-2xl">
                <button
                  onClick={() => {
                    setMenuOpen(false);
                    onEditTask(task);
                  }}
                  className="w-full flex items-center px-3 py-1.5 text-zinc-300 hover:bg-white/5 hover:text-amber-400 transition-colors"
                >
                  <Edit2 className="w-3 h-3 mr-2" /> Edit Task
                </button>
                <button
                  onClick={() => {
                    setMenuOpen(false);
                    onDeleteTask(task);
                  }}
                  className="w-full flex items-center px-3 py-1.5 text-red-400 hover:bg-red-500/10 transition-colors"
                >
                  <Trash2 className="w-3 h-3 mr-2" /> Delete
                </button>
              </div>
            )}
          </div>
        </div>

        <h4 className="text-xs font-bold text-zinc-100 group-hover:text-amber-300 transition-colors line-clamp-2 leading-snug">
          {task.title}
        </h4>
        <p className="text-[11px] text-zinc-400 mt-1 line-clamp-2 leading-relaxed">
          {task.description}
        </p>
      </div>

      {/* Meta Indicators */}
      <div className="pt-2 border-t border-white/5 flex items-center justify-between text-[10px] text-zinc-400">
        <div className="flex items-center space-x-3">
          {task.subtaskCount > 0 && (
            <span className="flex items-center space-x-1" title="Subtasks">
              <CheckSquare className="w-3 h-3 text-amber-400/80" />
              <span>{task.completedSubtasks}/{task.subtaskCount}</span>
            </span>
          )}
          {task.comments && task.comments.length > 0 && (
            <span className="flex items-center space-x-1" title="Comments">
              <MessageSquare className="w-3 h-3" />
              <span>{task.comments.length}</span>
            </span>
          )}
          {task.attachments && task.attachments.length > 0 && (
            <span className="flex items-center space-x-1" title="Attachments">
              <Paperclip className="w-3 h-3" />
              <span>{task.attachments.length}</span>
            </span>
          )}
        </div>

        <TaskAvatarGroup assignee={task.assignee} creator={task.creator} />
      </div>

      {/* Due Date Footer */}
      {task.dueDate && (
        <div className="flex items-center space-x-1 text-[9px] text-zinc-500 font-mono">
          <Calendar className="w-2.5 h-2.5" />
          <span>Due: {task.dueDate}</span>
        </div>
      )}
    </div>
  );
};
