"use client";

import { Clock } from "lucide-react";

import { formatDuration } from "@/lib/utils";
import type { VideoChapter } from "@/types/video";

interface ChapterListProps {
  chapters: VideoChapter[];
  onSeek?: (seconds: number) => void;
}

export function ChapterList({ chapters, onSeek }: ChapterListProps) {
  if (chapters.length === 0) {
    return (
      <p className="text-xs text-text-tertiary">
        No chapters available for this video.
      </p>
    );
  }

  return (
    <div className="space-y-1">
      {chapters.map((chapter) => (
        <button
          key={chapter.id}
          type="button"
          onClick={() => onSeek?.(chapter.startTime)}
          className="flex w-full items-start gap-3 rounded-md px-2 py-1.5 text-left transition-colors hover:bg-surface-2"
        >
          <span className="mt-0.5 flex shrink-0 items-center gap-1 font-mono text-[10px] text-text-tertiary">
            <Clock className="h-3 w-3" />
            {formatDuration(chapter.startTime)}
          </span>
          <span className="truncate text-xs text-text-secondary">
            {chapter.title}
          </span>
        </button>
      ))}
    </div>
  );
}
