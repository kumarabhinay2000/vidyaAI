"use client";

import { useState } from "react";
import Link from "next/link";
import { ArrowLeft } from "lucide-react";

import { PageHeader } from "@/components/common/page-header";
import { UploadTabs } from "@/components/videos/upload-tabs";

export default function UploadVideoPage() {
  const [mode, setMode] = useState<"file" | "youtube">("youtube");

  return (
    <>
      <div className="mb-2">
        <Link
          href="/dashboard/videos"
          className="inline-flex items-center gap-1 text-xs text-text-tertiary transition-colors hover:text-text-primary"
        >
          <ArrowLeft className="h-3 w-3" />
          Back to videos
        </Link>
      </div>

      <PageHeader
        title="Add a lecture"
        description="Paste a YouTube link or upload a lecture video to prepare a study kit."
      />

      <div className="mt-6">
        <UploadTabs mode={mode} onModeChange={setMode} />
      </div>
    </>
  );
}
