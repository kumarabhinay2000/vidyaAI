"use client";

import { useMemo } from "react";
import { useRouter } from "next/navigation";
import {
  BarChart3,
  Bot,
  LayoutDashboard,
  LogOut,
  Trophy,
  Upload,
  User,
  Video,
} from "lucide-react";

import {
  CommandDialog,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
  CommandList,
  CommandSeparator,
} from "@/components/ui/command";
import { authApi } from "@/lib/api/auth";
import { useAuthStore } from "@/stores/auth-store";
import { useUIStore } from "@/stores/ui-store";

export function CommandPalette() {
  const router = useRouter();
  const open = useUIStore((s) => s.commandPaletteOpen);
  const close = useUIStore((s) => s.closeCommandPalette);
  const clearAuth = useAuthStore((s) => s.clear);
  const isAuthenticated = useAuthStore((s) => s.isAuthenticated());

  const navigate = (href: string) => {
    close();
    router.push(href);
  };

  const handleLogout = async () => {
    close();
    try {
      await authApi.logout();
    } catch {
      clearAuth();
    }
    router.replace("/");
  };

  const items = useMemo(
    () => [
      { icon: LayoutDashboard, label: "Dashboard", href: "/dashboard" },
      { icon: Video, label: "Videos", href: "/dashboard/videos" },
      { icon: Upload, label: "Upload video", href: "/dashboard/videos/upload" },
      { icon: BarChart3, label: "Progress", href: "/dashboard/progress" },
      { icon: Trophy, label: "Leaderboard", href: "/dashboard/leaderboard" },
      { icon: Bot, label: "AI Tutor", href: "/dashboard/chat" },
      { icon: User, label: "Profile", href: "/dashboard/profile" },
    ],
    [],
  );

  return (
    <CommandDialog
      open={open}
      onOpenChange={(next) => (next ? undefined : close())}
    >
      <CommandInput placeholder="Search pages and actions…" />
      <CommandList className="max-h-80">
        <CommandEmpty>No results found.</CommandEmpty>
        <CommandGroup heading="Navigate">
          {items.map((item) => {
            const Icon = item.icon;
            return (
              <CommandItem
                key={item.href}
                onSelect={() => navigate(item.href)}
                className="flex items-center gap-2"
              >
                <Icon className="h-3.5 w-3.5 text-text-tertiary" />
                <span>{item.label}</span>
              </CommandItem>
            );
          })}
        </CommandGroup>

        {isAuthenticated ? (
          <>
            <CommandSeparator />
            <CommandGroup heading="Account">
              <CommandItem
                onSelect={() => void handleLogout()}
                className="flex items-center gap-2 text-danger data-[selected=true]:text-danger"
              >
                <LogOut className="h-3.5 w-3.5" />
                <span>Sign out</span>
              </CommandItem>
            </CommandGroup>
          </>
        ) : null}
      </CommandList>
    </CommandDialog>
  );
}
