import React from 'react';
import { Link } from 'react-router-dom';
import { Zap, ShieldCheck, Cpu, Database, ArrowRight } from 'lucide-react';

export const LandingPage: React.FC = () => {
  return (
    <div className="min-h-screen bg-background text-foreground relative overflow-hidden flex flex-col">
      {/* Hero Glow */}
      <div className="absolute top-1/4 left-1/2 -translate-x-1/2 w-[800px] h-[400px] bg-amber-500/10 rounded-full blur-[160px] pointer-events-none"></div>

      {/* Header */}
      <header className="h-20 px-8 flex items-center justify-between border-b border-white/10 glass-card">
        <div className="flex items-center space-x-3">
          <div className="w-10 h-10 rounded-xl gold-gradient-bg flex items-center justify-center shadow-gold-glow">
            <Zap className="w-6 h-6 text-zinc-950 stroke-[2.5]" />
          </div>
          <span className="text-xl font-bold tracking-tight gold-gradient-text">IntelliFlow</span>
        </div>

        <div className="flex items-center space-x-4">
          <Link to="/login" className="text-xs font-semibold text-zinc-300 hover:text-amber-400 transition-colors">
            Sign In
          </Link>
          <Link
            to="/dashboard"
            className="px-4 py-2 rounded-xl text-xs font-semibold gold-gradient-bg text-zinc-950 hover:brightness-110 transition-all shadow-gold-sm"
          >
            Launch Platform
          </Link>
        </div>
      </header>

      {/* Hero Section */}
      <main className="flex-1 flex flex-col items-center justify-center px-6 text-center max-w-5xl mx-auto py-20">
        <div className="inline-flex items-center space-x-2 px-3 py-1 rounded-full bg-amber-500/10 border border-amber-500/20 text-amber-400 text-xs font-medium mb-8 animate-bounce">
          <Cpu className="w-3.5 h-3.5" />
          <span>IntelliFlow v1.0.0 — Enterprise RAG & Vector Engine Active</span>
        </div>

        <h1 className="text-5xl md:text-7xl font-extrabold tracking-tight leading-tight">
          Next-Gen AI Task & <br />
          <span className="gold-gradient-text">Vector Knowledge Intelligence</span>
        </h1>

        <p className="mt-6 text-lg text-zinc-400 max-w-2xl leading-relaxed">
          Unify task workflows, Apache Tika document parsing, and pgvector HNSW similarity retrieval in a single security-hardened enterprise SaaS platform.
        </p>

        <div className="mt-10 flex items-center space-x-4">
          <Link
            to="/dashboard"
            className="px-8 py-3.5 rounded-xl font-bold text-sm gold-gradient-bg text-zinc-950 shadow-gold-glow hover:scale-105 transition-transform flex items-center space-x-2"
          >
            <span>Explore Dashboard</span>
            <ArrowRight className="w-4 h-4" />
          </Link>
          <Link
            to="/login"
            className="px-8 py-3.5 rounded-xl font-semibold text-sm glass-card border border-white/10 text-zinc-200 hover:border-amber-500/40 transition-colors"
          >
            System Login
          </Link>
        </div>

        {/* Feature Grid */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mt-20 text-left w-full">
          <div className="p-6 rounded-2xl glass-card border border-white/10 hover:border-amber-500/30 transition-colors">
            <Database className="w-8 h-8 text-amber-400 mb-4" />
            <h3 className="text-base font-bold text-zinc-100">384-dim Vector Search</h3>
            <p className="mt-2 text-xs text-zinc-400 leading-relaxed">
              Native pgvector HNSW index search returning grounded prompt context with strict department permissions.
            </p>
          </div>

          <div className="p-6 rounded-2xl glass-card border border-white/10 hover:border-amber-500/30 transition-colors">
            <ShieldCheck className="w-8 h-8 text-amber-400 mb-4" />
            <h3 className="text-base font-bold text-zinc-100">OWASP Hardened API</h3>
            <p className="mt-2 text-xs text-zinc-400 leading-relaxed">
              Token bucket rate limiting, HSTS security response headers, and idempotency key replay protection.
            </p>
          </div>

          <div className="p-6 rounded-2xl glass-card border border-white/10 hover:border-amber-500/30 transition-colors">
            <Zap className="w-8 h-8 text-amber-400 mb-4" />
            <h3 className="text-base font-bold text-zinc-100">Real-Time WebSockets</h3>
            <p className="mt-2 text-xs text-zinc-400 leading-relaxed">
              STOMP protocol event streaming delivering instant task assignments and system notifications.
            </p>
          </div>
        </div>
      </main>
    </div>
  );
};
