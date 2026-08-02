import React from 'react';
import { Bell, Search, Sparkles, Shield, User as UserIcon, LogOut } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';

export const Navbar: React.FC = () => {
  const { user, logout } = useAuth();
  const [dropdownOpen, setDropdownOpen] = React.useState(false);

  return (
    <header className="sticky top-0 z-30 h-16 glass-card border-b border-white/10 px-6 flex items-center justify-between">
      {/* Search Input */}
      <div className="relative w-96">
        <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-zinc-400" />
        <input
          type="text"
          placeholder="Search tasks, vector documents, RAG queries... (Ctrl + K)"
          className="w-full pl-9 pr-4 py-1.5 bg-zinc-900/80 border border-white/10 rounded-lg text-xs text-zinc-200 placeholder-zinc-500 focus:outline-none focus:border-primary/60 transition-colors"
        />
      </div>

      {/* Right Controls */}
      <div className="flex items-center space-x-4">
        {/* System Health Badge */}
        <div className="hidden md:flex items-center space-x-2 px-3 py-1 rounded-full bg-amber-500/10 border border-amber-500/20 text-amber-400 text-xs font-medium">
          <Sparkles className="w-3.5 h-3.5 animate-pulse text-amber-400" />
          <span>pgvector Engine Active</span>
        </div>

        {/* Notifications */}
        <button className="relative p-2 text-zinc-400 hover:text-amber-400 transition-colors rounded-lg hover:bg-white/5">
          <Bell className="w-5 h-5" />
          <span className="absolute top-1.5 right-1.5 w-2 h-2 bg-amber-400 rounded-full ring-4 ring-zinc-950 animate-pulse"></span>
        </button>

        {/* User Profile Dropdown */}
        <div className="relative">
          <button
            onClick={() => setDropdownOpen(!dropdownOpen)}
            className="flex items-center space-x-3 p-1.5 rounded-lg hover:bg-white/5 transition-colors border border-transparent hover:border-white/10"
          >
            <div className="w-8 h-8 rounded-full gold-gradient-bg flex items-center justify-center text-zinc-950 font-bold text-xs ring-2 ring-amber-500/40">
              {user?.username?.substring(0, 2).toUpperCase() || 'EX'}
            </div>
            <div className="hidden lg:block text-left">
              <div className="text-xs font-semibold text-zinc-200">{user?.username || 'Architect'}</div>
              <div className="text-[10px] text-amber-400/90 flex items-center gap-1">
                <Shield className="w-2.5 h-2.5" />
                <span>{user?.role?.replace('ROLE_', '')}</span>
              </div>
            </div>
          </button>

          {dropdownOpen && (
            <div className="absolute right-0 mt-2 w-48 glass-card border border-white/10 rounded-xl shadow-2xl py-1 text-xs z-50 animate-in fade-in slide-in-from-top-2">
              <div className="px-4 py-2 border-b border-white/10">
                <p className="font-semibold text-zinc-200">{user?.username}</p>
                <p className="text-[10px] text-zinc-400 truncate">{user?.email}</p>
              </div>
              <a href="#profile" className="flex items-center px-4 py-2 text-zinc-300 hover:bg-white/5 hover:text-amber-400 transition-colors">
                <UserIcon className="w-3.5 h-3.5 mr-2" />
                Profile Settings
              </a>
              <button
                onClick={logout}
                className="w-full flex items-center px-4 py-2 text-red-400 hover:bg-red-500/10 transition-colors border-t border-white/10 mt-1"
              >
                <LogOut className="w-3.5 h-3.5 mr-2" />
                Sign Out
              </button>
            </div>
          )}
        </div>
      </div>
    </header>
  );
};
