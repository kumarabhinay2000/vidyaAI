export const APP_NAME = process.env.NEXT_PUBLIC_APP_NAME ?? "VidyaAI";
export const API_URL =
    process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080/api";
export const WS_URL =
    process.env.NEXT_PUBLIC_WS_URL ?? "ws://localhost:8080/ws";

export const MAX_UPLOAD_SIZE_BYTES = 2 * 1024 * 1024 * 1024; // 2GB
export const ALLOWED_VIDEO_MIME_TYPES = [
    "video/mp4",
    "video/webm",
    "video/x-matroska",
    "video/quicktime",
    "video/x-msvideo",
];

export const ALLOWED_VIDEO_EXTENSIONS = [
    ".mp4",
    ".webm",
    ".mkv",
    ".mov",
    ".avi",
];

export const DURATION_LIMITS = {
    min: 30,
    max: 7200,
};