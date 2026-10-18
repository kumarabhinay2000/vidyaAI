"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";

import { aiApi } from "@/lib/api/ai";
import { isApiError } from "@/lib/api";
import { queryKeys } from "@/lib/query-keys";
import type {
    ChatbotRequest,
    GenerateNotesPayload,
    GenerateQuizPayload,
    GenerateSummaryPayload,
} from "@/types/ai";

function getErrorMessage(error: unknown): string {
    if (isApiError(error)) return error.message;
    if (error instanceof Error) return error.message;
    return "Something went wrong. Please try again.";
}

/* --------------------------- SUMMARY --------------------------- */

export function useSummary(videoId: string | null | undefined) {
    return useQuery({
        queryKey: videoId ? queryKeys.ai.summary(videoId) : ["ai", "summary", "none"],
        queryFn: () => aiApi.getSummary(videoId as string),
        enabled: Boolean(videoId),
        staleTime: 5 * 60_000,
        retry: 0,
    });
}

export function useGenerateSummary() {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: (payload: GenerateSummaryPayload) => aiApi.generateSummary(payload),
        onSuccess: (summary) => {
            queryClient.setQueryData(queryKeys.ai.summary(summary.videoId), summary);
            toast.success("Summary generated");
        },
        onError: (error) => {
            toast.error(getErrorMessage(error));
        },
    });
}

export function useDeleteSummary(videoId: string) {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: () => aiApi.deleteSummary(videoId),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: queryKeys.ai.summary(videoId) });
            toast.success("Summary deleted");
        },
        onError: (error) => {
            toast.error(getErrorMessage(error));
        },
    });
}

/* --------------------------- NOTES --------------------------- */

export function useNotes(videoId: string | null | undefined) {
    return useQuery({
        queryKey: videoId ? queryKeys.ai.notes(videoId) : ["ai", "notes", "none"],
        queryFn: () => aiApi.getNotes(videoId as string),
        enabled: Boolean(videoId),
        staleTime: 5 * 60_000,
        retry: 0,
    });
}

export function useGenerateNotes() {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: (payload: GenerateNotesPayload) => aiApi.generateNotes(payload),
        onSuccess: (notes) => {
            queryClient.setQueryData(queryKeys.ai.notes(notes.videoId), notes);
            toast.success("Notes generated");
        },
        onError: (error) => {
            toast.error(getErrorMessage(error));
        },
    });
}

/* --------------------------- QUIZ --------------------------- */

export function useQuiz(videoId: string | null | undefined) {
    return useQuery({
        queryKey: videoId ? queryKeys.ai.quiz(videoId) : ["ai", "quiz", "none"],
        queryFn: () => aiApi.getQuiz(videoId as string),
        enabled: Boolean(videoId),
        staleTime: 5 * 60_000,
        retry: 0,
    });
}

export function useGenerateQuiz() {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: (payload: GenerateQuizPayload) => aiApi.generateQuiz(payload),
        onSuccess: (quiz) => {
            queryClient.setQueryData(queryKeys.ai.quiz(quiz.videoId), quiz);
            toast.success("Quiz generated");
        },
        onError: (error) => {
            toast.error(getErrorMessage(error));
        },
    });
}

/* --------------------------- CHATBOT --------------------------- */

export function useAskChatbot() {
    return useMutation({
        mutationFn: (payload: ChatbotRequest) => aiApi.askChatbot(payload),
        onError: (error) => {
            toast.error(getErrorMessage(error));
        },
    });
}