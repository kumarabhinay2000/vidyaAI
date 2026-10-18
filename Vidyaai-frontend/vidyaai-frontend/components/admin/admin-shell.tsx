"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";

import { cn } from "@/lib/utils";

const ADMIN_TABS = [
    { href: "/admin", label: "Overview" },
    { href: "/admin/users", label: "Users" },
    { href: "/admin/videos", label: "Videos" },
];

export function AdminTabs() {
    const pathname = usePathname();
    return (
        <div className="flex items-center gap-1 border-b border-border">
            {ADMIN_TABS.map((tab) => {
                const active = pathname === tab.href;
                return (
                    <Link
                        key={tab.href}
                        href={tab.href}
                        className={cn(
                            "relative px-3 py-2 text-sm transition-colors",
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
