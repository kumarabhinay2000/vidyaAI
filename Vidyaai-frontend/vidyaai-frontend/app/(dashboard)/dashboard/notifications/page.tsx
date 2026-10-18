"use client";
import Link from "next/link";
import { useMyVideos } from "@/hooks/use-videos";
import { PageHeader } from "@/components/common/page-header";
import { QueryError } from "@/components/common/query-error";
import { VideoStatusPill } from "@/components/videos/video-status-pill";
import { formatRelativeTime } from "@/lib/utils";
export default function Page() {
  const query = useMyVideos();
  return (
    <div className="space-y-6">
      <PageHeader
        title="Lecture updates"
        description="Current lecture statuses. This list does not provide persistent read/unread tracking."
      />
      {query.isError ? (
        <QueryError retry={() => void query.refetch()} />
      ) : query.isLoading ? (
        <p role="status">Loading updates…</p>
      ) : query.data?.length ? (
        <div className="divide-y divide-border">
          {query.data.map((v) => (
            <Link
              key={v.id}
              href={"/dashboard/videos/" + v.id}
              className="flex min-h-24 items-center justify-between gap-5 py-5"
            >
              <div>
                <h2 className="font-medium">{v.title}</h2>
                <p className="mt-2 text-sm text-text-secondary">
                  {v.status === "FAILED"
                    ? "Preparation needs attention. Open the lecture for its status."
                    : v.status === "COMPLETED"
                      ? "Your lecture is ready to revisit."
                      : "Preparation is in progress."}
                </p>
                <p className="mt-2 text-xs text-text-tertiary">
                  Added {formatRelativeTime(v.createdAt)}
                </p>
              </div>
              <VideoStatusPill status={v.status} />
            </Link>
          ))}
        </div>
      ) : (
        <p className="py-12 text-text-secondary">
          Nothing here yet. Add a lecture to see its preparation status.
        </p>
      )}
    </div>
  );
}
