export type VideoStatus =
    | "PENDING"
    | "UPLOADING"
    | "UPLOADED"
    | "PROCESSING"
    | "COMPLETED"
    | "FAILED"
    | "DELETED";

export type SourceType = "YOUTUBE" | "UPLOAD";

export interface VideoChapter {
    id: string;
    title: string;
    startTime: number;
    endTime?: number | null;
    chapterOrder?: number | null;
}

export interface Video {
    id: string;
    userId: string;
    title: string;
    description?: string | null;
    sourceType: SourceType;
    sourceUrl?: string | null;
    youtubeVideoId?: string | null;
    filePath?: string | null;
    fileSize?: number | null;
    contentType?: string | null;
    durationSeconds?: number | null;
    resolution?: string | null;
    thumbnailUrl?: string | null;
    language?: string | null;
    status: VideoStatus;
    processingProgress: number;
    viewCount: number;
    tags: string[];
    chapters: VideoChapter[];
    createdAt: string;
    updatedAt: string;
}

export interface VideoSummary {
    id: string;
    userId: string;
    title: string;
    thumbnailUrl?: string | null;
    sourceType: SourceType;
    durationSeconds?: number | null;
    status: VideoStatus;
    processingProgress: number;
    viewCount: number;
    createdAt: string;
}

export interface VideoStatusResponse {
    videoId: string;
    status: VideoStatus;
    progress: number;
    currentStep?: string | null;
    message: string;
    updatedAt: string;
}

export interface UploadVideoPayload {
    title: string;
    description?: string;
    language?: string;
    file: File;
}

export interface RegisterYouTubePayload {
    youtubeUrl: string;
    title?: string;
    description?: string;
    autoProcess?: boolean;
}

export interface UpdateVideoPayload {
    title?: string;
    description?: string;
    language?: string;
}

export interface VideoSearchPayload {
    query?: string;
    sourceType?: SourceType;
    status?: VideoStatus;
    tag?: string;
    page?: number;
    size?: number;
    sortBy?: string;
    sortDirection?: "ASC" | "DESC";
}