import { VideoCard } from "./video-card";
import { cn } from "@/lib/utils";
import type { VideoSummary } from "@/types/video";

interface VideoGridProps {
    videos: VideoSummary[];
    className?: string;
}

export function VideoGrid({ videos, className }: VideoGridProps) {
    return (
        <div
            className={cn(
                "grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4",
                className,
            )}
        >
            {videos.map((video) => (
                <VideoCard key={video.id} video={video} />
            ))}
        </div>
    );
}