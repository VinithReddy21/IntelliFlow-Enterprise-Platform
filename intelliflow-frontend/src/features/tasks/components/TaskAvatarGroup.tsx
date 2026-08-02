import React from 'react';
import { TaskUser } from '../types/task';

interface Props {
  assignee?: TaskUser;
  creator?: TaskUser;
}

export const TaskAvatarGroup: React.FC<Props> = ({ assignee, creator }) => {
  return (
    <div className="flex items-center -space-x-2">
      {assignee && (
        <div
          title={`Assignee: ${assignee.name}`}
          className="w-7 h-7 rounded-full gold-gradient-bg flex items-center justify-center text-zinc-950 font-extrabold text-[10px] ring-2 ring-zinc-950 shadow-gold-sm"
        >
          {assignee.name.substring(0, 2).toUpperCase()}
        </div>
      )}
      {creator && (
        <div
          title={`Creator: ${creator.name}`}
          className="w-7 h-7 rounded-full bg-zinc-800 border border-white/20 flex items-center justify-center text-zinc-300 font-bold text-[10px] ring-2 ring-zinc-950"
        >
          {creator.name.substring(0, 2).toUpperCase()}
        </div>
      )}
    </div>
  );
};
