"use client";
import { use } from "react";
import { useQuery } from "@tanstack/react-query";
import { apiRequest } from "@/lib/api/client";
import { useVideo } from "@/hooks/use-videos";
import { useAuthStore } from "@/stores/auth-store";
import { QueryError } from "@/components/common/query-error";
import type { QuizAnalytics } from "@/types/quiz";
export default function Page({
  params,
}: {
  params: Promise<{ videoId: string }>;
}) {
  const { videoId } = use(params);
  const video = useVideo(videoId);
  const user = useAuthStore((s) => s.user);
  const allowed =
    !!video.data &&
    !!user &&
    (video.data.userId === user.id || user.role === "ADMIN");
  const quiz = useQuery({
    queryKey: ["analytics", "quiz", videoId],
    enabled: allowed,
    queryFn: () =>
      apiRequest<{ id: string }>({
        url: "/quizzes/video/" + videoId,
        method: "GET",
      }),
    retry: 0,
  });
  const analytics = useQuery({
    queryKey: ["analytics", quiz.data?.id],
    enabled: allowed && !!quiz.data?.id,
    queryFn: () =>
      apiRequest<QuizAnalytics>({
        url: "/quizzes/analytics/" + quiz.data?.id,
        method: "GET",
      }),
    retry: 0,
  });
  if (video.isLoading) return <p role="status">Checking lecture access…</p>;
  if (!allowed)
    return (
      <p className="py-8 text-text-secondary">
        Lesson analytics are available to the lecture owner and administrators.
      </p>
    );
  return (
    <div className="space-y-6">
      <p className="eyebrow">Owner view</p>
      <h2 className="text-xl font-medium">Lesson analytics</h2>
      {quiz.isError ? (
        <QueryError
          title="A published quiz is needed for analytics."
          retry={() => void quiz.refetch()}
        />
      ) : analytics.isError ? (
        <QueryError retry={() => void analytics.refetch()} />
      ) : analytics.isLoading || quiz.isLoading ? (
        <p role="status">Loading analytics…</p>
      ) : analytics.data?.totalAttempts ? (
        <>
          <dl className="grid grid-cols-2 gap-6">
            {[
              ["Attempts", analytics.data.totalAttempts],
              ["Average score", Math.round(analytics.data.avgScore) + "%"],
              ["Pass rate", Math.round(analytics.data.passRate) + "%"],
              ["Average time", analytics.data.avgTimeTaken + " seconds"],
            ].map(([label, value]) => (
              <div key={label} className="border-b border-border pb-5">
                <dt className="text-sm text-text-secondary">{label}</dt>
                <dd className="mt-3 text-2xl tabular-nums">{value}</dd>
              </div>
            ))}
          </dl>
          <p className="text-xs text-text-tertiary">
            Updated {new Date(analytics.data.updatedAt).toLocaleString()}
          </p>
        </>
      ) : (
        <p className="py-8 text-text-secondary">
          Not enough attempts yet. Saved quiz submissions will build this view.
        </p>
      )}
    </div>
  );
}
