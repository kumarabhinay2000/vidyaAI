"use client";

import { useState } from "react";
import Link from "next/link";
import { usePathname } from "next/navigation";
import { Menu } from "lucide-react";

import { Logo } from "@/components/common/logo";
import {
  Sheet,
  SheetContent,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
} from "@/components/ui/sheet";
import {
  accountNavigation,
  adminNavigation,
  primaryNavigation,
  type NavSection,
} from "@/lib/navigation";
import { cn } from "@/lib/utils";

function MobileNavSection({
  section,
  pathname,
  onNavigate,
}: {
  section: NavSection;
  pathname: string;
  onNavigate: () => void;
}) {
  return (
    <div className="space-y-1">
      {section.title ? (
        <p className="px-2.5 pt-3 pb-1 text-[10px] font-medium uppercase tracking-widest text-text-tertiary">
          {section.title}
        </p>
      ) : null}
      {section.items.map((item) => {
        const Icon = item.icon;
        const active =
          item.href === "/dashboard" || item.href === "/admin"
            ? pathname === "/dashboard"
            : pathname === item.href || pathname.startsWith(`${item.href}/`);
        return (
          <Link
            key={item.href}
            href={item.href}
            onClick={onNavigate}
            className={cn(
              "flex items-center gap-3 min-h-11 rounded-md px-2.5 py-2 text-sm transition-colors",
              active
                ? "bg-surface-2 text-text-primary"
                : "text-text-secondary hover:bg-surface-2 hover:text-text-primary",
            )}
          >
            <Icon
              className={cn(
                "h-4 w-4 shrink-0",
                active ? "text-accent" : "text-text-tertiary",
              )}
            />
            <span className="truncate">{item.label}</span>
          </Link>
        );
      })}
    </div>
  );
}

export function MobileSidebar() {
  const [open, setOpen] = useState(false);
  const pathname = usePathname();
  const close = () => setOpen(false);

  return (
    <Sheet open={open} onOpenChange={setOpen}>
      <SheetTrigger
        type="button"
        aria-label="Open navigation"
        className="flex h-11 w-11 items-center justify-center rounded-md text-text-tertiary transition-colors hover:bg-surface-2 hover:text-text-primary lg:hidden"
      >
        <Menu className="h-4 w-4" />
      </SheetTrigger>
      <SheetContent
        side="left"
        className="w-64 border-border bg-background p-0"
      >
        <SheetHeader className="flex h-16 flex-row items-center border-b border-border px-4">
          <SheetTitle className="text-sm">
            <Logo />
          </SheetTitle>
        </SheetHeader>
        <nav className="overflow-y-auto px-2 py-2 scrollbar-thin">
          <MobileNavSection
            section={
              pathname.startsWith("/admin")
                ? adminNavigation
                : primaryNavigation
            }
            pathname={pathname}
            onNavigate={close}
          />
          <MobileNavSection
            section={accountNavigation}
            pathname={pathname}
            onNavigate={close}
          />
        </nav>
      </SheetContent>
    </Sheet>
  );
}
