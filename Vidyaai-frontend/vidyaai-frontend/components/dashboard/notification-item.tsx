import type { LucideIcon } from "lucide-react";

import { cn } from "@/lib/utils";

interface NotificationItemProps {
    icon: LucideIcon;
    title: string;
    description?: string;
    meta?: string;
    className?: string;
}

export function NotificationItem({
                                     icon: Icon,
                                     title,
                                     description,
                                     meta,
                                     className,
                                 }: NotificationItemProps) {
    return (
        <div
            className={cn(
                "flex items-start gap-3 border-b border-border py-3 last:border-b-0",
                className,
            )}
        >
      <span className="mt-0.5 flex h-7 w-7 shrink-0 items-center justify-center rounded-md bg-surface-2">
        <Icon className="h-3.5 w-3.5 text-accent" />
      </span>
            <div className="min-w-0 flex-1">
                <p className="text-sm text-text-primary">{title}</p>
                {description ? (
                    <p className="mt-0.5 text-[11px] text-text-secondary">
                        {description}
                    </p>
                ) : null}
            </div>
            {meta ? (
                <span className="shrink-0 text-[11px] text-text-tertiary">{meta}</span>
            ) : null}
        </div>
    );
}