import React from 'react';
import { Outlet } from 'react-router-dom';
import { Sidebar } from './Sidebar';
import { Navbar } from './Navbar';

export const DashboardLayout: React.FC = () => {
  return (
    <div className="flex min-h-screen enterprise-grid-bg text-foreground relative overflow-hidden">
      {/* Restrained Ambient Gold Illumination */}
      <div className="absolute top-0 left-1/4 w-[600px] h-[300px] bg-amber-500/[0.03] rounded-full blur-[140px] pointer-events-none"></div>

      {/* Fixed Sidebar */}
      <Sidebar />

      {/* Main App Container */}
      <div className="flex-1 flex flex-col min-w-0">
        <Navbar />

        {/* Dynamic Page Content */}
        <main className="flex-1 p-6 md:p-8 overflow-y-auto">
          <Outlet />
        </main>
      </div>
    </div>
  );
};
