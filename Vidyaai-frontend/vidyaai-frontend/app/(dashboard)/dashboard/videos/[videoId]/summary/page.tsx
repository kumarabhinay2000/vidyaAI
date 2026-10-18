"use client";
import { use, useState } from "react";
import { useSummary, useGenerateSummary } from "@/hooks/use-ai";
import { QueryError } from "@/components/common/query-error";
import type { SummaryType, LanguageCode } from "@/types/ai";
import { isApiError } from "@/lib/api";
export default function Page({
  params,
}: {
  params: Promise<{ videoId: string }>;
}) {
  const { videoId } = use(params);
  const query = useSummary(videoId);
  const mutation = useGenerateSummary();
  const [type, setType] = useState<SummaryType>("FIVE_POINT");
  const [language, setLanguage] = useState<LanguageCode>("ENGLISH");
  const summary = query.data;
  const missing = isApiError(query.error) && query.error.status === 404;
  function generate() {
    if (
      summary &&
      !window.confirm(
        "Regenerate this summary? The current text stays visible while it is prepared.",
      )
    )
      return;
    mutation.mutate({
      videoId,
      type,
      language,
      useCache: !summary,
      forceRegenerate: !!summary,
    });
  }
  return (
    <div className="space-y-6">
      <div>
        <p className="eyebrow">The important ideas</p>
        <h2 className="mt-3 text-xl font-medium">Lecture summary</h2>
      </div>
      <div className="flex flex-wrap items-end gap-3">
        <label className="text-sm text-text-secondary">
          Format
          <select
            className="mt-2 block rounded-md border border-border bg-background px-3"
            value={type}
            onChange={(e) => setType(e.target.value as SummaryType)}
          >
            <option value="FIVE_POINT">5 points</option>
            <option value="TEN_POINT">10 points</option>
            <option value="DETAILED">Detailed</option>
          </select>
        </label>
        <label className="text-sm text-text-secondary">
          Language
          <select
            className="mt-2 block rounded-md border border-border bg-background px-3"
            value={language}
            onChange={(e) => setLanguage(e.target.value as LanguageCode)}
          >
            {["ENGLISH", "HINDI", "TAMIL", "TELUGU", "BENGALI", "MARATHI"].map(
              (l) => (
                <option key={l} value={l}>
                  {l.charAt(0) + l.slice(1).toLowerCase()}
                </option>
              ),
            )}
          </select>
        </label>
        <button
          className={
            summary
              ? "quiet-link rounded-md border border-border"
              : "primary-link"
          }
          disabled={mutation.isPending}
          onClick={generate}
        >
          {mutation.isPending
            ? "Preparing…"
            : summary
              ? "Regenerate"
              : "Generate summary"}
        </button>
      </div>
      {mutation.isError && (
        <p role="alert" className="text-sm text-danger">
          {mutation.error.message}
        </p>
      )}
      {query.isLoading ? (
        <p role="status">Loading summary…</p>
      ) : summary ? (
        <>
          <p className="border-t border-border pt-4 text-xs text-text-tertiary">
            Showing {summary.type?.replaceAll("_", " ").toLowerCase()} ·{" "}
            {summary.language?.toLowerCase()}. Change the options and regenerate
            for another version.
          </p>
          {summary.title && (
            <h3 className="text-lg font-medium">{summary.title}</h3>
          )}
          <ol className="space-y-5">
            {(summary.points ?? []).map((p, i) => (
              <li key={i} className="flex gap-4 text-base leading-8">
                <span className="font-mono text-sm text-text-tertiary">
                  {String(i + 1).padStart(2, "0")}
                </span>
                <span>{p}</span>
              </li>
            ))}
          </ol>
          {!summary.points?.length && (
            <p className="whitespace-pre-wrap text-base leading-8">
              {summary.fullContent || "No summary text was returned."}
            </p>
          )}
        </>
      ) : query.isError && !missing ? (
        <QueryError retry={() => void query.refetch()} />
      ) : (
        <p className="py-8 text-text-secondary">
          This summary is not ready yet. Generate it once a transcript is
          available.
        </p>
      )}
    </div>
  );
}
