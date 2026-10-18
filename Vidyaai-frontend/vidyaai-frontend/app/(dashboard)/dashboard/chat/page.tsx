"use client";
import { useState } from "react";
import Link from "next/link";
import { BookOpen, Search } from "lucide-react";
import { useMyVideos } from "@/hooks/use-videos";
import { ChatWindow } from "@/components/ai/chat-window";
import { PageHeader } from "@/components/common/page-header";
import { QueryError } from "@/components/common/query-error";
export default function Page() {
  const query = useMyVideos();
  const [id, setId] = useState("");
  const [search, setSearch] = useState("");
  const videos = query.data ?? [];
  const active = videos.find((v) => v.id === id);
  return (
    <div className="space-y-7">
      <PageHeader
        title="Which lecture are we studying?"
        description="Choose a lecture before asking a question. Each conversation stays within that video."
      />
      {query.isError ? (
        <QueryError retry={() => void query.refetch()} />
      ) : (
        <>
          <label className="flex max-w-xl items-center gap-3 rounded-md border border-border bg-surface px-4">
            <Search className="h-4 w-4 text-text-tertiary" />
            <span className="sr-only">Search your lectures</span>
            <input
              className="min-h-11 w-full bg-transparent outline-none"
              placeholder="Search your lectures"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
            />
          </label>
          {query.isLoading ? (
            <p role="status">Loading your lectures…</p>
          ) : videos.length ? (
            <div className="grid gap-3 sm:grid-cols-2">
              {videos
                .filter((v) =>
                  v.title.toLowerCase().includes(search.toLowerCase()),
                )
                .map((v) => (
                  <button
                    key={v.id}
                    aria-pressed={v.id === id}
                    onClick={() => setId(v.id)}
                    className={
                      "flex items-center gap-3 rounded-lg border p-4 text-left " +
                      (id === v.id
                        ? "border-accent bg-accent-muted"
                        : "border-border bg-surface")
                    }
                  >
                    <BookOpen className="h-5 w-5 shrink-0 text-accent" />
                    <span className="font-medium">{v.title}</span>
                  </button>
                ))}
            </div>
          ) : (
            <div className="rounded-lg border border-border p-8">
              <p className="text-text-secondary">
                Add a lecture to start your first conversation.
              </p>
              <Link
                className="primary-link mt-5"
                href="/dashboard/videos/upload"
              >
                Add lecture
              </Link>
            </div>
          )}
          {active ? (
            <section className="rounded-lg border border-border bg-surface p-5">
              <p className="mb-5 border-b border-border pb-4 text-sm">
                Questions about <strong>{active.title}</strong>
              </p>
              <ChatWindow key={id} videoId={id} />
            </section>
          ) : (
            <div className="rounded-lg border border-border bg-surface p-6">
              <label
                htmlFor="inactive-composer"
                className="text-sm text-text-secondary"
              >
                Choose a lecture above to enable the tutor.
              </label>
              <textarea
                id="inactive-composer"
                disabled
                placeholder="Ask about your lecture…"
                className="mt-4 w-full rounded-md border border-border bg-surface-2 p-4"
              />
            </div>
          )}
        </>
      )}
    </div>
  );
}
