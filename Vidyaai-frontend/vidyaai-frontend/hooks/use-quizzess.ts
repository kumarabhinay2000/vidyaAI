"use client";

import { useQuery } from "@tanstack/react-query";

import { quizzesApi } from "@/lib/api/quizzes";
import { queryKeys } from "@/lib/query-keys";

export function useLeaderboard(limit = 10) {
    return useQuery({
        queryKey: queryKeys.quizzes.leaderboard(limit),
        queryFn: () => quizzesApi.getLeaderboard(limit),
        staleTime: 60_000,
    });
}

export function useMyStats() {
    return useQuery({
        queryKey: queryKeys.quizzes.myStats,
        queryFn: () => quizzesApi.getMyStats(),
        staleTime: 60_000,
    });
}

export function useMyProgress() {
    return useQuery({
        queryKey: queryKeys.quizzes.progress.mine,
        queryFn: () => quizzesApi.listMyProgress(),
        staleTime: 60_000,
    });
}

export function useVideoProgress(videoId: string | null | undefined) {
    return useQuery({
        queryKey: videoId
            ? queryKeys.quizzes.progress.byVideo(videoId)
            : ["quizzes", "progress", "video", "none"],
        queryFn: () => quizzesApi.getProgressForVideo(videoId as string),
        enabled: Boolean(videoId),
        staleTime: 60_000,
        retry: 0,
    });
}