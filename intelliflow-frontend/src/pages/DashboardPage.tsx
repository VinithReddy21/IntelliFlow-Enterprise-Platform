import React from 'react';
import { CheckSquare, FileText, Search, Cpu, TrendingUp, Sparkles, Plus } from 'lucide-react';

export const DashboardPage: React.FC = () => {
  const metrics = [
    { title: 'Active Workflows', value: '24 Tasks', change: '+12% this week', icon: CheckSquare },
    { title: 'Ingested Documents', value: '142 Files', change: '1536-dim embeddings', icon: FileText },
    { title: 'Vector Search SLA', value: '14 ms', change: 'HNSW pgvector index', icon: Search },
    { title: 'AI RAG Queries', value: '1,280', change: '99.4% confidence rate', icon: Cpu },
  ];

  return (
    <div className="space-y-8">
      {/* Welcome Banner */}
      <div className="flex flex-col md:flex-row items-start md:items-center justify-between p-6 rounded-2xl glass-card-gold border border-amber-500/30">
        <div>
          <div className="inline-flex items-center space-x-2 text-amber-400 text-xs font-semibold uppercase tracking-wider mb-2">
            <Sparkles className="w-4 h-4" />
            <span>Platform Dashboard</span>
          </div>
          <h1 className="text-2xl font-bold text-zinc-100">Enterprise RAG & Task Command Center</h1>
          <p className="text-xs text-zinc-400 mt-1">Real-time telemetry, vector similarity search, and automated workflows.</p>
        </div>

        <div className="mt-4 md:mt-0 flex items-center space-x-3">
          <button className="px-4 py-2 rounded-xl text-xs font-semibold glass-card border border-white/10 text-zinc-200 hover:border-amber-500/40 transition-colors flex items-center space-x-2">
            <FileText className="w-3.5 h-3.5" />
            <span>Upload Document</span>
          </button>
          <button className="px-4 py-2 rounded-xl text-xs font-bold gold-gradient-bg text-zinc-950 shadow-gold-sm hover:brightness-110 transition-all flex items-center space-x-2">
            <Plus className="w-4 h-4 stroke-[3]" />
            <span>Create Task</span>
          </button>
        </div>
      </div>

      {/* Metrics Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
        {metrics.map((metric, idx) => {
          const Icon = metric.icon;
          return (
            <div key={idx} className="p-5 rounded-2xl glass-card border border-white/10 hover:border-amber-500/30 transition-all group">
              <div className="flex items-center justify-between">
                <span className="text-xs font-medium text-zinc-400">{metric.title}</span>
                <div className="p-2 rounded-xl bg-amber-500/10 text-amber-400 group-hover:bg-amber-500/20 transition-colors">
                  <Icon className="w-4 h-4" />
                </div>
              </div>
              <div className="mt-3 text-2xl font-extrabold text-zinc-100">{metric.value}</div>
              <div className="mt-1 text-[11px] text-amber-400/80 flex items-center space-x-1">
                <TrendingUp className="w-3 h-3" />
                <span>{metric.change}</span>
              </div>
            </div>
          );
        })}
      </div>

      {/* Main Grid Section */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Active Tasks Overview */}
        <div className="lg:col-span-2 p-6 rounded-2xl glass-card border border-white/10 space-y-4">
          <div className="flex items-center justify-between border-b border-white/10 pb-4">
            <h3 className="text-sm font-bold text-zinc-100">Priority Workflow Executions</h3>
            <span className="text-xs text-amber-400 cursor-pointer hover:underline">View All Tasks →</span>
          </div>

          <div className="space-y-3">
            {[
              { title: 'Deploy pgvector HNSW Index Optimization', status: 'IN_PROGRESS', priority: 'CRITICAL', time: '2 hours ago' },
              { title: 'Validate OWASP RateLimitingFilter Integration', status: 'COMPLETED', priority: 'HIGH', time: '4 hours ago' },
              { title: 'Tika Multi-part File Ingestion Pipeline Audit', status: 'TODO', priority: 'MEDIUM', time: '6 hours ago' },
            ].map((task, i) => (
              <div key={i} className="p-4 rounded-xl bg-zinc-900/60 border border-white/5 flex items-center justify-between hover:border-amber-500/20 transition-colors">
                <div>
                  <h4 className="text-xs font-semibold text-zinc-200">{task.title}</h4>
                  <span className="text-[10px] text-zinc-500">{task.time}</span>
                </div>
                <div className="flex items-center space-x-2">
                  <span className="px-2 py-0.5 text-[9px] font-bold rounded-full bg-amber-500/10 text-amber-400 border border-amber-500/20">
                    {task.priority}
                  </span>
                  <span className="px-2 py-0.5 text-[9px] font-bold rounded-full bg-emerald-500/10 text-emerald-400 border border-emerald-500/20">
                    {task.status}
                  </span>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Vector Engine Health */}
        <div className="p-6 rounded-2xl glass-card border border-white/10 space-y-4">
          <h3 className="text-sm font-bold text-zinc-100 border-b border-white/10 pb-4">AI Vector Engine Telemetry</h3>
          <div className="space-y-4">
            <div>
              <div className="flex justify-between text-xs text-zinc-300 mb-1">
                <span>Vector Index Warmup</span>
                <span className="text-amber-400 font-bold">98%</span>
              </div>
              <div className="w-full h-2 bg-zinc-800 rounded-full overflow-hidden">
                <div className="h-full bg-amber-400 rounded-full w-[98%]"></div>
              </div>
            </div>

            <div>
              <div className="flex justify-between text-xs text-zinc-300 mb-1">
                <span>HikariCP Pool Connections</span>
                <span className="text-amber-400 font-bold">10 / 30 Active</span>
              </div>
              <div className="w-full h-2 bg-zinc-800 rounded-full overflow-hidden">
                <div className="h-full bg-amber-400 rounded-full w-[33%]"></div>
              </div>
            </div>

            <div className="p-4 rounded-xl bg-amber-500/10 border border-amber-500/20 text-xs text-amber-300">
              <p className="font-semibold mb-1">pgvector HNSW Status</p>
              <p className="text-[11px] text-amber-400/80">1536-dimensional embeddings indexed with zero latency spikes.</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
