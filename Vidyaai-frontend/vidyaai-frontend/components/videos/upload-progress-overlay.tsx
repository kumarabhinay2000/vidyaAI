"use client";

import { Loader2 } from "lucide-react";

interface UploadProgressOverlayProps {
    open: boolean;
    filename?: string;
    progress: number; // 0-100
}

export function UploadProgressOverlay({
                                          open,
                                          filename,
                                          progress,
                                      }: UploadProgressOverlayProps) {
    if (!open) return null;

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-background/70 backdrop-blur-sm">
            <div className="w-full max-w-sm rounded-lg border border-border bg-surface-2 p-5">
                <div className="flex items-center gap-2">
                    <Loader2 className="h-4 w-4 animate-spin text-accent" />
                    <p className="text-sm font-medium text-text-primary">
                        Uploading your video
                    </p>
                </div>
                {filename ? (
                    <p className="mt-1 truncate text-[11px] text-text-tertiary">
                        {filename}
                    </p>
                ) : null}

                <div className="mt-4 h-1.5 overflow-hidden rounded-full bg-surface-3">
                    <div
                        className="h-full bg-accent transition-all"
                        style={{ width: `${Math.max(0, Math.min(100, progress))}%` }}
                    />
                </div>
                <p className="mt-2 text-right font-mono text-[11px] text-text-tertiary">
                    {progress}%
                </p>
            </div>
        </div>
    );
}