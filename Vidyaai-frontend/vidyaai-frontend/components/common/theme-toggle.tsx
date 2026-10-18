"use client";

import * as React from "react";
import { Laptop, Moon, Sun } from "lucide-react";

import { useTheme, type Theme } from "@/components/providers/theme-provider";
import { cn } from "@/lib/utils";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";

interface ThemeToggleProps {
  variant?: "dropdown" | "simple";
  className?: string;
}

const subscribeMounted = () => () => {};

export function ThemeToggle({
  variant = "dropdown",
  className,
}: ThemeToggleProps) {
  const { theme, resolvedTheme, setTheme, toggleTheme } = useTheme();
  const mounted = React.useSyncExternalStore(
    subscribeMounted,
    () => true,
    () => false,
  );

  // Render placeholder with same size during SSR to avoid hydration layout shift
  if (!mounted) {
    return (
      <div
        className={cn(
          "flex h-9 w-9 min-h-[44px] min-w-[44px] items-center justify-center rounded-lg border border-border bg-surface-2 text-text-tertiary",
          className,
        )}
        aria-hidden="true"
      >
        <span className="h-4 w-4" />
      </div>
    );
  }

  if (variant === "simple") {
    return (
      <button
        type="button"
        onClick={toggleTheme}
        aria-label={`Switch to ${resolvedTheme === "dark" ? "light" : "dark"} theme`}
        title={`Switch to ${resolvedTheme === "dark" ? "light" : "dark"} theme`}
        className={cn(
          "group relative flex h-9 w-9 min-h-[44px] min-w-[44px] items-center justify-center rounded-lg border border-border bg-surface-2 text-text-secondary transition-colors hover:border-border-strong hover:bg-surface-3 hover:text-text-primary focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring",
          className,
        )}
      >
        {resolvedTheme === "dark" ? (
          <Moon className="h-4 w-4 transition-transform group-hover:rotate-12 text-accent" />
        ) : (
          <Sun className="h-4 w-4 transition-transform group-hover:rotate-45 text-amber-500" />
        )}
      </button>
    );
  }

  return (
    <DropdownMenu>
      <DropdownMenuTrigger
        type="button"
        aria-label={`Current theme: ${theme}. Click to change theme`}
        title={`Current theme: ${theme}`}
        className={cn(
          "group flex h-9 w-9 min-h-[44px] min-w-[44px] items-center justify-center rounded-lg border border-border bg-surface-2 text-text-secondary transition-colors hover:border-border-strong hover:bg-surface-3 hover:text-text-primary focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring",
          className,
        )}
      >
        {resolvedTheme === "dark" ? (
          <Moon className="h-4 w-4 transition-transform group-hover:rotate-12 text-accent" />
        ) : (
          <Sun className="h-4 w-4 transition-transform group-hover:rotate-45 text-amber-500" />
        )}
      </DropdownMenuTrigger>

      <DropdownMenuContent
        align="end"
        sideOffset={6}
        className="w-36 border-border bg-surface-2 p-1 shadow-lg"
      >
        <DropdownMenuItem
          onClick={() => setTheme("light")}
          className={cn(
            "flex items-center gap-2 rounded-md px-2.5 py-1.5 text-xs transition-colors cursor-pointer",
            theme === "light"
              ? "bg-accent/15 text-accent font-medium"
              : "text-text-secondary hover:bg-surface-3 hover:text-text-primary",
          )}
        >
          <Sun className="h-3.5 w-3.5 text-amber-500" />
          <span>Light</span>
        </DropdownMenuItem>

        <DropdownMenuItem
          onClick={() => setTheme("dark")}
          className={cn(
            "flex items-center gap-2 rounded-md px-2.5 py-1.5 text-xs transition-colors cursor-pointer",
            theme === "dark"
              ? "bg-accent/15 text-accent font-medium"
              : "text-text-secondary hover:bg-surface-3 hover:text-text-primary",
          )}
        >
          <Moon className="h-3.5 w-3.5 text-accent" />
          <span>Dark</span>
        </DropdownMenuItem>

        <DropdownMenuItem
          onClick={() => setTheme("system")}
          className={cn(
            "flex items-center gap-2 rounded-md px-2.5 py-1.5 text-xs transition-colors cursor-pointer",
            theme === "system"
              ? "bg-accent/15 text-accent font-medium"
              : "text-text-secondary hover:bg-surface-3 hover:text-text-primary",
          )}
        >
          <Laptop className="h-3.5 w-3.5 text-text-tertiary" />
          <span>System</span>
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  );
}
