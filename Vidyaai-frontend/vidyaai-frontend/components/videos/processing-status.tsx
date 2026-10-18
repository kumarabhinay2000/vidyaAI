"use client";
import { useVideoStatus } from "@/hooks/use-videos";
import { QueryError } from "@/components/common/query-error";
export function ProcessingStatus({
  videoId,
  className,
}: {
  videoId: string;
  className?: string;
}) {
  const query = useVideoStatus(videoId);
  const data = query.data;
  if (query.isError)
    return (
      <QueryError
        title="Preparation status is unavailable."
        retry={() => void query.refetch()}
      />
    );
  if (!data)
    return (
      <p role="status" className="text-sm text-text-secondary">
        Checking preparation status…
      </p>
    );
  const ready = data.status === "COMPLETED";
  const failed = data.status === "FAILED";
  return (
    <div className={className}>
      <h3 className="text-sm font-medium">
        {ready
          ? "Ready to revisit"
          : failed
            ? "Preparation needs attention"
            : "Preparing your lecture"}
      </h3>
      <ol className="mt-4 space-y-3 text-sm">
        <li>✓ Upload received</li>
        <li className="text-text-secondary">
          {ready
            ? "✓ Transcript prepared"
            : failed
              ? "Transcript / study material needs attention"
              : "Transcript extraction · awaiting completion"}
        </li>
        <li className="text-text-secondary">
          {ready ? "✓ Study materials prepared" : "Study materials · pending"}
        </li>
      </ol>
      <p className="mt-4 text-xs leading-6 text-text-tertiary">
        {data.message ||
          "Status is refreshed while your lecture is being prepared."}
      </p>
      {failed && (
        <button
          className="quiet-link mt-3 border border-border"
          onClick={() => void query.refetch()}
        >
          Refresh status
        </button>
      )}
    </div>
  );
}
