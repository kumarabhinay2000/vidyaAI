"use client";

import { useEffect } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Loader2 } from "lucide-react";
import { z } from "zod";

import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { FieldError } from "@/components/ui/field-error";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { useUpdateVideo } from "@/hooks/use-videos";
import type { Video } from "@/types/video";

const schema = z.object({
  title: z
    .string()
    .min(1, "Title is required")
    .max(500, "Title must be 500 characters or less"),
  description: z
    .string()
    .max(5000, "Description must be 5000 characters or less")
    .optional()
    .or(z.literal("")),
  language: z.string().max(10).optional().or(z.literal("")),
});

type Values = z.infer<typeof schema>;

interface EditVideoDialogProps {
  video: Video;
  open: boolean;
  onOpenChange: (open: boolean) => void;
}

export function EditVideoDialog({
  video,
  open,
  onOpenChange,
}: EditVideoDialogProps) {
  const mutation = useUpdateVideo(video.id);
  const form = useForm<Values>({
    resolver: zodResolver(schema),
    defaultValues: {
      title: video.title,
      description: video.description ?? "",
      language: video.language ?? "ENGLISH",
    },
  });

  useEffect(() => {
    if (open) {
      form.reset({
        title: video.title,
        description: video.description ?? "",
        language: video.language ?? "ENGLISH",
      });
    }
  }, [open, video, form]);

  const onSubmit = form.handleSubmit((values) => {
    mutation.mutate(
      {
        title: values.title.trim(),
        description: values.description?.trim() || undefined,
        language: values.language || undefined,
      },
      {
        onSuccess: () => onOpenChange(false),
      },
    );
  });

  const pending = mutation.isPending;

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="border-border bg-surface-2 sm:max-w-lg">
        <DialogHeader>
          <DialogTitle className="text-text-primary">
            Edit video details
          </DialogTitle>
          <DialogDescription className="text-text-secondary">
            Only the title, description, and language are editable.
          </DialogDescription>
        </DialogHeader>

        <form onSubmit={onSubmit} className="space-y-4">
          <div>
            <Label htmlFor="title" className="text-xs text-text-secondary">
              Title
            </Label>
            <Input
              id="title"
              disabled={pending}
              className="mt-1.5 h-9 border-border bg-background text-sm focus-visible:border-accent focus-visible:ring-0"
              {...form.register("title")}
            />
            <FieldError message={form.formState.errors.title?.message} />
          </div>

          <div>
            <Label
              htmlFor="description"
              className="text-xs text-text-secondary"
            >
              Description
            </Label>
            <textarea
              id="description"
              rows={4}
              disabled={pending}
              className="mt-1.5 w-full rounded-md border border-border bg-background px-2.5 py-2 text-sm text-text-primary placeholder:text-text-tertiary focus:border-accent focus:outline-none"
              {...form.register("description")}
            />
            <FieldError message={form.formState.errors.description?.message} />
          </div>

          <div>
            <Label htmlFor="language" className="text-xs text-text-secondary">
              Language
            </Label>
            <select
              id="language"
              disabled={pending}
              className="mt-1.5 h-9 w-full rounded-md border border-border bg-background px-2 text-sm text-text-primary focus:border-accent focus:outline-none"
              {...form.register("language")}
            >
              <option value="ENGLISH">English</option>
              <option value="HINDI">Hindi</option>
              <option value="TAMIL">Tamil</option>
              <option value="TELUGU">Telugu</option>
              <option value="BENGALI">Bengali</option>
              <option value="MARATHI">Marathi</option>
            </select>
          </div>

          <DialogFooter className="pt-2">
            <Button
              type="button"
              variant="ghost"
              disabled={pending}
              onClick={() => onOpenChange(false)}
              className="h-9 text-xs text-text-secondary hover:bg-surface-3 hover:text-text-primary"
            >
              Cancel
            </Button>
            <Button
              type="submit"
              disabled={pending}
              className="h-9 bg-accent px-4 text-xs font-medium text-accent-fg transition-colors hover:bg-accent-hover"
            >
              {pending ? (
                <>
                  <Loader2 className="mr-2 h-3.5 w-3.5 animate-spin" />
                  Saving…
                </>
              ) : (
                "Save changes"
              )}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
