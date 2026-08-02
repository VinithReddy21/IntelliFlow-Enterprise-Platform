import React, { useState } from 'react';
import { Users, Shield, Activity, FileCode, CheckCircle2, Sparkles, UserX, UserCheck } from 'lucide-react';

export const AdminPage: React.FC = () => {
  const [activeTab, setActiveTab] = useState<'users' | 'rbac' | 'health' | 'audit'>('users');

  const [users, setUsers] = useState([
    { id: 'usr-1', username: 'alex.architect', email: 'alex.architect@intelliflow.ai', role: 'ROLE_ADMIN', status: 'ACTIVE' },
    { id: 'usr-2', name: 'david.lead', username: 'david.lead', email: 'david.lead@intelliflow.ai', role: 'ROLE_MANAGER', status: 'ACTIVE' },
    { id: 'usr-3', name: 'elena.secops', username: 'elena.secops', email: 'elena.secops@intelliflow.ai', role: 'ROLE_MEMBER', status: 'SUSPENDED' },
  ]);

  const toggleUserStatus = (id: string) => {
    setUsers((prev) =>
      prev.map((u) => (u.id === id ? { ...u, status: u.status === 'ACTIVE' ? 'SUSPENDED' : 'ACTIVE' } : u))
    );
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <div className="inline-flex items-center space-x-2 text-amber-400 text-xs font-semibold uppercase tracking-wider mb-1">
            <Sparkles className="w-3.5 h-3.5" />
            <span>Platform Governance</span>
          </div>
          <h1 className="text-2xl font-bold text-zinc-100">Enterprise Admin Console</h1>
        </div>
      </div>

      <div className="flex border-b border-white/10 glass-card px-4 rounded-xl text-xs font-semibold text-zinc-400 overflow-x-auto">
        <button
          onClick={() => setActiveTab('users')}
          className={`py-3 px-4 border-b-2 transition-colors whitespace-nowrap flex items-center space-x-2 ${
            activeTab === 'users' ? 'border-amber-400 text-amber-400' : 'border-transparent hover:text-zinc-200'
          }`}
        >
          <Users className="w-4 h-4" />
          <span>User Directory</span>
        </button>

        <button
          onClick={() => setActiveTab('rbac')}
          className={`py-3 px-4 border-b-2 transition-colors whitespace-nowrap flex items-center space-x-2 ${
            activeTab === 'rbac' ? 'border-amber-400 text-amber-400' : 'border-transparent hover:text-zinc-200'
          }`}
        >
          <Shield className="w-4 h-4" />
          <span>RBAC Matrix</span>
        </button>

        <button
          onClick={() => setActiveTab('health')}
          className={`py-3 px-4 border-b-2 transition-colors whitespace-nowrap flex items-center space-x-2 ${
            activeTab === 'health' ? 'border-amber-400 text-amber-400' : 'border-transparent hover:text-zinc-200'
          }`}
        >
          <Activity className="w-4 h-4" />
          <span>Health Telemetry</span>
        </button>

        <button
          onClick={() => setActiveTab('audit')}
          className={`py-3 px-4 border-b-2 transition-colors whitespace-nowrap flex items-center space-x-2 ${
            activeTab === 'audit' ? 'border-amber-400 text-amber-400' : 'border-transparent hover:text-zinc-200'
          }`}
        >
          <FileCode className="w-4 h-4" />
          <span>Audit Logs</span>
        </button>
      </div>

      {activeTab === 'users' && (
        <div className="rounded-2xl glass-card border border-white/10 overflow-hidden shadow-2xl">
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="border-b border-white/10 bg-zinc-900/80 text-[11px] font-semibold text-zinc-400 uppercase tracking-wider">
                  <th className="p-4">User</th>
                  <th className="p-4">Role</th>
                  <th className="p-4">Status</th>
                  <th className="p-4 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-white/5 text-xs text-zinc-300">
                {users.map((u) => (
                  <tr key={u.id} className="hover:bg-white/5 transition-colors">
                    <td className="p-4 font-bold text-zinc-100">{u.username}</td>
                    <td className="p-4 font-mono text-amber-400 text-[11px]">{u.role}</td>
                    <td className="p-4">
                      <span
                        className={`px-2.5 py-0.5 rounded-full text-[10px] font-bold border ${
                          u.status === 'ACTIVE'
                            ? 'bg-emerald-500/10 text-emerald-400 border-emerald-500/30'
                            : 'bg-red-500/15 text-red-400 border-red-500/40'
                        }`}
                      >
                        {u.status}
                      </span>
                    </td>
                    <td className="p-4 text-right">
                      <button
                        onClick={() => toggleUserStatus(u.id)}
                        className="px-3 py-1 rounded-lg glass-card border border-white/10 hover:border-amber-500/40 transition-colors flex items-center space-x-1 ml-auto text-[11px]"
                      >
                        {u.status === 'ACTIVE' ? <UserX className="w-3.5 h-3.5 text-red-400" /> : <UserCheck className="w-3.5 h-3.5 text-emerald-400" />}
                        <span>{u.status === 'ACTIVE' ? 'Suspend' : 'Activate'}</span>
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {activeTab === 'health' && (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
          {[
            { name: 'PostgreSQL Database', status: 'HEALTHY', latency: '2 ms', desc: 'HikariCP 10/30 Active' },
            { name: 'Redis Cache Layer', status: 'HEALTHY', latency: '1 ms', desc: 'Ping/Pong Response OK' },
            { name: 'OpenAI AI Engine', status: 'HEALTHY', latency: '18 ms', desc: '1536-dim Embedding Active' },
            { name: 'File Storage Service', status: 'HEALTHY', latency: '4 ms', desc: 'Local Storage Verified' },
          ].map((item, idx) => (
            <div key={idx} className="p-5 rounded-2xl glass-card border border-white/10 space-y-2">
              <div className="flex items-center justify-between">
                <span className="text-xs font-bold text-zinc-200">{item.name}</span>
                <CheckCircle2 className="w-4 h-4 text-emerald-400" />
              </div>
              <div className="text-sm font-extrabold text-emerald-400">{item.status}</div>
              <div className="text-[10px] text-zinc-500 font-mono">{item.desc} • {item.latency}</div>
            </div>
          ))}
        </div>
      )}

      {activeTab === 'audit' && (
        <div className="p-4 rounded-2xl glass-card border border-white/10 space-y-3">
          <h3 className="text-xs font-bold text-zinc-200">Domain Audit Trail</h3>
          <div className="space-y-2 font-mono text-[11px] text-zinc-400">
            <div className="p-3 rounded-xl bg-zinc-950/80 border border-white/5">
              [2026-08-02 21:30:00] AUDIT_EVENT: [Type: USER_CREATED, ID: 00262056-ee10-431f-9132-d043c0a9cc65]
            </div>
            <div className="p-3 rounded-xl bg-zinc-950/80 border border-white/5">
              [2026-08-02 21:35:00] AUDIT_EVENT: [Type: DOCUMENT_UPLOADED, Checksum: e3b0c44298fc1c14...]
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
