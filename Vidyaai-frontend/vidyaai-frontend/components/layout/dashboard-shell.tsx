import { CommandPaletteMount } from "./command-palette-mount";
import { MobileSidebar } from "./mobile-sidebar";
import { Sidebar } from "./sidebar";
import { Topbar } from "./topbar";
import { NetworkStatusBanner } from "@/components/common/network-status-banner";

interface DashboardShellProps {
  children: React.ReactNode;
}

export function DashboardShell({ children }: DashboardShellProps) {
  return (
    <div className="flex min-h-screen bg-background">
      <Sidebar />
      <div className="flex min-w-0 flex-1 flex-col">
        <NetworkStatusBanner />
        <div className="relative">
          <div className="absolute left-3 top-4 z-40 lg:hidden">
            <MobileSidebar />
          </div>
          <Topbar />
        </div>
        <main
          id="main-content"
          className="flex-1 overflow-x-hidden px-4 py-6 lg:px-8 lg:py-8"
        >
          <div className="mx-auto w-full max-w-6xl">{children}</div>
        </main>
      </div>
      <CommandPaletteMount />
    </div>
  );
}
