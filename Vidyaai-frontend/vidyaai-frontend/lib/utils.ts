import { clsx, type ClassValue } from "clsx";
import { twMerge } from "tailwind-merge";

/**
 * Merges Tailwind class names while resolving conflicts.
 */
export function cn(...inputs: ClassValue[]) {
    return twMerge(clsx(inputs));
}

/**
 * Formats a byte count into a human-readable string.
 */
export function formatBytes(bytes: number, decimals = 1): string {
    if (!Number.isFinite(bytes) || bytes < 0) return "0 B";
    if (bytes < 1024) return `${bytes} B`;
    const exp = Math.floor(Math.log(bytes) / Math.log(1024));
    const unit = "KMGTPE"[exp - 1] ?? "";
    const value = bytes / Math.pow(1024, exp);
    return `${value.toFixed(decimals)} ${unit}B`;
}

/**
 * Formats seconds into `m:ss` or `h:mm:ss`.
 */
export function formatDuration(seconds: number): string {
    if (!Number.isFinite(seconds) || seconds < 0) return "0:00";
    const h = Math.floor(seconds / 3600);
    const m = Math.floor((seconds % 3600) / 60);
    const s = Math.floor(seconds % 60);
    const mm = h > 0 ? String(m).padStart(2, "0") : String(m);
    const ss = String(s).padStart(2, "0");
    return h > 0 ? `${h}:${mm}:${ss}` : `${mm}:${ss}`;
}

/**
 * Converts a timestamp into a compact relative string.
 * Prefers Intl for correctness; falls back to a simple heuristic.
 */
export function formatRelativeTime(date: Date | string): string {
    const d = typeof date === "string" ? new Date(date) : date;
    const diffMs = Date.now() - d.getTime();
    const sec = Math.floor(diffMs / 1000);
    if (sec < 60) return "just now";
    const min = Math.floor(sec / 60);
    if (min < 60) return `${min}m ago`;
    const hr = Math.floor(min / 60);
    if (hr < 24) return `${hr}h ago`;
    const days = Math.floor(hr / 24);
    if (days < 7) return `${days}d ago`;
    const weeks = Math.floor(days / 7);
    if (weeks < 5) return `${weeks}w ago`;
    const months = Math.floor(days / 30);
    if (months < 12) return `${months}mo ago`;
    const years = Math.floor(days / 365);
    return `${years}y ago`;
}

/**
 * Truncates a string to a max length with an ellipsis.
 */
export function truncate(input: string, max = 60): string {
    if (input.length <= max) return input;
    return input.slice(0, max - 1).trimEnd() + "…";
}

/**
 * Returns true if the given value is a non-empty string.
 */
export function hasText(value: unknown): value is string {
    return typeof value === "string" && value.trim().length > 0;
}