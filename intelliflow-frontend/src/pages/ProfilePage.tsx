import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { User, Shield, Key, History, Smartphone, Check } from 'lucide-react';

export const ProfilePage: React.FC = () => {
  const { user } = useAuth();
  const [username, setUsername] = useState(user?.username || 'alex.architect');
  const [email, setEmail] = useState(user?.email || 'alex.architect@intelliflow.ai');
  const [mfaEnabled, setMfaEnabled] = useState(true);
  const [saved, setSaved] = useState(false);

  const handleSaveProfile = (e: React.FormEvent) => {
    e.preventDefault();
    setSaved(true);
    setTimeout(() => setSaved(false), 2000);
  };

  return (
    <div className="max-w-4xl space-y-8">
      {/* Profile Header */}
      <div className="p-6 rounded-2xl glass-card-gold border border-amber-500/30 flex items-center space-x-4">
        <div className="w-16 h-16 rounded-2xl gold-gradient-bg flex items-center justify-center text-zinc-950 font-extrabold text-xl ring-4 ring-amber-500/30 shadow-gold-glow">
          {user?.username?.substring(0, 2).toUpperCase() || 'EX'}
        </div>
        <div>
          <h1 className="text-xl font-bold text-zinc-100">{user?.username}</h1>
          <p className="text-xs text-zinc-400 font-mono mt-0.5">{user?.email}</p>
          <div className="mt-2 inline-flex items-center space-x-1.5 px-2.5 py-0.5 rounded-full bg-amber-500/10 border border-amber-500/30 text-amber-400 text-[10px] font-bold">
            <Shield className="w-3 h-3" />
            <span>ROLE: {user?.role?.replace('ROLE_', '')}</span>
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {/* Personal Details Form */}
        <form onSubmit={handleSaveProfile} className="p-6 rounded-2xl glass-card border border-white/10 space-y-4">
          <h3 className="text-sm font-bold text-zinc-100 flex items-center gap-2 border-b border-white/10 pb-3">
            <User className="w-4 h-4 text-amber-400" /> Account Identity
          </h3>

          <div>
            <label className="block text-xs font-semibold text-zinc-300 mb-1">Username</label>
            <input
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              className="w-full px-3.5 py-2.5 bg-zinc-900 border border-white/10 rounded-xl text-xs text-zinc-100 focus:outline-none focus:border-amber-500/60"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-zinc-300 mb-1">Email Address</label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="w-full px-3.5 py-2.5 bg-zinc-900 border border-white/10 rounded-xl text-xs text-zinc-100 focus:outline-none focus:border-amber-500/60"
            />
          </div>

          <button
            type="submit"
            className="w-full py-2.5 rounded-xl font-bold text-xs gold-gradient-bg text-zinc-950 shadow-gold-sm hover:brightness-110 flex items-center justify-center space-x-1"
          >
            {saved ? <Check className="w-4 h-4" /> : null}
            <span>{saved ? 'Changes Saved' : 'Update Profile'}</span>
          </button>
        </form>

        {/* Password & MFA Security */}
        <div className="p-6 rounded-2xl glass-card border border-white/10 space-y-5">
          <h3 className="text-sm font-bold text-zinc-100 flex items-center gap-2 border-b border-white/10 pb-3">
            <Key className="w-4 h-4 text-amber-400" /> Security & Authentication
          </h3>

          {/* MFA Toggle */}
          <div className="p-4 rounded-xl bg-zinc-900/60 border border-white/5 flex items-center justify-between">
            <div>
              <h4 className="text-xs font-bold text-zinc-200 flex items-center gap-1.5">
                <Smartphone className="w-4 h-4 text-amber-400" /> Two-Factor Authentication (2FA)
              </h4>
              <p className="text-[11px] text-zinc-400 mt-0.5">TOTP authenticator app verification</p>
            </div>
            <button
              onClick={() => setMfaEnabled(!mfaEnabled)}
              className={`w-11 h-6 rounded-full transition-colors relative p-1 ${
                mfaEnabled ? 'bg-amber-400' : 'bg-zinc-800'
              }`}
            >
              <div
                className={`w-4 h-4 rounded-full bg-zinc-950 transition-transform ${
                  mfaEnabled ? 'translate-x-5' : 'translate-x-0'
                }`}
              ></div>
            </button>
          </div>

          {/* Login History */}
          <div className="space-y-2">
            <h4 className="text-xs font-bold text-zinc-300 flex items-center gap-1.5">
              <History className="w-3.5 h-3.5 text-amber-400" /> Active Sessions
            </h4>
            <div className="p-3 rounded-xl bg-zinc-900/40 border border-white/5 text-xs flex justify-between items-center">
              <div>
                <div className="font-bold text-zinc-200">Chrome / Windows 11</div>
                <div className="text-[10px] text-zinc-500 font-mono">127.0.0.1 • Active Now</div>
              </div>
              <span className="text-[10px] text-emerald-400 font-bold font-mono">THIS DEVICE</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
