"use client";

import type { LucideIcon } from "lucide-react";
import { Loader2, Sparkles } from "lucide-react";

import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";

interface AIEmptyStateProps {
    title: string;
    description: string;
    ctaLabel?: string;
    loading?: boolean;
    onGenerate?: () => void;
    icon?: LucideIcon;
    className?: string;
}

export function AIEmptyState({
    title,
    description,
    ctaLabel = "Generate with AI",
    loading = false,
    onGenerate,
    icon: Icon = Sparkles,
    className,
}: AIEmptyStateProps) {
    return (
        <div
            className={cn(
                "flex flex-col items-center justify-center rounded-lg border border-dashed border-border bg-surface/30 px-6 py-12 text-center",
                className,
            )}
        >
            <div className="flex h-10 w-10 items-center justify-center rounded-full bg-accent-muted text-accent">
                {loading ? (
                    <Loader2 className="h-5 w-5 animate-spin" />
                ) : (
                    <Icon className="h-5 w-5" />
                )}
            </div>

            <h3 className="mt-4 text-sm font-medium text-text-primary">{title}</h3>
            <p className="mt-1.5 max-w-sm text-xs leading-relaxed text-text-secondary">
                {description}
            </p>

            {onGenerate ? (
                <Button
                    type="button"
                    disabled={loading}
                    onClick={onGenerate}
                    className="mt-5 h-8 bg-accent px-3.5 text-xs font-medium text-accent-fg transition-colors hover:bg-accent-hover"
                >
                    {loading ? (
                        <>
                            <Loader2 className="mr-2 h-3.5 w-3.5 animate-spin" />
                            Generating…
                        </>
                    ) : (
                        <>
                            <Icon className="mr-1.5 h-3.5 w-3.5" />
                            {ctaLabel}
                        </>
                    )}
                </Button>
            ) : null}
        </div>
    );
}
