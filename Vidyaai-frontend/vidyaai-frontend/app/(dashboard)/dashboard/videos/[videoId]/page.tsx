"use client";
import { use } from "react";
import Link from "next/link";
import { useVideo } from "@/hooks/use-videos";
export default function Page({
  params,
}: {
  params: Promise<{ videoId: string }>;
}) {
  const { videoId } = use(params);
  const { data: video } = useVideo(videoId);
  return (
    <div>
      <p className="eyebrow">Your lecture workspace</p>
      <h2 className="mt-4 text-xl font-medium">Make room for understanding.</h2>
      <p className="mt-5 whitespace-pre-wrap text-base leading-8 text-text-secondary">
        {video?.description ||
          "Explore the key ideas, revisit the notes, or bring a question to your lecture tutor."}
      </p>
      <div className="mt-8 divide-y divide-border border-y border-border">
        {[
          ["summary", "Summary", "Find the key ideas."],
          ["notes", "Notes", "Revisit concepts at your pace."],
          ["quiz", "Practice", "Check your understanding."],
          ["chat", "Ask AI", "Explore questions about this lecture."],
        ].map(([path, title, detail]) => (
          <Link
            key={path}
            href={"/dashboard/videos/" + videoId + "/" + path}
            className="flex min-h-20 items-center justify-between gap-4 py-4"
          >
            <div>
              <h3 className="font-medium">{title}</h3>
              <p className="mt-1 text-sm text-text-secondary">{detail}</p>
            </div>
            <span aria-hidden="true">→</span>
          </Link>
        ))}
      </div>
      <p className="mt-5 text-xs text-text-tertiary">
        Each material has its own availability. Open a section to see its
        preparation status.
      </p>
    </div>
  );
}
