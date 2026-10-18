"use client";

import { useState,useSyncExternalStore } from "react";
import { useRouter } from "next/navigation";
import { Loader2 } from "lucide-react";
import { Youtube } from "@/components/common/icons";

import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { FieldError } from "@/components/ui/field-error";
import { useRegisterYouTube } from "@/hooks/use-videos";

const YT_PATTERN =
    /^(https?:\/\/)?(www\.)?(youtube\.com\/watch\?v=|youtu\.be\/|youtube\.com\/embed\/|youtube\.com\/shorts\/)[\w-]{11}.*$/;

export function UploadYouTubeForm() {
    const router = useRouter();
    const mutation = useRegisterYouTube();

    const initialUrl=useSyncExternalStore(()=>()=>{},()=>new URLSearchParams(window.location.search).get("videoUrl")??"",()=>"");
    const [editedUrl,setUrl]=useState<string|null>(null);
    const url=editedUrl??initialUrl;
    const [title, setTitle] = useState("");
    const [description, setDescription] = useState("");
    const [autoProcess, setAutoProcess] = useState(true);
    const [errors, setErrors] = useState<{ url?: string }>({});

    const validate = (): boolean => {
        const next: typeof errors = {};
        const trimmed = url.trim();
        if (!trimmed) next.url = "YouTube URL is required.";
        else if (!YT_PATTERN.test(trimmed)) next.url = "Enter a valid YouTube URL.";
        setErrors(next);
        return Object.keys(next).length === 0;
    };

    const onSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        if (!validate()) return;

        mutation.mutate(
            {
                youtubeUrl: url.trim(),
                title: title.trim() || undefined,
                description: description.trim() || undefined,
                autoProcess,
            },
            {
                onSuccess: (video) => {
                    router.replace(`/dashboard/videos/${video.id}`);
                },
            },
        );
    };

    const pending = mutation.isPending;

    return (
        <form onSubmit={onSubmit} className="space-y-6">
            <div>
                <Label htmlFor="youtubeUrl" className="text-xs text-text-secondary">
                    YouTube URL        </Label>
                <div className="mt-1.5 flex items-center gap-2">
                    <div className="relative flex-1">
                        <Youtube className="absolute left-2.5 top-1/2 h-3.5 w-3.5 -translate-y-1/2 text-text-tertiary" />
                        <Input
                            id="youtubeUrl"
                            value={url}
                            onChange={(e) => setUrl(e.target.value)}
                            placeholder="https://youtube.com/watch?v=…"
                            disabled={pending}
                            className="h-9 border-border bg-surface-2 pl-8 text-sm placeholder:text-text-tertiary focus-visible:border-accent focus-visible:ring-0"
                        />
                    </div>
                </div>
                <FieldError message={errors.url} />
                <p className="mt-1 text-[11px] text-text-tertiary">
                    We fetch the metadata and register the video in your library.
                </p>
            </div>

            <div className="space-y-4">
                <div>
                    <Label htmlFor="ytTitle" className="text-xs text-text-secondary">
                        Title <span className="text-text-tertiary">(optional)</span>
                    </Label>
                    <Input
                        id="ytTitle"
                        value={title}
                        onChange={(e) => setTitle(e.target.value)}
                        placeholder="Leave blank to use the YouTube title"
                        disabled={pending}
                        className="mt-1.5 h-9 border-border bg-surface-2 text-sm placeholder:text-text-tertiary focus-visible:border-accent focus-visible:ring-0"
                    />
                </div>

                <div>
                    <Label htmlFor="ytDescription" className="text-xs text-text-secondary">
                        Description <span className="text-text-tertiary">(optional)</span>
                    </Label>
                    <textarea
                        id="ytDescription"
                        value={description}
                        onChange={(e) => setDescription(e.target.value)}
                        rows={3}
                        disabled={pending}
                        placeholder="Add notes for yourself"
                        className="mt-1.5 w-full rounded-md border border-border bg-surface-2 px-2.5 py-2 text-sm text-text-primary placeholder:text-text-tertiary focus:border-accent focus:outline-none"
                    />
                </div>

                <label className="flex cursor-pointer items-center gap-2 text-xs text-text-secondary">
                    <input
                        type="checkbox"
                        checked={autoProcess}
                        onChange={(e) => setAutoProcess(e.target.checked)}
                        disabled={pending}
                        className="h-3.5 w-3.5 rounded border-border bg-surface-2 accent-accent"
                    />
                    Start AI processing immediately
                </label>
            </div>

            <div className="flex items-center justify-end gap-2 pt-2">
                <Button
                    type="button"
                    variant="ghost"
                    disabled={pending}
                    onClick={() => router.push("/dashboard/videos")}
                    className="h-9 text-xs text-text-secondary hover:bg-surface-2 hover:text-text-primary"
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
                            Registering…
                        </>
                    ) : (
                        "Fetch & register"
                    )}
                </Button>
            </div>
        </form>
    );
}