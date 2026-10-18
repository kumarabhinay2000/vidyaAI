"use client";

import { useRef, useState } from "react";
import { useRouter } from "next/navigation";
import { FileVideo, Loader2, X } from "lucide-react";

import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { FieldError } from "@/components/ui/field-error";
import { useUploadVideo } from "@/hooks/use-videos";
import {
    ALLOWED_VIDEO_EXTENSIONS,
    ALLOWED_VIDEO_MIME_TYPES,
    MAX_UPLOAD_SIZE_BYTES,
} from "@/lib/constants";
import { cn, formatBytes } from "@/lib/utils";

export function UploadFileForm() {
    const router = useRouter();
    const mutation = useUploadVideo();
    const inputRef = useRef<HTMLInputElement>(null);

    const [file, setFile] = useState<File | null>(null);
    const [title, setTitle] = useState("");
    const [description, setDescription] = useState("");
    const [language, setLanguage] = useState("ENGLISH");
    const [errors, setErrors] = useState<{
        file?: string;
        title?: string;
    }>({});
    const [dragging, setDragging] = useState(false);

    const validate = (): boolean => {
        const next: typeof errors = {};
        if (!file) next.file = "Please choose a video file.";
        if (file) {
            if (file.size > MAX_UPLOAD_SIZE_BYTES) {
                next.file = `File must be under ${formatBytes(MAX_UPLOAD_SIZE_BYTES)}.`;
            } else if (!ALLOWED_VIDEO_MIME_TYPES.includes(file.type)) {
                next.file = "Unsupported format. Use MP4, WebM, MKV, MOV, or AVI.";
            }
        }
        if (!title.trim()) next.title = "Title is required.";
        else if (title.length > 500) next.title = "Title must be 500 characters or less.";
        setErrors(next);
        return Object.keys(next).length === 0;
    };

    const pickFile = (f: File | null) => {
        if (!f) return;
        setFile(f);
        if (!title) setTitle(f.name.replace(/\.[^/.]+$/, ""));
    };

    const onDrop = (e: React.DragEvent<HTMLDivElement>) => {
        e.preventDefault();
        setDragging(false);
        const f = e.dataTransfer.files?.[0];
        if (f) pickFile(f);
    };

    const onSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        if (!validate() || !file) return;

        mutation.mutate(
            {
                title: title.trim(),
                description: description.trim() || undefined,
                language,
                file,
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
            {/* Dropzone */}
            <div>
                <div
                    onDragOver={(e) => {
                        e.preventDefault();
                        setDragging(true);
                    }}
                    onDragLeave={() => setDragging(false)}
                    onDrop={onDrop}
                    onClick={() => inputRef.current?.click()}
                    role="button"
                    tabIndex={0}
                    onKeyDown={(e) => {
                        if (e.key === "Enter" || e.key === " ") inputRef.current?.click();
                    }}
                    className={cn(
                        "flex cursor-pointer flex-col items-center justify-center rounded-lg border border-dashed border-border bg-surface/40 px-6 py-10 text-center transition-colors",
                        dragging ? "border-accent bg-accent-muted" : "hover:border-border-strong",
                    )}
                >
                    {file ? (
                        <div className="flex w-full items-center justify-between gap-3 rounded-md border border-border bg-surface-2 px-3 py-2">
                            <div className="flex min-w-0 items-center gap-3">
                                <FileVideo className="h-4 w-4 shrink-0 text-accent" />
                                <div className="min-w-0">
                                    <p className="truncate text-sm text-text-primary">{file.name}</p>
                                    <p className="text-[11px] text-text-tertiary">
                                        {formatBytes(file.size)}
                                    </p>
                                </div>
                            </div>
                            <button
                                type="button"
                                onClick={(e) => {
                                    e.stopPropagation();
                                    setFile(null);
                                }}
                                className="flex h-6 w-6 items-center justify-center rounded-md text-text-tertiary transition-colors hover:bg-surface-3 hover:text-text-primary"
                                aria-label="Remove file"
                            >
                                <X className="h-3.5 w-3.5" />
                            </button>
                        </div>
                    ) : (
                        <>
                            <FileVideo className="h-6 w-6 text-text-tertiary" />
                            <p className="mt-3 text-sm text-text-primary">
                                Drop your file here, or browse
                            </p>
                            <p className="mt-1 text-[11px] text-text-tertiary">
                                MP4, WebM, MKV, MOV, AVI · up to {formatBytes(MAX_UPLOAD_SIZE_BYTES)}
                            </p>
                        </>
                    )}

                    <input
                        ref={inputRef}
                        type="file"
                        accept={`${ALLOWED_VIDEO_MIME_TYPES.join(",")},${ALLOWED_VIDEO_EXTENSIONS.join(",")}`}
                        className="hidden"
                        onChange={(e) => pickFile(e.target.files?.[0] ?? null)}
                        disabled={pending}
                    />
                </div>
                <FieldError message={errors.file} />
            </div>

            {/* Details */}
            <div className="space-y-4">
                <div>
                    <Label htmlFor="title" className="text-xs text-text-secondary">
                        Title
                    </Label>
                    <Input
                        id="title"
                        value={title}
                        onChange={(e) => setTitle(e.target.value)}
                        placeholder="e.g. JavaScript Closures — Full Tutorial"
                        disabled={pending}
                        className="mt-1.5 h-9 border-border bg-surface-2 text-sm placeholder:text-text-tertiary focus-visible:border-accent focus-visible:ring-0"
                    />
                    <FieldError message={errors.title} />
                </div>

                <div>
                    <Label htmlFor="description" className="text-xs text-text-secondary">
                        Description <span className="text-text-tertiary">(optional)</span>
                    </Label>
                    <textarea
                        id="description"
                        value={description}
                        onChange={(e) => setDescription(e.target.value)}
                        rows={3}
                        disabled={pending}
                        placeholder="What is this video about?"
                        className="mt-1.5 w-full rounded-md border border-border bg-surface-2 px-2.5 py-2 text-sm text-text-primary placeholder:text-text-tertiary focus:border-accent focus:outline-none"
                    />
                </div>

                <div>
                    <Label htmlFor="language" className="text-xs text-text-secondary">
                        Language
                    </Label>
                    <select
                        id="language"
                        value={language}
                        onChange={(e) => setLanguage(e.target.value)}
                        disabled={pending}
                        className="mt-1.5 h-9 w-full rounded-md border border-border bg-surface-2 px-2 text-sm text-text-primary focus:border-accent focus:outline-none"
                    >
                        <option value="ENGLISH">English</option>
                        <option value="HINDI">Hindi</option>
                        <option value="TAMIL">Tamil</option>
                        <option value="TELUGU">Telugu</option>
                        <option value="BENGALI">Bengali</option>
                        <option value="MARATHI">Marathi</option>
                    </select>
                </div>
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
                            Uploading…
                        </>
                    ) : (
                        "Upload & process"
                    )}
                </Button>
            </div>
        </form>
    );
}