import React from 'react';
import { NavLink } from 'react-router-dom';
import {
  LayoutDashboard,
  CheckSquare,
  FileText,
  Search,
  MessageSquareText,
  Bell,
  User,
  Shield,
  Settings,
  Zap,
} from 'lucide-react';

export const Sidebar: React.FC = () => {
  const navItems = [
    { title: 'Dashboard', href: '/dashboard', icon: LayoutDashboard },
    { title: 'Tasks Matrix', href: '/tasks', icon: CheckSquare, badge: '5 Active' },
    { title: 'Document Vault', href: '/documents', icon: FileText },
    { title: 'AI Vector Search', href: '/ai-search', icon: Search, badge: 'RAG' },
    { title: 'AI Assistant Chat', href: '/ai-chat', icon: MessageSquareText },
    { title: 'Notifications', href: '/notifications', icon: Bell },
    { title: 'User Profile', href: '/profile', icon: User },
    { title: 'Admin Console', href: '/admin', icon: Shield },
    { title: 'Settings', href: '/settings', icon: Settings },
  ];

  return (
    <aside className="w-64 glass-card border-r border-white/10 flex flex-col h-screen sticky top-0">
      {/* Brand Header */}
      <div className="h-16 px-6 flex items-center border-b border-white/10">
        <div className="flex items-center space-x-3">
          <div className="w-9 h-9 rounded-xl gold-gradient-bg flex items-center justify-center shadow-gold-glow">
            <Zap className="w-5 h-5 text-zinc-950 stroke-[2.5]" />
          </div>
          <div>
            <h1 className="text-base font-bold tracking-tight gold-gradient-text">IntelliFlow</h1>
            <p className="text-[10px] text-zinc-400 font-mono">ENTERPRISE RAG PLATFORM</p>
          </div>
        </div>
      </div>

      {/* Navigation List */}
      <nav className="flex-1 px-3 py-4 space-y-1 overflow-y-auto">
        <div className="px-3 py-2 text-[10px] font-semibold text-zinc-500 uppercase tracking-wider">
          Core Workflows
        </div>
        {navItems.map((item) => {
          const Icon = item.icon;
          return (
            <NavLink
              key={item.href}
              to={item.href}
              className={({ isActive }) =>
                `flex items-center justify-between px-3 py-2.5 rounded-xl text-xs font-medium transition-all group ${
                  isActive
                    ? 'bg-amber-500/10 text-amber-400 border border-amber-500/30 shadow-gold-sm'
                    : 'text-zinc-400 hover:text-zinc-200 hover:bg-white/5'
                }`
              }
            >
              <div className="flex items-center space-x-3">
                <Icon className="w-4 h-4 transition-transform group-hover:scale-110" />
                <span>{item.title}</span>
              </div>
              {item.badge && (
                <span className="px-2 py-0.5 text-[9px] font-semibold rounded-full bg-amber-500/20 text-amber-300 border border-amber-500/30">
                  {item.badge}
                </span>
              )}
            </NavLink>
          );
        })}
      </nav>

      {/* Footer Info */}
      <div className="p-4 border-t border-white/10">
        <div className="p-3 rounded-xl bg-zinc-900/60 border border-white/5 text-xs text-zinc-400">
          <div className="flex items-center justify-between font-mono text-[10px]">
            <span>ENV: PRODUCTION</span>
            <span className="text-emerald-400 font-semibold">● ONLINE</span>
          </div>
          <p className="mt-1 text-[11px] text-zinc-500">v1.0.0-SNAPSHOT</p>
        </div>
      </div>
    </aside>
  );
};
