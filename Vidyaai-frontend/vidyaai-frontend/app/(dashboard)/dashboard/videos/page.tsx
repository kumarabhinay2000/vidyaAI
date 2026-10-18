"use client";

import { useMemo, useState } from "react";
import Link from "next/link";
import { Search, Upload, Video as VideoIcon } from "lucide-react";

import { EmptyState } from "@/components/common/empty-state";
import { PageHeader } from "@/components/common/page-header";
import { PageLoadingSkeleton } from "@/components/common/loading-skeleton";
import { VideoGrid } from "@/components/videos/video-grid";
import { Input } from "@/components/ui/input";
import { useMyVideos } from "@/hooks/use-videos";

type Filter = "all" | "ready" | "processing" | "failed";

const FILTERS: Array<{ key: Filter; label: string }> = [
  { key: "all", label: "All" },
  { key: "ready", label: "Ready" },
  { key: "processing", label: "Preparing" },
  { key: "failed", label: "Needs attention" },
];

export default function VideosPage() {
  const videosQuery = useMyVideos();
  const [query, setQuery] = useState("");
  const [filter, setFilter] = useState<Filter>("all");

  const filtered = useMemo(() => {
    const all = videosQuery.data ?? [];
    const q = query.trim().toLowerCase();

    return all.filter((v) => {
      const matchesQuery = q === "" || v.title.toLowerCase().includes(q);
      const matchesFilter =
        filter === "all"
          ? true
          : filter === "ready"
            ? v.status === "COMPLETED"
            : filter === "processing"
              ? v.status === "PROCESSING" ||
                v.status === "UPLOADING" ||
                v.status === "PENDING" ||
                v.status === "UPLOADED"
              : v.status === "FAILED";
      return matchesQuery && matchesFilter;
    });
  }, [videosQuery.data, query, filter]);

  return (
    <>
      <PageHeader
        title="My lectures"
        description="Your lectures, ready to revisit."
        actions={
          <Link
            href="/dashboard/videos/upload"
            className="inline-flex items-center gap-1.5 rounded-md bg-accent px-3 py-1.5 text-xs font-medium text-accent-fg transition-colors hover:bg-accent-hover"
          >
            <Upload className="h-3 w-3" />
            Upload
          </Link>
        }
      />

      <div className="mt-5 flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
        <div className="relative w-full sm:max-w-sm">
          <Search className="absolute left-2.5 top-1/2 h-3.5 w-3.5 -translate-y-1/2 text-text-tertiary" />
          <Input
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            aria-label="Search lectures by title"
            placeholder="Search by title…"
            className="h-9 border-border bg-surface-2 pl-8 text-sm placeholder:text-text-tertiary focus-visible:border-accent focus-visible:ring-0"
          />
        </div>

        <div className="flex items-center gap-1">
          {FILTERS.map((f) => (
            <button
              key={f.key}
              type="button"
              onClick={() => setFilter(f.key)}
              aria-pressed={filter === f.key}
              className={
                filter === f.key
                  ? "min-h-11 rounded-md bg-surface-2 px-2.5 py-1 text-sm text-text-primary"
                  : "min-h-11 rounded-md px-2.5 py-1 text-sm text-text-tertiary transition-colors hover:bg-surface-2 hover:text-text-secondary"
              }
            >
              {f.label}
            </button>
          ))}
        </div>
      </div>

      <div className="mt-6">
        {videosQuery.isLoading ? (
          <PageLoadingSkeleton />
        ) : videosQuery.isError ? (
          <EmptyState
            icon={VideoIcon}
            title="Could not load your videos"
            description="Please refresh the page or try again shortly."
          />
        ) : (videosQuery.data ?? []).length === 0 ? (
          <EmptyState
            icon={VideoIcon}
            title="No videos yet"
            description="Upload a file or paste a YouTube URL to get started."
            actionLabel="Upload your first video"
            actionHref="/dashboard/videos/upload"
          />
        ) : filtered.length === 0 ? (
          <EmptyState
            icon={Search}
            title="No matches"
            description="Try a different search term or filter."
          />
        ) : (
          <VideoGrid videos={filtered} />
        )}
      </div>
    </>
  );
}
