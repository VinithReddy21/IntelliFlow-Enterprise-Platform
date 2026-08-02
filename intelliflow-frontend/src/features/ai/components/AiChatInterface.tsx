import React, { useState, useRef, useEffect } from 'react';
import { Conversation, ChatMessage } from '../types/ai';
import { useRagQuery } from '../hooks/useAi';
import { ConversationSidebar } from './ConversationSidebar';
import { ChatMessageItem } from './ChatMessageItem';
import { Send, Square, Sparkles, Bot, Terminal } from 'lucide-react';

export const AiChatInterface: React.FC = () => {
  const ragQueryMutation = useRagQuery();

  const [conversations, setConversations] = useState<Conversation[]>([
    {
      id: 'conv-1',
      title: 'Vector Engine & pgvector Architecture',
      createdAt: '2026-08-02T10:00:00Z',
      updatedAt: '2026-08-02T10:00:00Z',
      isPinned: true,
      messages: [
        {
          id: 'msg-1',
          sender: 'user',
          content: 'Explain how IntelliFlow implements 1536-dimensional vector search with pgvector.',
          timestamp: '10:00 AM',
        },
        {
          id: 'msg-2',
          sender: 'assistant',
          content: `IntelliFlow platform implements a **1536-dimensional vector similarity engine** using PostgreSQL \`pgvector\` with an HNSW index (\`vector_cosine_ops\`).

### Architectural Pipeline
1. **Document Ingestion**: Files uploaded via \`FileStorageService\` are parsed by Apache Tika and split into 500-token chunks (50-token overlap).
2. **Embedding Generation**: \`OpenAiEmbeddingService\` generates 1536-dimensional vector arrays stored in \`document_chunks\`.
3. **Similarity Search**: Executes HNSW vector cosine similarity queries with department-level ABAC security filtering.`,
          timestamp: '10:01 AM',
          confidenceScore: 0.96,
          latencyMs: 18,
          citations: [
            {
              id: 'cit-1',
              documentId: '46246246-65c4-4ea4-ad49-5299342bc731',
              documentTitle: 'Enterprise_RAG_Architecture_Specification_v1.pdf',
              chunkIndex: 1,
              similarityScore: 0.96,
              contentSnippet: 'IntelliFlow platform implements a 1536-dimensional vector similarity retrieval engine leveraging pgvector HNSW indexes.',
            },
          ],
        },
      ],
    },
  ]);

  const [activeConvId, setActiveConvId] = useState<string>('conv-1');
  const [inputPrompt, setInputPrompt] = useState('');
  const [isGenerating, setIsGenerating] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const activeConv = conversations.find((c) => c.id === activeConvId) || conversations[0];

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [activeConv?.messages, isGenerating]);

  const handleSend = () => {
    if (!inputPrompt.trim() || isGenerating) return;

    const userMsg: ChatMessage = {
      id: crypto.randomUUID(),
      sender: 'user',
      content: inputPrompt,
      timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
    };

    setConversations((prev) =>
      prev.map((c) => (c.id === activeConvId ? { ...c, messages: [...c.messages, userMsg] } : c))
    );

    const currentPrompt = inputPrompt;
    setInputPrompt('');
    setIsGenerating(true);

    ragQueryMutation.mutate(currentPrompt, {
      onSuccess: (data) => {
        const assistantMsg: ChatMessage = {
          id: crypto.randomUUID(),
          sender: 'assistant',
          content: data.answer,
          timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
          citations: data.citations,
          confidenceScore: data.confidenceScore,
          latencyMs: data.latencyMs,
        };

        setConversations((prev) =>
          prev.map((c) => (c.id === activeConvId ? { ...c, messages: [...c.messages, assistantMsg] } : c))
        );
        setIsGenerating(false);
      },
      onError: () => {
        setIsGenerating(false);
      },
    });
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLTextAreaElement>) => {
    if (e.key === 'Enter' && (e.ctrlKey || e.metaKey)) {
      e.preventDefault();
      handleSend();
    }
  };

  const handleNewChat = () => {
    const newId = crypto.randomUUID();
    const newConv: Conversation = {
      id: newId,
      title: 'New AI Conversation',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
      messages: [],
    };
    setConversations([newConv, ...conversations]);
    setActiveConvId(newId);
  };

  return (
    <div className="flex h-[calc(100vh-7rem)] rounded-2xl glass-card border border-white/10 overflow-hidden shadow-2xl">
      {/* Sidebar */}
      <ConversationSidebar
        conversations={conversations}
        activeId={activeConvId}
        onSelect={setActiveConvId}
        onNewChat={handleNewChat}
        onDelete={(id) => setConversations((prev) => prev.filter((c) => c.id !== id))}
        onPin={(id) => setConversations((prev) => prev.map((c) => (c.id === id ? { ...c, isPinned: !c.isPinned } : c)))}
      />

      {/* Main Chat Canvas */}
      <div className="flex-1 flex flex-col justify-between bg-zinc-950/40 relative">
        {/* Messages Scroll Area */}
        <div className="flex-1 overflow-y-auto p-6 space-y-6">
          {activeConv?.messages.map((msg) => (
            <ChatMessageItem key={msg.id} message={msg} onRegenerate={handleSend} />
          ))}

          {isGenerating && (
            <div className="flex gap-4 p-5 rounded-2xl glass-card-gold border border-amber-500/20 items-center">
              <div className="w-9 h-9 rounded-xl gold-gradient-bg flex items-center justify-center text-zinc-950 font-bold shadow-gold-glow animate-pulse">
                <Bot className="w-5 h-5 stroke-[2.5]" />
              </div>
              <div className="flex items-center space-x-2 text-xs font-semibold text-amber-400">
                <Sparkles className="w-4 h-4 animate-spin" />
                <span>Executing Grounded RAG Query & Vector Cosine Retrieval...</span>
              </div>
            </div>
          )}

          {activeConv?.messages.length === 0 && (
            <div className="h-full flex flex-col items-center justify-center text-center p-8 max-w-xl mx-auto space-y-6">
              <div className="w-14 h-14 rounded-2xl gold-gradient-bg flex items-center justify-center text-zinc-950 shadow-gold-glow">
                <Terminal className="w-8 h-8 stroke-[2.5]" />
              </div>
              <div>
                <h2 className="text-xl font-extrabold text-zinc-100 gold-gradient-text">IntelliFlow Copilot Enterprise</h2>
                <p className="text-xs text-zinc-400 mt-2 leading-relaxed">
                  Ask grounded questions against your ingested vector documents, security policies, and benchmark reports.
                </p>
              </div>

              {/* Prompt Suggestions */}
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 w-full text-left">
                {[
                  'Explain 1536-dim vector search with pgvector HNSW',
                  'Audit OWASP RateLimitingFilter & HSTS security implementation',
                  'Summarize HikariCP database pool benchmark SLA',
                  'Describe Apache Tika document chunking pipeline',
                ].map((prompt, idx) => (
                  <button
                    key={idx}
                    onClick={() => setInputPrompt(prompt)}
                    className="p-3.5 rounded-xl glass-card border border-white/10 hover:border-amber-500/40 text-xs text-zinc-300 hover:text-amber-300 transition-all text-left"
                  >
                    "{prompt}"
                  </button>
                ))}
              </div>
            </div>
          )}

          <div ref={messagesEndRef} />
        </div>

        {/* Input Bar */}
        <div className="p-4 border-t border-white/10 bg-zinc-900/80">
          <div className="relative flex items-center">
            <textarea
              rows={2}
              placeholder="Ask Copilot Enterprise... (Press Ctrl + Enter to submit)"
              value={inputPrompt}
              onChange={(e) => setInputPrompt(e.target.value)}
              onKeyDown={handleKeyDown}
              className="w-full pl-4 pr-24 py-3 bg-zinc-950 border border-white/10 rounded-xl text-xs text-zinc-100 placeholder-zinc-500 focus:outline-none focus:border-amber-500/60 resize-none"
            />

            <div className="absolute right-3 flex items-center space-x-2">
              {isGenerating ? (
                <button
                  onClick={() => setIsGenerating(false)}
                  className="p-2 rounded-xl bg-red-500/20 text-red-400 border border-red-500/30 hover:bg-red-500/30 transition-colors"
                  title="Stop Generation"
                >
                  <Square className="w-4 h-4 fill-current" />
                </button>
              ) : (
                <button
                  onClick={handleSend}
                  disabled={!inputPrompt.trim()}
                  className="p-2 rounded-xl gold-gradient-bg text-zinc-950 font-bold shadow-gold-sm hover:brightness-110 disabled:opacity-40 transition-all"
                  title="Send Prompt (Ctrl + Enter)"
                >
                  <Send className="w-4 h-4 stroke-[2.5]" />
                </button>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
