"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";

import { cn } from "@/lib/utils";

interface VideoDetailTabsProps {
  videoId: string;
}

const TABS = [
  { href: "", label: "Overview" },
  { href: "notes", label: "Notes" },
  { href: "summary", label: "Summary" },
  { href: "quiz", label: "Quiz" },
  { href: "chat", label: "Ask AI" },
];

export function VideoDetailTabs({ videoId }: VideoDetailTabsProps) {
  const pathname = usePathname();
  const base = `/dashboard/videos/${videoId}`;

  return (
    <div className="flex items-center gap-1 overflow-x-auto border-b border-border scrollbar-thin">
      {TABS.map((tab) => {
        const href = tab.href ? `${base}/${tab.href}` : base;
        const active = pathname === href;
        return (
          <Link
            key={tab.href || "overview"}
            href={href}
            aria-current={active ? "page" : undefined}
            className={cn(
              "relative inline-flex min-h-12 items-center shrink-0 px-3 py-2 text-sm transition-colors",
              active
                ? "text-text-primary"
                : "text-text-tertiary hover:text-text-secondary",
            )}
          >
            {tab.label}
            {active ? (
              <span className="absolute inset-x-2 -bottom-px h-0.5 bg-accent" />
            ) : null}
          </Link>
        );
      })}
    </div>
  );
}
