"use client";
import { useLeaderboard, useMyStats } from "@/hooks/use-quizzes";
import { PageHeader } from "@/components/common/page-header";
import { QueryError } from "@/components/common/query-error";
export default function Page() {
  const query = useLeaderboard(20);
  const stats = useMyStats();
  return (
    <div className="space-y-6">
      <PageHeader
        title="Leaderboard"
        description="All-time points earned through practice."
      />
      {query.isLoading ? (
        <p role="status">Loading rankings…</p>
      ) : query.isError ? (
        <QueryError retry={() => void query.refetch()} />
      ) : query.data?.length ? (
        <div className="overflow-x-auto rounded-lg border border-border bg-surface">
          <table className="w-full min-w-[520px] text-left text-sm">
            <caption className="sr-only">
              Top learners, ranked by all-time points
            </caption>
            <thead className="border-b border-border text-text-secondary">
              <tr>
                {["Rank", "Learner", "Quizzes", "Average score", "Points"].map(
                  (h) => (
                    <th key={h} className="p-4 font-medium">
                      {h}
                    </th>
                  ),
                )}
              </tr>
            </thead>
            <tbody>
              {query.data.map((e) => (
                <tr
                  key={e.userId}
                  className={
                    "border-b border-border last:border-0 " +
                    (e.userId === stats.data?.userId ? "bg-accent-muted" : "")
                  }
                >
                  <td className="p-4 tabular-nums">{e.rank}</td>
                  <td className="p-4">
                    {e.userId === stats.data?.userId
                      ? "You"
                      : "Learner " + e.userId.slice(0, 6)}
                  </td>
                  <td className="p-4">{e.quizzesTaken}</td>
                  <td className="p-4">{Math.round(e.avgScore ?? 0)}%</td>
                  <td className="p-4 font-medium">{e.totalPoints}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      ) : (
        <p className="py-12 text-text-secondary">
          No rankings yet. Your saved practice results will help build this
          list.
        </p>
      )}
      {stats.data?.rank && (
        <p className="text-sm text-text-secondary">
          Your all-time rank: {stats.data.rank}
        </p>
      )}
    </div>
  );
}
