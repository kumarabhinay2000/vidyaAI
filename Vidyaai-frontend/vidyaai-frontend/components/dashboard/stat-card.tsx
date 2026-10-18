import type { LucideIcon } from "lucide-react";

import { cn } from "@/lib/utils";

interface StatCardProps {
    label: string;
    value?: string | number | null;
    hint?: string;
    icon?: LucideIcon;
    className?: string;
}

/**
 * StatCard follows core UX Principles:
 * - Hierarchy: Numbers are the hero in stats. Labels are the shadow.
 * - Trust: Empty is better than wrong. "—" is better than "0%" when 0 is unknown.
 */
export function StatCard({
                             label,
                             value,
                             hint,
                             icon: Icon,
                             className,
                         }: StatCardProps) {
    const displayValue =
        value === null || value === undefined || value === "" ? "—" : value;

    return (
        <div
            className={cn(
                "rounded-xl border border-border bg-surface/50 p-5 transition-colors hover:border-border-strong",
                className,
            )}
        >
            <div className="flex items-center justify-between gap-2">
                <p className="text-[11px] font-medium uppercase tracking-wider text-text-tertiary">
                    {label}
                </p>
                {Icon ? (
                    <Icon className="h-4 w-4 text-text-tertiary shrink-0" aria-hidden="true" />
                ) : null}
            </div>

            <p className="mt-3 text-3xl font-extrabold tracking-tight text-text-primary sm:text-4xl">
                {displayValue}
            </p>

            {hint ? (
                <p className="mt-1.5 text-xs text-text-tertiary">{hint}</p>
            ) : null}
        </div>
    );
}