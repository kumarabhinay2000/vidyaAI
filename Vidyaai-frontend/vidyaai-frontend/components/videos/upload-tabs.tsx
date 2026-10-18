"use client";

import { Upload } from "lucide-react";

import { Youtube } from "@/components/common/icons";

import { cn } from "@/lib/utils";
import { UploadFileForm } from "./upload-file-form";
import { UploadYouTubeForm } from "./upload-youtube-form";

interface UploadTabsProps {
    mode: "file" | "youtube";
    onModeChange: (mode: "file" | "youtube") => void;
}

const TABS = [
    { key: "file" as const, label: "Upload a file", icon: Upload },
    { key: "youtube" as const, label: "YouTube URL", icon: Youtube },
];

export function UploadTabs({ mode, onModeChange }: UploadTabsProps) {
    return (
        <div>
            <div className="flex items-center gap-1 border-b border-border">
                {TABS.map((tab) => {
                    const Icon = tab.icon;
                    const active = tab.key === mode;
                    return (
                        <button
                            key={tab.key}
                            type="button"
                            onClick={() => onModeChange(tab.key)}
                            className={cn(
                                "relative inline-flex items-center gap-2 px-3 py-2 text-sm transition-colors",
                                active
                                    ? "text-text-primary"
                                    : "text-text-tertiary hover:text-text-secondary",
                            )}
                        >
                            <Icon className="h-3.5 w-3.5" />
                            {tab.label}
                            {active ? (
                                <span className="absolute inset-x-0 -bottom-px h-0.5 bg-accent" />
                            ) : null}
                        </button>
                    );
                })}
            </div>

            <div className="mt-6">
                {mode === "file" ? <UploadFileForm /> : <UploadYouTubeForm />}
            </div>
        </div>
    );
}