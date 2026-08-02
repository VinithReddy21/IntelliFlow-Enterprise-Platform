import React, { useState } from 'react';
import { ChatMessage } from '../types/ai';
import { SourceCitationPanel } from './SourceCitationPanel';
import { AiConfidenceMeter } from './AiConfidenceMeter';
import { Bot, User, Copy, Check, RotateCcw } from 'lucide-react';

interface Props {
  message: ChatMessage;
  onRegenerate?: () => void;
}

export const ChatMessageItem: React.FC<Props> = ({ message, onRegenerate }) => {
  const [copied, setCopied] = useState(false);
  const isUser = message.sender === 'user';

  const handleCopy = () => {
    navigator.clipboard.writeText(message.content);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  return (
    <div className={`flex gap-4 p-5 rounded-2xl transition-all ${isUser ? 'bg-zinc-900/60 border border-white/5' : 'glass-card-gold border border-amber-500/20 shadow-lg'}`}>
      {/* Avatar Icon */}
      <div className={`w-9 h-9 rounded-xl flex items-center justify-center flex-shrink-0 ${isUser ? 'bg-zinc-800 text-zinc-300' : 'gold-gradient-bg text-zinc-950 font-bold shadow-gold-glow'}`}>
        {isUser ? <User className="w-5 h-5" /> : <Bot className="w-5 h-5 stroke-[2.5]" />}
      </div>

      {/* Message Body */}
      <div className="flex-1 space-y-3 min-w-0">
        <div className="flex items-center justify-between">
          <span className={`text-xs font-bold ${isUser ? 'text-zinc-300' : 'gold-gradient-text'}`}>
            {isUser ? 'User Specification' : 'IntelliFlow Copilot Enterprise'}
          </span>
          <span className="text-[10px] text-zinc-500 font-mono">{message.timestamp}</span>
        </div>

        {/* Text Content */}
        <div className="text-xs text-zinc-200 leading-relaxed font-sans space-y-2 whitespace-pre-wrap">
          {message.content}
        </div>

        {/* Confidence Meter & Citations for Assistant */}
        {!isUser && (
          <div className="space-y-4 pt-2">
            {message.confidenceScore && (
              <AiConfidenceMeter
                confidenceScore={message.confidenceScore}
                latencyMs={message.latencyMs}
                citationCount={message.citations?.length}
              />
            )}

            <SourceCitationPanel citations={message.citations} />

            {/* Bottom Actions */}
            <div className="flex items-center justify-between pt-2 border-t border-white/5 text-xs text-zinc-400">
              <div className="flex items-center space-x-2">
                <button
                  onClick={handleCopy}
                  className="flex items-center space-x-1 hover:text-amber-400 transition-colors p-1"
                >
                  {copied ? <Check className="w-3.5 h-3.5 text-emerald-400" /> : <Copy className="w-3.5 h-3.5" />}
                  <span>{copied ? 'Copied' : 'Copy'}</span>
                </button>

                {onRegenerate && (
                  <button
                    onClick={onRegenerate}
                    className="flex items-center space-x-1 hover:text-amber-400 transition-colors p-1 ml-2"
                  >
                    <RotateCcw className="w-3.5 h-3.5" />
                    <span>Regenerate Response</span>
                  </button>
                )}
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};
