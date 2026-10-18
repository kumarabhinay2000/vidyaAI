"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { MoreHorizontal, Pencil, Trash2 } from "lucide-react";

import { ConfirmDialog } from "@/components/common/confirm-dialog";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { useDeleteVideo } from "@/hooks/use-videos";

interface VideoActionsMenuProps {
  videoId: string;
  videoTitle: string;
  onEdit?: () => void;
}

export function VideoActionsMenu({
  videoId,
  videoTitle,
  onEdit,
}: VideoActionsMenuProps) {
  const router = useRouter();
  const deleteMutation = useDeleteVideo();
  const [confirmOpen, setConfirmOpen] = useState(false);

  const handleDelete = async () => {
    deleteMutation.mutate(videoId, {
      onSuccess: () => {
        setConfirmOpen(false);
        router.replace("/dashboard/videos");
      },
    });
  };

  return (
    <>
      <DropdownMenu>
        <DropdownMenuTrigger
          type="button"
          aria-label="More actions"
          className="flex h-7 w-7 items-center justify-center rounded-md text-text-tertiary transition-colors hover:bg-surface-2 hover:text-text-primary"
        >
          <MoreHorizontal className="h-4 w-4" />
        </DropdownMenuTrigger>
        <DropdownMenuContent
          align="end"
          className="w-44 border-border bg-surface-2"
        >
          {onEdit ? (
            <>
              <DropdownMenuItem
                onSelect={(e) => {
                  e.preventDefault();
                  onEdit();
                }}
                className="flex items-center gap-2 text-text-secondary focus:bg-surface-3 focus:text-text-primary"
              >
                <Pencil className="h-3.5 w-3.5" />
                Edit details
              </DropdownMenuItem>
              <DropdownMenuSeparator className="bg-border" />
            </>
          ) : null}

          <DropdownMenuItem
            onSelect={(e) => {
              e.preventDefault();
              setConfirmOpen(true);
            }}
            className="flex items-center gap-2 text-danger focus:bg-danger-muted focus:text-danger"
          >
            <Trash2 className="h-3.5 w-3.5" />
            Delete video
          </DropdownMenuItem>
        </DropdownMenuContent>
      </DropdownMenu>

      <ConfirmDialog
        open={confirmOpen}
        onOpenChange={setConfirmOpen}
        title="Delete this video?"
        description={`"${videoTitle}" and all AI-generated content (summary, notes, quiz) will be removed. This cannot be undone.`}
        confirmLabel="Delete"
        destructive
        loading={deleteMutation.isPending}
        onConfirm={handleDelete}
      />
    </>
  );
}
