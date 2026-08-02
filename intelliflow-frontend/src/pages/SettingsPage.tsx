import React, { useState } from 'react';
import { Sliders, Cpu, Bell, Shield, Palette } from 'lucide-react';

export const SettingsPage: React.FC = () => {
  const [activeTab, setActiveTab] = useState<'ai' | 'notifications' | 'appearance' | 'privacy'>('ai');
  const [temperature, setTemperature] = useState(0.2);
  const [vectorModel, setVectorModel] = useState('text-embedding-3-small');
  const [emailNotifications, setEmailNotifications] = useState(true);

  return (
    <div className="max-w-4xl space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-zinc-100">Platform Settings</h1>
        <p className="text-xs text-zinc-400 mt-1">Configure AI engine preferences, theme appearance, and notification channels.</p>
      </div>

      {/* Settings Navigation Tabs */}
      <div className="flex border-b border-white/10 glass-card px-4 rounded-xl text-xs font-semibold text-zinc-400">
        <button
          onClick={() => setActiveTab('ai')}
          className={`py-3 px-4 border-b-2 transition-colors flex items-center space-x-2 ${
            activeTab === 'ai' ? 'border-amber-400 text-amber-400' : 'border-transparent hover:text-zinc-200'
          }`}
        >
          <Cpu className="w-4 h-4" />
          <span>AI Engine & RAG</span>
        </button>

        <button
          onClick={() => setActiveTab('notifications')}
          className={`py-3 px-4 border-b-2 transition-colors flex items-center space-x-2 ${
            activeTab === 'notifications' ? 'border-amber-400 text-amber-400' : 'border-transparent hover:text-zinc-200'
          }`}
        >
          <Bell className="w-4 h-4" />
          <span>Notifications</span>
        </button>

        <button
          onClick={() => setActiveTab('appearance')}
          className={`py-3 px-4 border-b-2 transition-colors flex items-center space-x-2 ${
            activeTab === 'appearance' ? 'border-amber-400 text-amber-400' : 'border-transparent hover:text-zinc-200'
          }`}
        >
          <Palette className="w-4 h-4" />
          <span>Appearance</span>
        </button>

        <button
          onClick={() => setActiveTab('privacy')}
          className={`py-3 px-4 border-b-2 transition-colors flex items-center space-x-2 ${
            activeTab === 'privacy' ? 'border-amber-400 text-amber-400' : 'border-transparent hover:text-zinc-200'
          }`}
        >
          <Shield className="w-4 h-4" />
          <span>Privacy & API</span>
        </button>
      </div>

      {/* Tab Panels */}
      <div className="p-6 rounded-2xl glass-card border border-white/10 space-y-6 text-xs">
        {activeTab === 'ai' && (
          <div className="space-y-5">
            <h3 className="text-sm font-bold text-zinc-100 flex items-center gap-2">
              <Sliders className="w-4 h-4 text-amber-400" /> Vector Engine & Model Preferences
            </h3>

            <div className="space-y-2">
              <label className="block font-semibold text-zinc-300">Default Embedding Model</label>
              <select
                value={vectorModel}
                onChange={(e) => setVectorModel(e.target.value)}
                className="w-full px-3.5 py-2.5 bg-zinc-900 border border-white/10 rounded-xl text-zinc-200 font-mono focus:outline-none focus:border-amber-500/60"
              >
                <option value="text-embedding-3-small">text-embedding-3-small (1536-dim)</option>
                <option value="text-embedding-3-large">text-embedding-3-large (3072-dim)</option>
              </select>
            </div>

            <div className="space-y-2">
              <div className="flex justify-between font-semibold text-zinc-300">
                <span>RAG Generation Temperature</span>
                <span className="font-mono text-amber-400 font-bold">{temperature}</span>
              </div>
              <input
                type="range"
                min="0.0"
                max="1.0"
                step="0.05"
                value={temperature}
                onChange={(e) => setTemperature(parseFloat(e.target.value))}
                className="w-full accent-amber-400 cursor-pointer"
              />
              <p className="text-[10px] text-zinc-500">Lower values guarantee deterministic, strict grounded answers.</p>
            </div>
          </div>
        )}

        {activeTab === 'notifications' && (
          <div className="space-y-4">
            <h3 className="text-sm font-bold text-zinc-100 border-b border-white/10 pb-3">Notification Subscriptions</h3>
            <div className="p-4 rounded-xl bg-zinc-900/60 border border-white/5 flex items-center justify-between">
              <div>
                <h4 className="font-bold text-zinc-200">Email Digest Notifications</h4>
                <p className="text-[11px] text-zinc-400">Receive daily summary digests of task updates and document ingestion events.</p>
              </div>
              <button
                onClick={() => setEmailNotifications(!emailNotifications)}
                className={`w-11 h-6 rounded-full transition-colors relative p-1 ${
                  emailNotifications ? 'bg-amber-400' : 'bg-zinc-800'
                }`}
              >
                <div
                  className={`w-4 h-4 rounded-full bg-zinc-950 transition-transform ${
                    emailNotifications ? 'translate-x-5' : 'translate-x-0'
                  }`}
                ></div>
              </button>
            </div>
          </div>
        )}

        {activeTab === 'appearance' && (
          <div className="space-y-4">
            <h3 className="text-sm font-bold text-zinc-100 border-b border-white/10 pb-3">Theme & Aesthetics</h3>
            <div className="p-4 rounded-xl glass-card-gold border border-amber-500/30 flex items-center justify-between">
              <div>
                <h4 className="font-bold text-zinc-100">Black + Gold Metallic Theme</h4>
                <p className="text-[11px] text-amber-400/80">Active default enterprise SaaS theme with backdrop glassmorphism.</p>
              </div>
              <span className="px-3 py-1 rounded-full bg-amber-500/20 text-amber-300 font-bold text-[10px]">ACTIVE</span>
            </div>
          </div>
        )}

        {activeTab === 'privacy' && (
          <div className="space-y-4">
            <h3 className="text-sm font-bold text-zinc-100 border-b border-white/10 pb-3">Security & API Keys</h3>
            <div className="p-4 rounded-xl bg-zinc-900/80 border border-white/5 font-mono text-[11px] text-zinc-400 space-y-2">
              <span className="block text-zinc-500">JWT Signing Strategy</span>
              <span className="text-amber-400">HMAC-SHA256 (256-bit entropy)</span>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};
