import type { LucideIcon } from "lucide-react";

import { cn } from "@/lib/utils";

interface ActivityItemProps {
    icon: LucideIcon;
    title: string;
    meta?: string;
    className?: string;
}

export function ActivityItem({
                                 icon: Icon,
                                 title,
                                 meta,
                                 className,
                             }: ActivityItemProps) {
    return (
        <div
            className={cn(
                "flex items-center gap-3 border-b border-border py-3 last:border-b-0",
                className,
            )}
        >
      <span className="flex h-7 w-7 shrink-0 items-center justify-center rounded-md bg-surface-2">
        <Icon className="h-3.5 w-3.5 text-text-secondary" />
      </span>
            <p className="min-w-0 flex-1 truncate text-sm text-text-primary">
                {title}
            </p>
            {meta ? (
                <span className="shrink-0 text-[11px] text-text-tertiary">{meta}</span>
            ) : null}
        </div>
    );
}