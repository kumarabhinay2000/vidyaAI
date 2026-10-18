import type { LucideIcon } from "lucide-react";
import Link from "next/link";

import { cn } from "@/lib/utils";

interface EmptyStateProps {
    icon?: LucideIcon;
    title: string;
    description: string;
    actionLabel?: string;
    actionHref?: string;
    className?: string;
}

export function EmptyState({
                               icon: Icon,
                               title,
                               description,
                               actionLabel,
                               actionHref,
                               className,
                           }: EmptyStateProps) {
    return (
        <div
            className={cn(
                "flex flex-col items-center justify-center rounded-lg border border-dashed border-border bg-surface/40 px-6 py-14 text-center",
                className,
            )}
        >
            {Icon ? (
                <span className="mb-4 flex h-10 w-10 items-center justify-center rounded-full bg-surface-2">
          <Icon className="h-4 w-4 text-text-secondary" />
        </span>
            ) : null}
            <h3 className="text-sm font-medium text-text-primary">{title}</h3>
            <p className="mt-1 max-w-md text-xs text-text-secondary">{description}</p>
            {actionLabel && actionHref ? (
                <Link
                    href={actionHref}
                    className="mt-6 inline-flex items-center rounded-md bg-accent px-3 py-1.5 text-xs font-medium text-accent-fg transition-colors hover:bg-accent-hover"
                >
                    {actionLabel}
                </Link>
            ) : null}
        </div>
    );
}