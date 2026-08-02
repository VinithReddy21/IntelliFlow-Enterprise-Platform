import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Zap, Lock, Mail, ArrowRight } from 'lucide-react';

export const LoginPage: React.FC = () => {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [email, setEmail] = useState('alex.architect@intelliflow.ai');
  const [password, setPassword] = useState('Password123!');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    login('mock-jwt-token-123456789', {
      id: 'b5c3c6b2-0f7c-4490-82ae-e7d99d3b0816',
      username: 'alex.architect',
      email,
      role: 'ROLE_ADMIN',
      status: 'ACTIVE',
    });
    navigate('/dashboard');
  };

  return (
    <div className="min-h-screen bg-background flex items-center justify-center p-6 relative overflow-hidden">
      <div className="w-full max-w-md p-8 glass-card border border-white/10 rounded-2xl shadow-2xl relative z-10">
        <div className="text-center mb-8">
          <div className="w-12 h-12 rounded-xl gold-gradient-bg mx-auto flex items-center justify-center shadow-gold-glow mb-4">
            <Zap className="w-7 h-7 text-zinc-950 stroke-[2.5]" />
          </div>
          <h2 className="text-2xl font-bold gold-gradient-text">Welcome to IntelliFlow</h2>
          <p className="text-xs text-zinc-400 mt-1">Sign in with your enterprise credentials</p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-5">
          <div>
            <label className="block text-xs font-semibold text-zinc-300 mb-1.5">Email Address</label>
            <div className="relative">
              <Mail className="w-4 h-4 text-zinc-400 absolute left-3 top-1/2 -translate-y-1/2" />
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
                className="w-full pl-9 pr-4 py-2.5 bg-zinc-900/80 border border-white/10 rounded-xl text-xs text-zinc-100 placeholder-zinc-500 focus:outline-none focus:border-amber-500/60 transition-colors"
              />
            </div>
          </div>

          <div>
            <label className="block text-xs font-semibold text-zinc-300 mb-1.5">Password</label>
            <div className="relative">
              <Lock className="w-4 h-4 text-zinc-400 absolute left-3 top-1/2 -translate-y-1/2" />
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
                className="w-full pl-9 pr-4 py-2.5 bg-zinc-900/80 border border-white/10 rounded-xl text-xs text-zinc-100 placeholder-zinc-500 focus:outline-none focus:border-amber-500/60 transition-colors"
              />
            </div>
          </div>

          <button
            type="submit"
            className="w-full py-3 rounded-xl font-bold text-xs gold-gradient-bg text-zinc-950 shadow-gold-glow hover:brightness-110 transition-all flex items-center justify-center space-x-2"
          >
            <span>Sign In to Dashboard</span>
            <ArrowRight className="w-4 h-4" />
          </button>
        </form>
      </div>
    </div>
  );
};
