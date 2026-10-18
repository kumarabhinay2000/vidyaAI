"use client";

import { QueryError } from "@/components/common/query-error";
import { isApiError } from "@/lib/api";
import { use } from "react";
import { FileText, RefreshCw } from "lucide-react";

import { AIEmptyState } from "@/components/ai/ai-empty-state";
import { NotesViewer } from "@/components/ai/notes-viewer";
import { SectionCard } from "@/components/ai/section-card";
import { PageLoadingSkeleton } from "@/components/common/loading-skeleton";
import { Button } from "@/components/ui/button";
import { useGenerateNotes, useNotes } from "@/hooks/use-ai";

interface PageProps {
  params: Promise<{ videoId: string }>;
}

export default function VideoNotesPage({ params }: PageProps) {
  const { videoId } = use(params);
  const notesQuery = useNotes(videoId);
  const mutation = useGenerateNotes();

  const hasNotes = notesQuery.isSuccess && notesQuery.data;

  const handleGenerate = () => {
    if (
      hasNotes &&
      !window.confirm(
        "Regenerate these notes? Existing text stays visible while preparing.",
      )
    )
      return;
    mutation.mutate({
      videoId,
      language: "ENGLISH",
      includeTimestamps: true,
      useCache: true,
      forceRegenerate: Boolean(hasNotes),
    });
  };

  return (
    <div className="space-y-6">
      <SectionCard
        title="Notes"
        description={
          hasNotes
            ? "Structured notes with timestamps."
            : "Turn the video into structured, revision-friendly notes."
        }
        actions={
          <Button
            type="button"
            disabled={mutation.isPending}
            onClick={handleGenerate}
            className="h-8 bg-accent px-3 text-xs font-medium text-accent-fg transition-colors hover:bg-accent-hover"
          >
            {mutation.isPending ? (
              <>
                <RefreshCw className="mr-2 h-3.5 w-3.5 animate-spin" />
                Working…
              </>
            ) : notesQuery.isError &&
              !(
                isApiError(notesQuery.error) && notesQuery.error.status === 404
              ) ? (
              <QueryError retry={() => void notesQuery.refetch()} />
            ) : hasNotes ? (
              <>
                <RefreshCw className="mr-2 h-3.5 w-3.5" />
                Regenerate
              </>
            ) : (
              <>
                <FileText className="mr-2 h-3.5 w-3.5" />
                Generate
              </>
            )}
          </Button>
        }
      >
        {notesQuery.isLoading ? (
          <PageLoadingSkeleton />
        ) : hasNotes ? (
          <NotesViewer notes={notesQuery.data} />
        ) : (
          <AIEmptyState
            title="No notes yet"
            description="Generate structured notes from the transcript. Timestamps included."
            ctaLabel="Generate notes"
            loading={mutation.isPending}
            onGenerate={handleGenerate}
          />
        )}
      </SectionCard>
    </div>
  );
}
