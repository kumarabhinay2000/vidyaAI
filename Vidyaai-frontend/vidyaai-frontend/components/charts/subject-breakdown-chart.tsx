"use client";

import { cn } from "@/lib/utils";

interface SubjectBreakdownItem {
    label: string;
    percent: number;
    count: number;
}

interface SubjectBreakdownChartProps {
    data: SubjectBreakdownItem[];
}

export function SubjectBreakdownChart({ data }: SubjectBreakdownChartProps) {
    return (
        <div className="rounded-lg border border-border bg-surface/40">
            <div className="border-b border-border px-4 py-3">
                <p className="text-sm font-medium text-text-primary">
                    Subject breakdown
                </p>
                <p className="mt-0.5 text-[11px] text-text-tertiary">
                    Coverage across your library
                </p>
            </div>
            <div className="space-y-4 px-4 py-4">
                {data.length === 0 ? (
                    <p className="text-xs text-text-tertiary">No data yet.</p>
                ) : (
                    data.map((item) => (
                        <div key={item.label}>
                            <div className="flex items-center justify-between text-xs">
                                <span className="text-text-secondary">{item.label}</span>
                                <span className="font-mono text-text-tertiary">
                  {item.percent}%
                </span>
                            </div>
                            <div className="mt-1.5 h-1.5 overflow-hidden rounded-full bg-surface-2">
                                <div
                                    className={cn("h-full bg-accent transition-all")}
                                    style={{ width: `${Math.max(0, Math.min(100, item.percent))}%` }}
                                />
                            </div>
                        </div>
                    ))
                )}
            </div>
        </div>
    );
}