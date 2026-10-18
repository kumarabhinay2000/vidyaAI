import { StatusBadge } from "@/components/common/status-badge";
import { getVideoStatusMeta } from "@/lib/video-status";
import type { VideoStatus } from "@/types/video";

interface VideoStatusPillProps {
    status: VideoStatus;
}

export function VideoStatusPill({ status }: VideoStatusPillProps) {
    const meta = getVideoStatusMeta(status);
    return <StatusBadge label={meta.label} variant={meta.variant} />;
}