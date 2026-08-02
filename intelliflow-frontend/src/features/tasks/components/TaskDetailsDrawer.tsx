import React, { useState } from 'react';
import { TaskItem, TaskStatus } from '../types/task';
import { TaskStatusBadge } from './TaskStatusBadge';
import { TaskPriorityBadge } from './TaskPriorityBadge';
import { X, Calendar, User, MessageSquare, Paperclip, Clock } from 'lucide-react';

interface Props {
  task: TaskItem | null;
  onClose: () => void;
  onUpdateStatus: (id: string, status: TaskStatus) => void;
}

export const TaskDetailsDrawer: React.FC<Props> = ({ task, onClose, onUpdateStatus }) => {
  const [activeTab, setActiveTab] = useState<'overview' | 'comments' | 'attachments'>('overview');
  const [newComment, setNewComment] = useState('');

  if (!task) return null;

  const statuses: TaskStatus[] = ['BACKLOG', 'TODO', 'IN_PROGRESS', 'BLOCKED', 'IN_REVIEW', 'COMPLETED', 'ARCHIVED'];

  return (
    <div className="fixed inset-0 z-50 overflow-hidden flex justify-end bg-black/60 backdrop-blur-sm animate-in fade-in">
      <div className="w-full max-w-xl h-full glass-card border-l border-white/10 shadow-2xl flex flex-col justify-between animate-in slide-in-from-right duration-300">
        <div className="p-6 border-b border-white/10 flex items-start justify-between bg-zinc-900/60">
          <div>
            <div className="flex items-center space-x-3 mb-2">
              <TaskPriorityBadge priority={task.priority} />
              <TaskStatusBadge status={task.status} />
            </div>
            <h2 className="text-lg font-bold text-zinc-100">{task.title}</h2>
            <p className="text-[10px] text-zinc-500 font-mono mt-1">ID: {task.id}</p>
          </div>
          <button
            onClick={onClose}
            className="p-2 rounded-xl text-zinc-400 hover:text-zinc-100 hover:bg-white/10 transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        <div className="flex border-b border-white/10 px-6 bg-zinc-950/40 text-xs font-semibold text-zinc-400">
          <button
            onClick={() => setActiveTab('overview')}
            className={`py-3 px-4 border-b-2 transition-colors ${
              activeTab === 'overview' ? 'border-amber-400 text-amber-400' : 'border-transparent hover:text-zinc-200'
            }`}
          >
            Overview
          </button>
          <button
            onClick={() => setActiveTab('comments')}
            className={`py-3 px-4 border-b-2 transition-colors flex items-center space-x-1.5 ${
              activeTab === 'comments' ? 'border-amber-400 text-amber-400' : 'border-transparent hover:text-zinc-200'
            }`}
          >
            <MessageSquare className="w-3.5 h-3.5" />
            <span>Comments ({task.comments?.length || 0})</span>
          </button>
          <button
            onClick={() => setActiveTab('attachments')}
            className={`py-3 px-4 border-b-2 transition-colors flex items-center space-x-1.5 ${
              activeTab === 'attachments' ? 'border-amber-400 text-amber-400' : 'border-transparent hover:text-zinc-200'
            }`}
          >
            <Paperclip className="w-3.5 h-3.5" />
            <span>Files ({task.attachments?.length || 0})</span>
          </button>
        </div>

        <div className="flex-1 p-6 overflow-y-auto space-y-6 text-xs text-zinc-300">
          {activeTab === 'overview' && (
            <>
              <div>
                <label className="block text-[11px] font-semibold text-zinc-400 uppercase tracking-wider mb-2">
                  Update Task Status
                </label>
                <div className="flex flex-wrap gap-2">
                  {statuses.map((st) => (
                    <button
                      key={st}
                      onClick={() => onUpdateStatus(task.id, st)}
                      className={`px-3 py-1.5 rounded-lg text-xs font-bold border transition-colors ${
                        task.status === st
                          ? 'bg-amber-500 text-zinc-950 border-amber-400 shadow-gold-sm'
                          : 'bg-zinc-900 border-white/10 text-zinc-400 hover:border-amber-500/40'
                      }`}
                    >
                      {st}
                    </button>
                  ))}
                </div>
              </div>

              <div>
                <h4 className="text-xs font-bold text-zinc-200 mb-2">Description</h4>
                <div className="p-4 rounded-xl bg-zinc-900/80 border border-white/5 leading-relaxed text-zinc-300">
                  {task.description}
                </div>
              </div>

              <div className="grid grid-cols-2 gap-4 p-4 rounded-xl bg-zinc-900/40 border border-white/5 text-xs">
                <div>
                  <span className="text-zinc-500 flex items-center gap-1 mb-1">
                    <User className="w-3 h-3" /> Assignee
                  </span>
                  <span className="font-semibold text-zinc-200">{task.assignee?.name || 'Unassigned'}</span>
                </div>
                <div>
                  <span className="text-zinc-500 flex items-center gap-1 mb-1">
                    <User className="w-3 h-3" /> Creator
                  </span>
                  <span className="font-semibold text-zinc-200">{task.creator.name}</span>
                </div>
                <div>
                  <span className="text-zinc-500 flex items-center gap-1 mb-1">
                    <Calendar className="w-3 h-3" /> Due Date
                  </span>
                  <span className="font-mono text-zinc-200">{task.dueDate || 'N/A'}</span>
                </div>
                <div>
                  <span className="text-zinc-500 flex items-center gap-1 mb-1">
                    <Clock className="w-3 h-3" /> Last Updated
                  </span>
                  <span className="font-mono text-zinc-200">{new Date(task.updatedAt).toLocaleTimeString()}</span>
                </div>
              </div>
            </>
          )}

          {activeTab === 'comments' && (
            <div className="space-y-4">
              <div className="space-y-3">
                {task.comments?.map((c) => (
                  <div key={c.id} className="p-3.5 rounded-xl bg-zinc-900/80 border border-white/5">
                    <div className="flex items-center justify-between mb-1 text-[11px]">
                      <span className="font-bold text-amber-400">{c.authorName}</span>
                      <span className="text-zinc-500 font-mono">{c.createdAt}</span>
                    </div>
                    <p className="text-zinc-300 leading-relaxed">{c.content}</p>
                  </div>
                ))}

                {(!task.comments || task.comments.length === 0) && (
                  <div className="text-center py-8 text-zinc-500 font-mono">No comments posted yet.</div>
                )}
              </div>

              <div className="pt-4 border-t border-white/10 flex space-x-2">
                <input
                  type="text"
                  placeholder="Write a comment..."
                  value={newComment}
                  onChange={(e) => setNewComment(e.target.value)}
                  className="flex-1 px-3 py-2 bg-zinc-900 border border-white/10 rounded-xl text-xs text-zinc-200 focus:outline-none focus:border-amber-500/60"
                />
                <button
                  onClick={() => setNewComment('')}
                  className="px-4 py-2 rounded-xl gold-gradient-bg text-zinc-950 font-bold text-xs shadow-gold-sm"
                >
                  Post
                </button>
              </div>
            </div>
          )}

          {activeTab === 'attachments' && (
            <div className="space-y-3">
              {task.attachments?.map((att) => (
                <div key={att.id} className="p-3 rounded-xl bg-zinc-900/80 border border-white/5 flex items-center justify-between">
                  <div className="flex items-center space-x-3">
                    <Paperclip className="w-4 h-4 text-amber-400" />
                    <div>
                      <div className="font-bold text-zinc-200">{att.fileName}</div>
                      <div className="text-[10px] text-zinc-500 font-mono">{att.fileSize}</div>
                    </div>
                  </div>
                  <button className="text-xs text-amber-400 hover:underline">Download</button>
                </div>
              ))}

              {(!task.attachments || task.attachments.length === 0) && (
                <div className="text-center py-8 text-zinc-500 font-mono">No files attached to this task.</div>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
