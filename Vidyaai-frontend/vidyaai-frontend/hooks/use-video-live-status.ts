"use client";

import { useEffect, useMemo, useState } from "react";
import { useQueryClient } from "@tanstack/react-query";

import { queryKeys } from "@/lib/query-keys";
import { useWebSocket } from "./use-websocket";
import type { VideoStatus } from "@/types/video";

interface LiveStatusPayload {
    videoId: string;
    status: VideoStatus;
    progress: number;
    currentStep?: string | null;
    message?: string;
}

interface Options {
    videoId: string;
    initial?: LiveStatusPayload | null;
}

export function useVideoLiveStatus({ videoId, initial }: Options) {
    const queryClient = useQueryClient();
    const [state, setState] = useState<LiveStatusPayload | null>(initial ?? null);

    const subscriptions = useMemo(
        () => [
            {
                type: "video.status",
                handler: (payload: unknown) => {
                    const p = payload as LiveStatusPayload;
                    if (!p || p.videoId !== videoId) return;
                    setState(p);

                    // Update the cached status query so any other consumer picks it up.
                    queryClient.setQueryData(
                        queryKeys.videos.status(videoId),
                        {
                            videoId: p.videoId,
                            status: p.status,
                            progress: p.progress,
                            currentStep: p.currentStep ?? null,
                            message: p.message ?? "",
                            updatedAt: new Date().toISOString(),
                        },
                    );

                    // If processing finished, invalidate detail + AI content.
                    if (p.status === "COMPLETED" || p.status === "FAILED") {
                        queryClient.invalidateQueries({
                            queryKey: queryKeys.videos.detail(videoId),
                        });
                        queryClient.invalidateQueries({
                            queryKey: queryKeys.ai.summary(videoId),
                        });
                        queryClient.invalidateQueries({
                            queryKey: queryKeys.ai.notes(videoId),
                        });
                        queryClient.invalidateQueries({
                            queryKey: queryKeys.ai.quiz(videoId),
                        });
                    }
                },
            },
        ],
        [videoId, queryClient],
    );

    const { status } = useWebSocket(subscriptions);

    // Fallback polling if socket is disconnected and status is in-progress.
    useEffect(() => {
        if (status === "connected") return;
        if (!state) return;
        if (state.status !== "PROCESSING" && state.status !== "UPLOADING") return;

        const id = setInterval(() => {
            queryClient.invalidateQueries({
                queryKey: queryKeys.videos.status(videoId),
            });
        }, 5000);
        return () => clearInterval(id);
    }, [status, state, videoId, queryClient]);

    return state;
}