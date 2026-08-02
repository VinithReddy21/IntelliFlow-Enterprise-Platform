import React from 'react';
import { PackageOpen } from 'lucide-react';

interface Props {
  title?: string;
  description?: string;
  actionLabel?: string;
  onAction?: () => void;
}

export const EmptyState: React.FC<Props> = ({
  title = 'No items found',
  description = 'There are no active records matching your current filter criteria.',
  actionLabel,
  onAction,
}) => {
  return (
    <div className="p-12 rounded-2xl glass-card border border-white/10 text-center flex flex-col items-center justify-center space-y-3 max-w-md mx-auto my-6">
      <div className="w-12 h-12 rounded-2xl gold-gradient-bg flex items-center justify-center text-zinc-950 shadow-gold-glow">
        <PackageOpen className="w-6 h-6 stroke-[2.5]" />
      </div>
      <h3 className="text-sm font-bold text-zinc-100">{title}</h3>
      <p className="text-xs text-zinc-400 leading-relaxed">{description}</p>

      {actionLabel && onAction && (
        <button
          onClick={onAction}
          className="mt-2 px-4 py-2 rounded-xl font-bold text-xs gold-gradient-bg text-zinc-950 shadow-gold-sm hover:brightness-110"
        >
          {actionLabel}
        </button>
      )}
    </div>
  );
};
