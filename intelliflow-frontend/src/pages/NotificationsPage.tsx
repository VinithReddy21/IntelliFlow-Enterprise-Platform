import React, { useState } from 'react';
import { NotificationItem } from '../features/notifications/types/notification';
import { CheckCheck, ShieldAlert, Cpu, Sparkles, Trash2 } from 'lucide-react';

export const NotificationsPage: React.FC = () => {
  const [notifications, setNotifications] = useState<NotificationItem[]>([
    {
      id: 'notif-1',
      title: 'Task Assigned: Deploy pgvector HNSW Index',
      message: 'Alex Architect assigned task fe9e239a to you with Critical SLA priority.',
      type: 'TASK_ASSIGNED',
      isRead: false,
      timestamp: '10 mins ago',
    },
    {
      id: 'notif-2',
      title: 'Document Ingestion Complete: Enterprise_RAG.pdf',
      message: '384-dimensional embeddings generated with pgvector HNSW cosine ops.',
      type: 'DOCUMENT_EMBEDDED',
      isRead: false,
      timestamp: '1 hour ago',
    },
    {
      id: 'notif-3',
      title: 'Security Alert: RateLimitingFilter Triggered',
      message: 'Client IP 127.0.0.1 exceeded rate limit threshold on /api/v1/auth/login.',
      type: 'SECURITY_ALERT',
      isRead: true,
      timestamp: '3 hours ago',
    },
  ]);

  const markAllRead = () => {
    setNotifications((prev) => prev.map((n) => ({ ...n, isRead: true })));
  };

  const deleteNotification = (id: string) => {
    setNotifications((prev) => prev.filter((n) => n.id !== id));
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <div className="inline-flex items-center space-x-2 text-amber-400 text-xs font-semibold uppercase tracking-wider mb-1">
            <Sparkles className="w-3.5 h-3.5" />
            <span>Real-time STOMP WebSockets</span>
          </div>
          <h1 className="text-2xl font-bold text-zinc-100">Notifications & System Telemetry</h1>
        </div>

        <div className="flex items-center space-x-3">
          <div className="flex items-center space-x-2 px-3 py-1.5 rounded-full bg-emerald-500/10 border border-emerald-500/30 text-emerald-400 text-xs font-mono font-semibold">
            <span className="w-2 h-2 rounded-full bg-emerald-400 animate-ping"></span>
            <span>WebSocket STOMP: CONNECTED</span>
          </div>

          <button
            onClick={markAllRead}
            className="px-4 py-2 rounded-xl text-xs font-bold glass-card border border-white/10 text-zinc-200 hover:border-amber-500/40 transition-colors flex items-center space-x-1.5"
          >
            <CheckCheck className="w-4 h-4 text-amber-400" />
            <span>Mark All Read</span>
          </button>
        </div>
      </div>

      <div className="space-y-3">
        {notifications.map((n) => (
          <div
            key={n.id}
            className={`p-4 rounded-2xl glass-card border transition-all flex items-start justify-between ${
              !n.isRead ? 'border-amber-500/30 bg-amber-500/5 shadow-gold-sm' : 'border-white/10'
            }`}
          >
            <div className="flex items-start space-x-3">
              <div className="p-2.5 rounded-xl bg-white/5 border border-white/10 text-amber-400 mt-0.5">
                {n.type === 'SECURITY_ALERT' ? <ShieldAlert className="w-4 h-4 text-red-400" /> : <Cpu className="w-4 h-4" />}
              </div>
              <div>
                <h4 className="text-xs font-bold text-zinc-100 flex items-center gap-2">
                  {n.title}
                  {!n.isRead && (
                    <span className="w-2 h-2 rounded-full bg-amber-400 animate-pulse"></span>
                  )}
                </h4>
                <p className="text-xs text-zinc-400 mt-1 leading-relaxed">{n.message}</p>
                <span className="text-[10px] text-zinc-500 font-mono mt-2 block">{n.timestamp}</span>
              </div>
            </div>

            <button
              onClick={() => deleteNotification(n.id)}
              className="p-1.5 rounded-lg text-zinc-500 hover:text-red-400 hover:bg-red-500/10 transition-colors"
            >
              <Trash2 className="w-4 h-4" />
            </button>
          </div>
        ))}

        {notifications.length === 0 && (
          <div className="p-12 text-center text-xs text-zinc-500 font-mono glass-card border border-white/10 rounded-2xl">
            No active notifications in queue.
          </div>
        )}
      </div>
    </div>
  );
};
