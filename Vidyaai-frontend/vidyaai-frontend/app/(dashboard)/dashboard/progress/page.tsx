"use client";
import Link from "next/link";
import { useMyAttempts } from "@/hooks/use-attempts";
import { useMyProgress } from "@/hooks/use-quizzes";
import { PageHeader } from "@/components/common/page-header";
import { QueryError } from "@/components/common/query-error";
import { ScoreTrendChart } from "@/components/charts/score-trend-chart";
export default function Page() {
  const attempts = useMyAttempts();
  const progress = useMyProgress();
  const completed = (attempts.data ?? []).filter(
    (a) => a.status === "COMPLETED",
  );
  return (
    <div className="space-y-8">
      <PageHeader
        title="Your learning, over time"
        description="A record of practice, progress, and ideas worth revisiting."
      />
      {attempts.isError ? (
        <QueryError
          title="Practice history is unavailable."
          retry={() => void attempts.refetch()}
        />
      ) : attempts.isLoading ? (
        <p role="status">Loading your practice history…</p>
      ) : completed.length ? (
        <section className="rounded-lg border border-border bg-surface p-6">
          <h2 className="mb-5 text-lg font-medium">Recent practice scores</h2>
          <ScoreTrendChart
            data={completed
              .slice(0, 10)
              .reverse()
              .map((a, i) => ({
                label: "#" + (i + 1),
                score: Math.round(a.percentage ?? 0),
              }))}
          />
          <p className="mt-3 text-xs text-text-secondary">
            Your latest {Math.min(10, completed.length)} saved attempts, oldest
            to newest. Scores shown as percentages.
          </p>
        </section>
      ) : (
        <p className="py-10 text-text-secondary">
          Your first completed quiz will start this learning journal.
        </p>
      )}
      <section>
        <h2 className="mb-5 text-lg font-medium">Lectures to revisit</h2>
        {progress.isError ? (
          <QueryError
            title="Lecture progress is unavailable."
            retry={() => void progress.refetch()}
          />
        ) : progress.data?.length ? (
          <div className="divide-y divide-border">
            {progress.data.map((p) => (
              <Link
                key={p.id}
                href={"/dashboard/videos/" + p.videoId}
                className="flex items-center justify-between gap-5 py-5"
              >
                <div>
                  <p className="font-medium">
                    {p.status === "COMPLETED"
                      ? "Completed lecture"
                      : "Keep practicing"}
                  </p>
                  <p className="mt-2 text-sm text-text-secondary">
                    {p.attemptsCount} attempt(s) · Best score {p.bestScore}
                  </p>
                </div>
                <span className="text-sm text-accent">Open lecture →</span>
              </Link>
            ))}
          </div>
        ) : (
          <p className="text-sm text-text-secondary">
            Saved lecture progress will appear here.
          </p>
        )}
      </section>
    </div>
  );
}
