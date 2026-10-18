"use client";

import { Loader2, PlayCircle } from "lucide-react";

import { isProcessing } from "@/lib/video-status";
import type { VideoSummary } from "@/types/video";

interface VideoStatusInlineProps {
  video: VideoSummary;
}

export function VideoStatusInline({ video }: VideoStatusInlineProps) {
  const processing = isProcessing(video.status);
  if (processing) {
    return (
      <span className="inline-flex items-center gap-1 text-[10px] text-warning">
        <Loader2 className="h-3 w-3 animate-spin" />
        Processing {video.processingProgress}%
      </span>
    );
  }
  if (video.status === "COMPLETED") {
    return (
      <span className="inline-flex items-center gap-1 text-[10px] text-success">
        <PlayCircle className="h-3 w-3" />
        Ready
      </span>
    );
  }
  return null;
}
