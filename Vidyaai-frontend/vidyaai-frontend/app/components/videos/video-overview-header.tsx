"use client";

import { useState } from "react";
import { Pencil } from "lucide-react";

import { EditVideoDialog } from "./edit-video-dialog";
import { VideoActionsMenu } from "./video-actions-menu";
import { VideoStatusPill } from "@/components/videos/video-status-pill";
import { Button } from "@/components/ui/button";
import { formatDuration, formatRelativeTime } from "@/lib/utils";
import type { Video } from "@/types/video";

interface VideoOverviewHeaderProps {
  video: Video;
}

export function VideoOverviewHeader({ video }: VideoOverviewHeaderProps) {
  const [editOpen, setEditOpen] = useState(false);

  return (
    <div className="flex flex-wrap items-start justify-between gap-4">
      <div className="min-w-0 flex-1">
        <h1 className="truncate text-lg font-semibold tracking-tight text-text-primary">
          {video.title}
        </h1>

        <div className="mt-1 flex flex-wrap items-center gap-2 text-[11px] text-text-tertiary">
          <VideoStatusPill status={video.status} />
          {typeof video.durationSeconds === "number" &&
          video.durationSeconds > 0 ? (
            <span className="font-mono">
              {formatDuration(video.durationSeconds)}
            </span>
          ) : null}
          <span>·</span>
          <span>{formatRelativeTime(video.createdAt)}</span>
          {video.viewCount > 0 ? (
            <>
              <span>·</span>
              <span>{video.viewCount} views</span>
            </>
          ) : null}
        </div>

        {video.description ? (
          <p className="mt-3 max-w-2xl text-xs leading-relaxed text-text-secondary">
            {video.description}
          </p>
        ) : null}

        {video.tags.length > 0 ? (
          <div className="mt-3 flex flex-wrap gap-1.5">
            {video.tags.map((tag) => (
              <span
                key={tag}
                className="rounded-full border border-border bg-surface-2 px-2 py-0.5 text-[10px] uppercase tracking-wide text-text-tertiary"
              >
                {tag}
              </span>
            ))}
          </div>
        ) : null}
      </div>

      <div className="flex items-center gap-2">
        <Button
          type="button"
          variant="ghost"
          onClick={() => setEditOpen(true)}
          className="h-7 px-2 text-xs text-text-secondary hover:bg-surface-2 hover:text-text-primary"
        >
          <Pencil className="mr-1.5 h-3 w-3" />
          Edit
        </Button>
        <VideoActionsMenu
          videoId={video.id}
          videoTitle={video.title}
          onEdit={() => setEditOpen(true)}
        />
      </div>

      <EditVideoDialog
        video={video}
        open={editOpen}
        onOpenChange={setEditOpen}
      />
    </div>
  );
}
