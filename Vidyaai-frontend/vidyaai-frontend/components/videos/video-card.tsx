"use client";

import Link from "next/link";
import { PlayCircle } from "lucide-react";

import { Youtube } from "@/components/common/icons";

import { VideoStatusPill } from "./video-status-pill";
import { formatDuration, formatRelativeTime, cn } from "@/lib/utils";
import type { VideoSummary } from "@/types/video";

interface VideoCardProps {
    video: VideoSummary;
    className?: string;
}

export function VideoCard({ video, className }: VideoCardProps) {
    const isYouTube = video.sourceType === "YOUTUBE";
    const processing =
        video.status === "PROCESSING" || video.status === "UPLOADING";

    return (
        <Link
            href={`/dashboard/videos/${video.id}`}
            className={cn(
                "group block overflow-hidden rounded-lg border border-border bg-surface/40 transition-colors hover:border-border-strong hover:bg-surface-2",
                className,
            )}
        >
            <div className="relative aspect-video w-full overflow-hidden bg-surface-2">
                {video.thumbnailUrl ? (
                    // eslint-disable-next-line @next/next/no-img-element
                    <img
                        src={video.thumbnailUrl}
                        alt={video.title}
                        className="h-full w-full object-cover transition-transform duration-300 group-hover:scale-[1.02]"
                        loading="lazy"
                    />
                ) : (
                    <div className="flex h-full w-full items-center justify-center">
                        <PlayCircle className="h-10 w-10 text-text-tertiary" />
                    </div>
                )}

                <div className="absolute right-2 top-2">
                    <VideoStatusPill status={video.status} />
                </div>

                {typeof video.durationSeconds === "number" && video.durationSeconds > 0 ? (
                    <span className="absolute bottom-2 right-2 rounded bg-black/70 px-1.5 py-0.5 font-mono text-[10px] text-white">
            {formatDuration(video.durationSeconds)}
          </span>
                ) : null}

                {isYouTube ? (
                    <span className="absolute bottom-2 left-2 flex items-center gap-1 rounded bg-black/70 px-1.5 py-0.5 text-[10px] text-white">
            <Youtube className="h-3 w-3" />
            YouTube
          </span>
                ) : null}

                {processing ? (
                    <div className="absolute inset-x-0 bottom-0 h-0.5 bg-border">
                        <div
                            className="h-full bg-accent transition-all"
                            style={{ width: `${Math.max(0, Math.min(100, video.processingProgress))}%` }}
                        />
                    </div>
                ) : null}
            </div>

            <div className="p-3">
                <p className="line-clamp-2 text-sm font-medium text-text-primary">
                    {video.title}
                </p>
                <p className="mt-1 text-[11px] text-text-tertiary">
                    {formatRelativeTime(video.createdAt)}
                    {video.viewCount > 0 ? ` · ${video.viewCount} views` : ""}
                </p>
            </div>
        </Link>
    );
}