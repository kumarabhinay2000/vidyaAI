"use client";

import { useRouter } from "next/navigation";
import Link from "next/link";
import { LogOut, Moon, Sun, User as UserIcon } from "lucide-react";
import { toast } from "sonner";

import { UserAvatar } from "@/components/common/user-avatar";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { authApi } from "@/lib/api/auth";
import { useAuthStore } from "@/stores/auth-store";
import { useTheme } from "@/components/providers/theme-provider";

export function UserMenu() {
  const router = useRouter();
  const user = useAuthStore((s) => s.user);
  const clear = useAuthStore((s) => s.clear);
  const { theme, resolvedTheme, toggleTheme } = useTheme();

  if (!user) {
    return (
      <Link
        href="/login"
        className="rounded-md border border-border px-2.5 py-1 text-xs text-text-secondary transition-colors hover:bg-surface-2 hover:text-text-primary"
      >
        Sign in
      </Link>
    );
  }

  const handleLogout = async () => {
    try {
      await authApi.logout();
    } catch {
      clear();
    } finally {
      toast.success("Signed out");
      router.replace("/");
    }
  };

  return (
    <DropdownMenu>
      <DropdownMenuTrigger
        type="button"
        aria-label="Open user menu"
        className="flex h-8 items-center gap-2 rounded-md px-1 transition-colors hover:bg-surface-2"
      >
        <UserAvatar
          firstName={user.firstName}
          lastName={user.lastName}
          imageUrl={user.profilePicUrl}
          size="sm"
        />
      </DropdownMenuTrigger>

      <DropdownMenuContent
        align="end"
        sideOffset={6}
        className="w-56 border-border bg-surface-2"
      >
        <DropdownMenuLabel className="flex flex-col">
          <span className="text-xs font-medium text-text-primary">
            {user.firstName} {user.lastName ?? ""}
          </span>
          <span className="truncate text-[11px] font-normal text-text-tertiary">
            {user.email}
          </span>
        </DropdownMenuLabel>
        <DropdownMenuSeparator className="bg-border" />

        <DropdownMenuItem className="p-0 focus:bg-surface-3">
          <Link
            href="/dashboard/profile"
            className="flex w-full items-center gap-2 px-1.5 py-1"
          >
            <UserIcon className="h-3.5 w-3.5" />
            <span>Profile</span>
          </Link>
        </DropdownMenuItem>

        <DropdownMenuItem
          onSelect={(e) => {
            e.preventDefault();
            toggleTheme();
          }}
          className="flex items-center justify-between px-1.5 py-1 focus:bg-surface-3 cursor-pointer"
        >
          <div className="flex items-center gap-2">
            {resolvedTheme === "dark" ? (
              <Moon className="h-3.5 w-3.5 text-accent" />
            ) : (
              <Sun className="h-3.5 w-3.5 text-amber-500" />
            )}
            <span>Theme</span>
          </div>
          <span className="text-[10px] uppercase font-mono text-text-tertiary">
            {theme}
          </span>
        </DropdownMenuItem>

        <DropdownMenuSeparator className="bg-border" />

        <DropdownMenuItem
          onSelect={(e) => {
            e.preventDefault();
            void handleLogout();
          }}
          className="text-danger focus:bg-danger-muted focus:text-danger"
        >
          <LogOut className="h-3.5 w-3.5" />
          <span>Sign out</span>
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  );
}
