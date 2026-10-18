"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { ChevronLeft } from "lucide-react";

import { Logo } from "@/components/common/logo";
import {
  accountNavigation,
  adminNavigation,
  primaryNavigation,
  type NavItem,
  type NavSection,
} from "@/lib/navigation";
import { cn } from "@/lib/utils";
import { useUIStore } from "@/stores/ui-store";

function NavLink({
  item,
  collapsed,
  active,
}: {
  item: NavItem;
  collapsed: boolean;
  active: boolean;
}) {
  const Icon = item.icon;
  return (
    <Link
      href={item.href}
      title={collapsed ? item.label : undefined}
      className={cn(
        "group flex items-center gap-3 min-h-11 rounded-md px-2.5 py-2 text-sm transition-colors",
        active
          ? "bg-surface-2 text-text-primary"
          : "text-text-secondary hover:bg-surface-2 hover:text-text-primary",
      )}
    >
      <Icon
        className={cn(
          "h-4 w-4 shrink-0",
          active
            ? "text-accent"
            : "text-text-tertiary group-hover:text-text-secondary",
        )}
      />
      {!collapsed ? <span className="truncate">{item.label}</span> : null}
      {active && !collapsed ? (
        <span className="ml-auto h-1.5 w-1.5 rounded-full bg-accent" />
      ) : null}
    </Link>
  );
}

function Section({
  section,
  collapsed,
  pathname,
}: {
  section: NavSection;
  collapsed: boolean;
  pathname: string;
}) {
  return (
    <div className="space-y-1">
      {section.title && !collapsed ? (
        <p className="px-2.5 pt-4 pb-1 text-[10px] font-medium uppercase tracking-widest text-text-tertiary">
          {section.title}
        </p>
      ) : null}
      {section.items.map((item) => {
        const active =
          item.href === "/dashboard" || item.href === "/admin"
            ? pathname === "/dashboard"
            : pathname === item.href || pathname.startsWith(`${item.href}/`);
        return (
          <NavLink
            key={item.href}
            item={item}
            collapsed={collapsed}
            active={active}
          />
        );
      })}
    </div>
  );
}

export function Sidebar() {
  const pathname = usePathname();
  const collapsed = useUIStore((s) => s.sidebarCollapsed);
  const toggle = useUIStore((s) => s.toggleSidebar);

  return (
    <aside
      className={cn(
        "hidden shrink-0 flex-col border-r border-border bg-background lg:flex",
        collapsed ? "w-16" : "w-56",
      )}
    >
      <div
        className={cn(
          "flex h-16 items-center border-b border-border",
          collapsed ? "justify-center px-2" : "justify-between px-4",
        )}
      >
        <Logo showWordmark={!collapsed} />
        {!collapsed ? (
          <button
            type="button"
            onClick={toggle}
            aria-label="Collapse sidebar"
            className="flex h-6 w-6 items-center justify-center rounded-md text-text-tertiary transition-colors hover:bg-surface-2 hover:text-text-primary"
          >
            <ChevronLeft className="h-3.5 w-3.5" />
          </button>
        ) : null}
      </div>

      <nav className="flex-1 overflow-y-auto px-2 py-3 scrollbar-thin">
        <Section
          section={
            pathname.startsWith("/admin") ? adminNavigation : primaryNavigation
          }
          collapsed={collapsed}
          pathname={pathname}
        />
        <Section
          section={accountNavigation}
          collapsed={collapsed}
          pathname={pathname}
        />
      </nav>

      {collapsed ? (
        <div className="border-t border-border p-2">
          <button
            type="button"
            onClick={toggle}
            aria-label="Expand sidebar"
            className="flex h-8 w-full items-center justify-center rounded-md text-text-tertiary transition-colors hover:bg-surface-2 hover:text-text-primary"
          >
            <ChevronLeft className="h-3.5 w-3.5 rotate-180" />
          </button>
        </div>
      ) : null}
    </aside>
  );
}
