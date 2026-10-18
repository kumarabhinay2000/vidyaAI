"use client";
import { createContext, useContext, useRef } from "react";
import Link from "next/link";
import { usePathname } from "next/navigation";
import { useVideo } from "@/hooks/use-videos";
import { VideoPlayer, type VideoPlayerHandle } from "./video-player";
import { VideoDetailTabs } from "./video-detail-tabs";
import { VideoStatusPill } from "./video-status-pill";
import { ProcessingStatus } from "./processing-status";
import { QueryError } from "@/components/common/query-error";
import { formatDuration } from "@/lib/utils";

const SeekContext = createContext<(time: number) => void>(() => {});
export const useLessonSeek = () => useContext(SeekContext);
export function LessonWorkspace({
  videoId,
  children,
}: {
  videoId: string;
  children: React.ReactNode;
}) {
  const query = useVideo(videoId);
  const player = useRef<VideoPlayerHandle>(null);
  const pathname = usePathname();
  const video = query.data;
  if (query.isLoading)
    return (
      <p role="status" className="py-16 text-text-secondary">
        Opening your lecture…
      </p>
    );
  if (query.isError || !video)
    return (
      <QueryError
        title="This lecture is unavailable."
        retry={() => void query.refetch()}
      />
    );
  const seek = (time: number) => {
    player.current?.seek(time);
  };
  return (
    <SeekContext.Provider value={seek}>
      <div className="space-y-6">
        <Link href="/dashboard/videos" className="quiet-link pl-0">
          ← My lectures
        </Link>
        <div className="flex flex-wrap items-start justify-between gap-4">
          <div>
            <h1 className="text-2xl font-semibold tracking-tight">
              {video.title}
            </h1>
            <p className="mt-3 text-sm text-text-secondary">
              {video.sourceType === "YOUTUBE"
                ? "YouTube lecture"
                : "Uploaded lecture"}
              {video.durationSeconds
                ? " · " + formatDuration(video.durationSeconds)
                : ""}
            </p>
          </div>
          <VideoStatusPill status={video.status} />
        </div>
        <VideoDetailTabs videoId={videoId} />
        <div className="grid min-w-0 gap-6 xl:grid-cols-[minmax(0,42fr)_minmax(0,58fr)]">
          <aside className="min-w-0 space-y-5 xl:sticky xl:top-24 xl:self-start">
            <VideoPlayer ref={player} video={video} />
            {video.status !== "COMPLETED" && (
              <ProcessingStatus videoId={videoId} />
            )}
            <div>
              <h2 className="mb-3 text-sm font-medium">Lecture chapters</h2>
              {video.chapters?.length ? (
                <div className="divide-y divide-border">
                  {video.chapters.map((c) => (
                    <button
                      key={c.id}
                      onClick={() => seek(c.startTime)}
                      className="flex min-h-11 w-full gap-4 py-3 text-left text-sm"
                    >
                      <span className="font-mono text-xs text-accent">
                        {formatDuration(c.startTime)}
                      </span>
                      {c.title}
                    </button>
                  ))}
                </div>
              ) : (
                <p className="text-sm text-text-secondary">
                  Chapters will appear when they are available.
                </p>
              )}
            </div>
          </aside>
          <section
            key={pathname}
            className="min-w-0 rounded-lg border border-border bg-surface p-5 sm:p-7"
          >
            {children}
          </section>
        </div>
      </div>
    </SeekContext.Provider>
  );
}
