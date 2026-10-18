"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";

import { isApiError } from "@/lib/api";
import { quizzesApi } from "@/lib/api/quizzes";
import { queryKeys } from "@/lib/query-keys";
import type {
    AttemptResult,
    QuizAttempt,
    StartAttemptPayload,
    SubmitQuizPayload,
} from "@/types/quiz";

function getErrorMessage(error: unknown): string {
    if (isApiError(error)) return error.message;
    if (error instanceof Error) return error.message;
    return "Something went wrong. Please try again.";
}

export function useStartAttempt() {
    return useMutation<QuizAttempt, Error, StartAttemptPayload>({
        mutationFn: (payload) => quizzesApi.startAttempt(payload),
        onError: (error) => {
            toast.error(getErrorMessage(error));
        },
    });
}

export function useSubmitAttempt() {
    const queryClient = useQueryClient();

    return useMutation<AttemptResult, Error, SubmitQuizPayload>({
        mutationFn: (payload) => quizzesApi.submitAttempt(payload),
        onSuccess: () => {
            toast.success("Quiz submitted successfully!");
            queryClient.invalidateQueries({
                queryKey: queryKeys.quizzes.attempts.mine,
            });
            queryClient.invalidateQueries({
                queryKey: queryKeys.quizzes.progress.mine,
            });
            queryClient.invalidateQueries({
                queryKey: queryKeys.quizzes.myStats,
            });
        },
        onError: (error) => {
            toast.error(getErrorMessage(error));
        },
    });
}

export function useMyAttempts() {
    return useQuery<QuizAttempt[]>({
        queryKey: queryKeys.quizzes.attempts.mine,
        queryFn: () => quizzesApi.listMyAttempts(),
        staleTime: 60_000,
    });
}

export function useAttempt(attemptId: string | null | undefined) {
    return useQuery<QuizAttempt>({
        queryKey: attemptId
            ? queryKeys.quizzes.attempts.detail(attemptId)
            : ["quizzes", "attempts", "detail", "none"],
        queryFn: () => quizzesApi.getAttempt(attemptId as string),
        enabled: Boolean(attemptId),
        staleTime: 60_000,
    });
}

export function useQuizAttempts(quizId: string | null | undefined) {
    return useQuery<QuizAttempt[]>({
        queryKey: quizId
            ? queryKeys.quizzes.attempts.byQuiz(quizId)
            : ["quizzes", "attempts", "quiz", "none"],
        queryFn: () => quizzesApi.listQuizAttempts(quizId as string),
        enabled: Boolean(quizId),
        staleTime: 60_000,
    });
}
