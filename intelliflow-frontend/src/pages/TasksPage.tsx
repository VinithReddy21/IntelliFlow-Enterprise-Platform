import React, { useState } from 'react';
import {
  useTasks,
  useCreateTask,
  useUpdateTask,
  useUpdateTaskStatus,
  useDeleteTask,
} from '../features/tasks/hooks/useTasks';
import { TaskItem, TaskPriority, TaskStatus } from '../features/tasks/types/task';
import { TaskKanbanBoard } from '../features/tasks/components/TaskKanbanBoard';
import { TaskDataTable } from '../features/tasks/components/TaskDataTable';
import { TaskDetailsDrawer } from '../features/tasks/components/TaskDetailsDrawer';
import { CreateTaskModal } from '../features/tasks/components/CreateTaskModal';
import { EditTaskModal } from '../features/tasks/components/EditTaskModal';
import { DeleteTaskDialog } from '../features/tasks/components/DeleteTaskDialog';
import { LayoutGrid, Table, Search, Plus, Sparkles, Filter } from 'lucide-react';

export const TasksPage: React.FC = () => {
  const { data: tasks = [], isLoading } = useTasks();
  const createTaskMutation = useCreateTask();
  const updateTaskMutation = useUpdateTask();
  const updateStatusMutation = useUpdateTaskStatus();
  const deleteTaskMutation = useDeleteTask();

  const [viewMode, setViewMode] = useState<'kanban' | 'table'>('kanban');
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState<TaskStatus | 'ALL'>('ALL');
  const [priorityFilter, setPriorityFilter] = useState<TaskPriority | 'ALL'>('ALL');

  const [selectedTask, setSelectedTask] = useState<TaskItem | null>(null);
  const [editingTask, setEditingTask] = useState<TaskItem | null>(null);
  const [deletingTask, setDeletingTask] = useState<TaskItem | null>(null);
  const [isCreateOpen, setIsCreateOpen] = useState(false);

  // Filter Tasks
  const filteredTasks = tasks.filter((task) => {
    const matchesSearch =
      task.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
      task.description.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesStatus = statusFilter === 'ALL' || task.status === statusFilter;
    const matchesPriority = priorityFilter === 'ALL' || task.priority === priorityFilter;
    return matchesSearch && matchesStatus && matchesPriority;
  });

  return (
    <div className="space-y-6">
      {/* Header & Controls */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <div className="inline-flex items-center space-x-2 text-amber-400 text-xs font-semibold uppercase tracking-wider mb-1">
            <Sparkles className="w-3.5 h-3.5" />
            <span>Workflow Workspace</span>
          </div>
          <h1 className="text-2xl font-bold text-zinc-100">Enterprise Task Matrix</h1>
        </div>

        <div className="flex items-center space-x-3">
          {/* View Mode Toggle */}
          <div className="flex p-1 rounded-xl glass-card border border-white/10 text-xs font-medium">
            <button
              onClick={() => setViewMode('kanban')}
              className={`flex items-center space-x-1.5 px-3 py-1.5 rounded-lg transition-all ${
                viewMode === 'kanban'
                  ? 'bg-amber-500/20 text-amber-300 border border-amber-500/30 shadow-gold-sm'
                  : 'text-zinc-400 hover:text-zinc-200'
              }`}
            >
              <LayoutGrid className="w-3.5 h-3.5" />
              <span>Kanban</span>
            </button>
            <button
              onClick={() => setViewMode('table')}
              className={`flex items-center space-x-1.5 px-3 py-1.5 rounded-lg transition-all ${
                viewMode === 'table'
                  ? 'bg-amber-500/20 text-amber-300 border border-amber-500/30 shadow-gold-sm'
                  : 'text-zinc-400 hover:text-zinc-200'
              }`}
            >
              <Table className="w-3.5 h-3.5" />
              <span>Table</span>
            </button>
          </div>

          <button
            onClick={() => setIsCreateOpen(true)}
            className="px-4 py-2.5 rounded-xl font-bold text-xs gold-gradient-bg text-zinc-950 shadow-gold-sm hover:brightness-110 transition-all flex items-center space-x-2"
          >
            <Plus className="w-4 h-4 stroke-[3]" />
            <span>Create Task</span>
          </button>
        </div>
      </div>

      {/* Search & Filters Toolbar */}
      <div className="p-4 rounded-2xl glass-card border border-white/10 flex flex-col md:flex-row items-center justify-between gap-4">
        {/* Search */}
        <div className="relative w-full md:w-80">
          <Search className="w-4 h-4 text-zinc-400 absolute left-3 top-1/2 -translate-y-1/2" />
          <input
            type="text"
            placeholder="Search task title or description..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full pl-9 pr-4 py-2 bg-zinc-900/80 border border-white/10 rounded-xl text-xs text-zinc-200 placeholder-zinc-500 focus:outline-none focus:border-amber-500/60"
          />
        </div>

        {/* Filters */}
        <div className="flex items-center space-x-3 w-full md:w-auto text-xs">
          <div className="flex items-center space-x-1.5 text-zinc-400">
            <Filter className="w-3.5 h-3.5" />
            <span>Filter:</span>
          </div>

          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value as any)}
            className="px-3 py-2 bg-zinc-900 border border-white/10 rounded-xl text-zinc-300 focus:outline-none focus:border-amber-500/60"
          >
            <option value="ALL">All Statuses</option>
            <option value="BACKLOG">Backlog</option>
            <option value="TODO">To Do</option>
            <option value="IN_PROGRESS">In Progress</option>
            <option value="BLOCKED">Blocked</option>
            <option value="IN_REVIEW">In Review</option>
            <option value="COMPLETED">Completed</option>
            <option value="ARCHIVED">Archived</option>
          </select>

          <select
            value={priorityFilter}
            onChange={(e) => setPriorityFilter(e.target.value as any)}
            className="px-3 py-2 bg-zinc-900 border border-white/10 rounded-xl text-zinc-300 focus:outline-none focus:border-amber-500/60"
          >
            <option value="ALL">All Priorities</option>
            <option value="LOW">Low</option>
            <option value="MEDIUM">Medium</option>
            <option value="HIGH">High</option>
            <option value="CRITICAL">Critical</option>
          </select>
        </div>
      </div>

      {/* Main View Area */}
      {isLoading ? (
        <div className="h-64 flex items-center justify-center">
          <div className="w-10 h-10 border-4 border-amber-400 border-t-transparent rounded-full animate-spin"></div>
        </div>
      ) : viewMode === 'kanban' ? (
        <TaskKanbanBoard
          tasks={filteredTasks}
          onSelectTask={setSelectedTask}
          onEditTask={setEditingTask}
          onDeleteTask={setDeletingTask}
          onUpdateStatus={(id, status) => updateStatusMutation.mutate({ id, status })}
        />
      ) : (
        <TaskDataTable
          tasks={filteredTasks}
          onSelectTask={setSelectedTask}
          onEditTask={setEditingTask}
          onDeleteTask={setDeletingTask}
        />
      )}

      {/* Modals and Drawers */}
      <TaskDetailsDrawer
        task={selectedTask}
        onClose={() => setSelectedTask(null)}
        onUpdateStatus={(id, status) => {
          updateStatusMutation.mutate({ id, status });
          if (selectedTask) setSelectedTask({ ...selectedTask, status });
        }}
      />

      <CreateTaskModal
        isOpen={isCreateOpen}
        onClose={() => setIsCreateOpen(false)}
        onSubmit={(data) => createTaskMutation.mutate(data)}
      />

      <EditTaskModal
        task={editingTask}
        isOpen={Boolean(editingTask)}
        onClose={() => setEditingTask(null)}
        onSubmit={(id, data) => updateTaskMutation.mutate({ id, input: data })}
      />

      <DeleteTaskDialog
        task={deletingTask}
        isOpen={Boolean(deletingTask)}
        onClose={() => setDeletingTask(null)}
        onConfirm={(id) => deleteTaskMutation.mutate(id)}
      />
    </div>
  );
};
