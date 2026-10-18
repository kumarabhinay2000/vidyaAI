"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { Bell, Search } from "lucide-react";

import { Logo } from "@/components/common/logo";
import { ThemeToggle } from "@/components/common/theme-toggle";
import { UserMenu } from "./user-menu";
import { cn } from "@/lib/utils";
import { useUIStore } from "@/stores/ui-store";

const PAGE_TITLES: Array<{ pattern: string; label: string }> = [
  { pattern: "/admin", label: "Administration" },
  { pattern: "/dashboard", label: "Overview" },
  { pattern: "/dashboard/videos/upload", label: "Upload video" },
  { pattern: "/dashboard/videos", label: "My lectures" },
  { pattern: "/dashboard/progress", label: "Progress" },
  { pattern: "/dashboard/leaderboard", label: "Leaderboard" },
  { pattern: "/dashboard/chat", label: "AI Tutor" },
  { pattern: "/dashboard/profile", label: "Profile" },
];

function resolveTitle(pathname: string): string {
  // Longest match wins.
  const matches = PAGE_TITLES.filter((t) => pathname.startsWith(t.pattern));
  if (matches.length === 0) return "VidyaAI";
  matches.sort((a, b) => b.pattern.length - a.pattern.length);
  return matches[0].label;
}

export function Topbar() {
  const pathname = usePathname();
  const openPalette = useUIStore((s) => s.openCommandPalette);
  const title = resolveTitle(pathname);

  return (
    <header className="sticky top-0 z-30 flex h-16 items-center gap-3 border-b border-border bg-background/85 pl-14 pr-4 backdrop-blur-md lg:pl-6 lg:px-6">
      <div className="hidden">
        <Logo showWordmark={false} />
      </div>

      <h2 className="text-sm font-medium text-text-primary">{title}</h2>

      <div className="ml-auto flex items-center gap-1">
        <button
          type="button"
          onClick={openPalette}
          aria-label="Open search"
          className={cn(
            "hidden h-8 items-center gap-2 rounded-md border border-border bg-surface-2 px-2.5 text-xs text-text-tertiary transition-colors hover:border-border-strong hover:text-text-secondary sm:inline-flex",
          )}
        >
          <Search className="h-3.5 w-3.5" />
          <span>Search</span>
          <kbd className="ml-2 rounded border border-border bg-background px-1.5 py-0.5 font-mono text-[10px] text-text-tertiary">
            Ctrl K
          </kbd>
        </button>

        <ThemeToggle
          variant="dropdown"
          className="h-8 w-8 min-h-[36px] min-w-[36px]"
        />

        <Link
          href="/dashboard/notifications"
          aria-label="Notifications"
          className="flex h-8 w-8 min-h-[36px] min-w-[36px] items-center justify-center rounded-md text-text-tertiary transition-colors hover:bg-surface-2 hover:text-text-primary"
        >
          <Bell className="h-4 w-4" />
        </Link>

        <UserMenu />
      </div>
    </header>
  );
}
