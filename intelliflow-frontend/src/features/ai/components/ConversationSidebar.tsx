import React from 'react';
import { Conversation } from '../types/ai';
import { Plus, MessageSquare, Pin, Trash2, Search } from 'lucide-react';

interface Props {
  conversations: Conversation[];
  activeId: string | null;
  onSelect: (id: string) => void;
  onNewChat: () => void;
  onDelete: (id: string) => void;
  onPin: (id: string) => void;
}

export const ConversationSidebar: React.FC<Props> = ({
  conversations,
  activeId,
  onSelect,
  onNewChat,
  onDelete,
  onPin,
}) => {
  const [searchQuery, setSearchQuery] = React.useState('');

  const filtered = conversations.filter((c) =>
    c.title.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div className="w-64 glass-card border-r border-white/10 flex flex-col h-full">
      {/* New Chat Action */}
      <div className="p-4 border-b border-white/10">
        <button
          onClick={onNewChat}
          className="w-full py-2.5 px-4 rounded-xl font-bold text-xs gold-gradient-bg text-zinc-950 shadow-gold-sm hover:brightness-110 transition-all flex items-center justify-center space-x-2"
        >
          <Plus className="w-4 h-4 stroke-[3]" />
          <span>New AI Conversation</span>
        </button>
      </div>

      {/* Search Threads */}
      <div className="p-3 border-b border-white/10">
        <div className="relative">
          <Search className="w-3.5 h-3.5 text-zinc-400 absolute left-2.5 top-1/2 -translate-y-1/2" />
          <input
            type="text"
            placeholder="Search threads..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full pl-8 pr-3 py-1.5 bg-zinc-900 border border-white/10 rounded-lg text-xs text-zinc-200 placeholder-zinc-500 focus:outline-none focus:border-amber-500/60"
          />
        </div>
      </div>

      {/* Conversations List */}
      <div className="flex-1 overflow-y-auto p-2 space-y-1">
        {filtered.map((c) => {
          const isActive = c.id === activeId;
          return (
            <div
              key={c.id}
              onClick={() => onSelect(c.id)}
              className={`p-2.5 rounded-xl text-xs flex items-center justify-between cursor-pointer transition-all group ${
                isActive
                  ? 'bg-amber-500/10 text-amber-300 border border-amber-500/30 shadow-gold-sm font-semibold'
                  : 'text-zinc-400 hover:text-zinc-200 hover:bg-white/5'
              }`}
            >
              <div className="flex items-center space-x-2 truncate pr-2">
                <MessageSquare className="w-3.5 h-3.5 flex-shrink-0" />
                <span className="truncate">{c.title}</span>
              </div>

              <div className="flex items-center space-x-1 opacity-0 group-hover:opacity-100 transition-opacity">
                <button
                  onClick={(e) => {
                    e.stopPropagation();
                    onPin(c.id);
                  }}
                  className={`p-1 hover:text-amber-400 ${c.isPinned ? 'text-amber-400 opacity-100' : ''}`}
                >
                  <Pin className="w-3 h-3" />
                </button>
                <button
                  onClick={(e) => {
                    e.stopPropagation();
                    onDelete(c.id);
                  }}
                  className="p-1 hover:text-red-400"
                >
                  <Trash2 className="w-3 h-3" />
                </button>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};
