import React from 'react';
import { Link } from 'react-router-dom';
import { AlertCircle, ArrowLeft } from 'lucide-react';

export const NotFoundPage: React.FC = () => {
  return (
    <div className="min-h-screen bg-background flex flex-col items-center justify-center p-6 text-center">
      <div className="w-16 h-16 rounded-2xl bg-amber-500/10 border border-amber-500/30 flex items-center justify-center text-amber-400 mb-4 shadow-gold-glow">
        <AlertCircle className="w-8 h-8" />
      </div>
      <h1 className="text-4xl font-extrabold gold-gradient-text">404 — Page Not Found</h1>
      <p className="text-xs text-zinc-400 mt-2 max-w-sm">
        The requested route or resource location does not exist in the IntelliFlow environment.
      </p>
      <Link
        to="/dashboard"
        className="mt-6 px-6 py-2.5 rounded-xl font-bold text-xs gold-gradient-bg text-zinc-950 shadow-gold-sm hover:brightness-110 flex items-center space-x-2"
      >
        <ArrowLeft className="w-4 h-4" />
        <span>Return to Dashboard</span>
      </Link>
    </div>
  );
};
