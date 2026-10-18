"use client";
import Link from "next/link";
import { ArrowRight, BookOpen, Plus } from "lucide-react";
import { useMyVideos } from "@/hooks/use-videos";
import { useMyAttempts } from "@/hooks/use-attempts";
import { PageHeader } from "@/components/common/page-header";
import { PageLoadingSkeleton } from "@/components/common/loading-skeleton";
import { QueryError } from "@/components/common/query-error";
import { formatDuration, formatRelativeTime } from "@/lib/utils";
import { VideoStatusPill } from "@/components/videos/video-status-pill";
export default function Page() {
  const query = useMyVideos();
  const attemptsQuery = useMyAttempts();
  const videos = query.data ?? [];
  const latest = videos[0];
  const attempts = attemptsQuery.data ?? [];
  return (
    <div className="space-y-8">
      <PageHeader
        title="Pick up where you left off"
        description="A little more understanding, one lecture at a time."
        actions={
          <Link
            href="/dashboard/videos/upload"
            className="primary-link text-sm"
          >
            <Plus className="h-4 w-4" />
            Add lecture
          </Link>
        }
      />
      {query.isLoading ? (
        <PageLoadingSkeleton />
      ) : query.isError ? (
        <QueryError
          title="We couldn’t load your lectures."
          retry={() => void query.refetch()}
        />
      ) : latest ? (
        <>
          <section className="grid gap-6 rounded-lg border border-border bg-surface p-6 md:grid-cols-[160px_1fr_auto] md:items-center">
            <div className="flex aspect-video items-center justify-center rounded-md bg-accent-muted text-accent">
              <BookOpen className="h-10 w-10" strokeWidth={1} />
            </div>
            <div>
              <p className="eyebrow">Continue studying</p>
              <h2 className="mt-3 text-xl font-medium">{latest.title}</h2>
              <p className="mt-3 text-sm text-text-secondary">
                {latest.durationSeconds
                  ? formatDuration(latest.durationSeconds)
                  : "Duration unavailable"}{" "}
                · Added {formatRelativeTime(latest.createdAt)}
              </p>
            </div>
            <Link
              className="primary-link text-sm"
              href={"/dashboard/videos/" + latest.id}
            >
              Open lesson
              <ArrowRight className="h-4 w-4" />
            </Link>
          </section>
          <section>
            <div className="mb-5 flex items-center justify-between">
              <h2 className="text-lg font-medium">Recent lectures</h2>
              <Link href="/dashboard/videos" className="quiet-link">
                View library →
              </Link>
            </div>
            <div className="divide-y divide-border border-y border-border">
              {videos.slice(0, 5).map((video) => (
                <Link
                  key={video.id}
                  href={"/dashboard/videos/" + video.id}
                  className="flex min-h-20 items-center justify-between gap-4 py-4"
                >
                  <div>
                    <p className="font-medium">{video.title}</p>
                    <p className="mt-1 text-sm text-text-secondary">
                      {video.sourceType === "YOUTUBE"
                        ? "YouTube lecture"
                        : "Uploaded lecture"}
                    </p>
                  </div>
                  <VideoStatusPill status={video.status} />
                </Link>
              ))}
            </div>
          </section>
        </>
      ) : (
        <section className="rounded-lg border border-dashed border-border px-6 py-16 text-center">
          <BookOpen className="mx-auto h-10 w-10 text-accent" />
          <h2 className="mt-5 text-2xl font-medium">
            Your next lecture starts here.
          </h2>
          <p className="mt-3 text-text-secondary">
            Add a video and build your first study kit.
          </p>
          <Link href="/dashboard/videos/upload" className="primary-link mt-6">
            Add your first lecture
          </Link>
          <Link href="/#sample-lesson" className="quiet-link ml-3">
            Explore a sample
          </Link>
        </section>
      )}
      <section className="border-t border-border pt-6">
        <div className="flex items-center justify-between">
          <h2 className="text-lg font-medium">Recent practice</h2>
          <Link href="/dashboard/progress" className="quiet-link">
            Your progress →
          </Link>
        </div>
        {attemptsQuery.isError ? (
          <QueryError
            title="Practice history is unavailable."
            retry={() => void attemptsQuery.refetch()}
          />
        ) : attempts.length ? (
          <ul className="mt-4 divide-y divide-border">
            {attempts.slice(0, 3).map((a) => (
              <li
                key={a.id}
                className="flex justify-between gap-4 py-4 text-sm"
              >
                <span>
                  {a.status === "COMPLETED"
                    ? "Practice completed"
                    : "Practice in progress"}
                </span>
                <span className="tabular-nums text-text-secondary">
                  {a.status === "COMPLETED"
                    ? Math.round(a.percentage ?? 0) + "%"
                    : "Not submitted"}
                </span>
              </li>
            ))}
          </ul>
        ) : (
          <p className="mt-4 text-sm text-text-secondary">
            Your saved quiz attempts will appear here.
          </p>
        )}
      </section>
    </div>
  );
}
