"use client";

import { useState } from "react";
import { useLessonSeek } from "@/components/videos/lesson-workspace";
import { Clock } from "lucide-react";

import { formatDuration } from "@/lib/utils";
import type { Notes } from "@/types/ai";

interface NotesViewerProps {
  notes: Notes;
}

export function NotesViewer({ notes }: NotesViewerProps) {
  const seek = useLessonSeek();
  const [search, setSearch] = useState("");
  const [copied, setCopied] = useState(false);
  const sections = notes.sections.filter((section) =>
    (section.heading + " " + section.bulletPoints.join(" "))
      .toLowerCase()
      .includes(search.toLowerCase()),
  );
  return (
    <div className="space-y-8">
      <div className="flex flex-wrap gap-3">
        <label className="flex-1">
          <span className="sr-only">Search notes</span>
          <input
            className="w-full rounded-md border border-border bg-background px-3 py-2"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Find a concept in these notes…"
          />
        </label>
        <button
          type="button"
          className="quiet-link rounded border border-border"
          onClick={async () => {
            try {
              await navigator.clipboard.writeText(
                notes.sections
                  .map((s) => s.heading + "\n" + s.bulletPoints.join("\n"))
                  .join("\n\n"),
              );
              setCopied(true);
            } catch {
              setCopied(false);
            }
          }}
        >
          {copied ? "Copied" : "Copy notes"}
        </button>
      </div>
      {sections.length === 0 && (
        <p role="status">No notes match your search.</p>
      )}
      {notes.title ? (
        <p className="text-sm font-medium text-text-primary">{notes.title}</p>
      ) : null}

      {notes.sections.map((section, idx) => (
        <div key={idx} className="space-y-3">
          <div className="flex items-center gap-2">
            {typeof section.startTimeSeconds === "number" ? (
              <button
                type="button"
                className="inline-flex items-center gap-1 rounded-md border border-border bg-surface-2 px-2 py-0.5 font-mono text-[10px] text-text-secondary transition-colors hover:border-accent hover:text-accent"
                onClick={() => seek(section.startTimeSeconds ?? 0)}
                title={`Jump to ${formatDuration(section.startTimeSeconds)}`}
              >
                <Clock className="h-3 w-3" />
                {formatDuration(section.startTimeSeconds)}
              </button>
            ) : null}
            <h3 className="text-sm font-medium text-text-primary">
              {section.heading}
            </h3>
          </div>
          <ul className="space-y-1.5 pl-1">
            {section.bulletPoints.map((bp, j) => (
              <li
                key={j}
                className="flex gap-3 text-sm leading-relaxed text-text-secondary"
              >
                <span className="mt-2 h-1 w-1 shrink-0 rounded-full bg-text-tertiary" />
                <span>{bp}</span>
              </li>
            ))}
          </ul>
        </div>
      ))}

      <div className="border-t border-border pt-3">
        <p className="text-[10px] text-text-tertiary">
          {notes.cached ? "From cache · " : ""}
          {notes.llmProvider ?? "AI"} · {notes.processingTimeMs ?? 0} ms
        </p>
      </div>
    </div>
  );
}
