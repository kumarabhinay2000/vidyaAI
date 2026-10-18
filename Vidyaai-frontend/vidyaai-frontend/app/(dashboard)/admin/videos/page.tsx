"use client";

import Link from "next/link";
import { ExternalLink, Video } from "lucide-react";

import { QueryError } from "@/components/common/query-error";
import { EmptyState } from "@/components/common/empty-state";
import { VideoStatusPill } from "@/components/videos/video-status-pill";
import { useMyVideos } from "@/hooks/use-videos";
import { formatRelativeTime } from "@/lib/utils";

export default function AdminVideosPage() {
  const videosQuery = useMyVideos();
  const videos = videosQuery.data ?? [];

  if (videosQuery.isLoading) {
    return (
      <div className="h-64 animate-pulse rounded-lg border border-border bg-surface/40" />
    );
  }

  if (videosQuery.isError)
    return <QueryError retry={() => void videosQuery.refetch()} />;
  if (videos.length === 0) {
    return (
      <EmptyState
        icon={Video}
        title="No videos"
        description="No lectures are available to your current account."
      />
    );
  }

  return (
    <div className="overflow-x-auto rounded-lg border border-border bg-surface/40">
      <div className="grid min-w-[600px] grid-cols-[1fr_120px_100px_80px] gap-3 border-b border-border px-4 py-2 text-[10px] uppercase tracking-widest text-text-tertiary">
        <span>Title</span>
        <span>Status</span>
        <span>Uploaded</span>
        <span className="text-right">Open</span>
      </div>
      {videos.map((v) => (
        <div
          key={v.id}
          className="grid grid-cols-[1fr_120px_100px_80px] items-center gap-3 border-b border-border px-4 py-3 last:border-b-0"
        >
          <span className="truncate text-sm text-text-primary">{v.title}</span>
          <VideoStatusPill status={v.status} />
          <span className="text-[11px] text-text-tertiary">
            {formatRelativeTime(v.createdAt)}
          </span>
          <div className="flex justify-end">
            <Link
              href={`/dashboard/videos/${v.id}`}
              className="inline-flex h-7 items-center gap-1.5 rounded-md px-2 text-xs text-text-secondary transition-colors hover:bg-surface-2 hover:text-text-primary"
            >
              Open
              <ExternalLink className="h-3 w-3" />
            </Link>
          </div>
        </div>
      ))}
    </div>
  );
}
