import React from 'react';

export const SkeletonLoader: React.FC<{ count?: number }> = ({ count = 3 }) => {
  return (
    <div className="space-y-3">
      {Array.from({ length: count }).map((_, idx) => (
        <div key={idx} className="p-4 rounded-xl glass-card border border-white/5 animate-pulse space-y-2">
          <div className="h-4 bg-zinc-800 rounded w-1/3"></div>
          <div className="h-3 bg-zinc-900 rounded w-3/4"></div>
          <div className="h-3 bg-zinc-900 rounded w-1/2"></div>
        </div>
      ))}
    </div>
  );
};
