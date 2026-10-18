"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";

import { videosApi } from "@/lib/api/videos";
import { isApiError } from "@/lib/api";
import { queryKeys } from "@/lib/query-keys";
import type {
  RegisterYouTubePayload,
  UpdateVideoPayload,
  UploadVideoPayload,
  VideoSearchPayload,
} from "@/types/video";

function getErrorMessage(error: unknown): string {
  if (isApiError(error)) return error.message;
  if (error instanceof Error) return error.message;
  return "Something went wrong. Please try again.";
}

export function useMyVideos() {
  return useQuery({
    queryKey: queryKeys.videos.mine,
    queryFn: () => videosApi.listMine(),
    staleTime: 30_000,
  });
}

export function useVideo(videoId: string | null | undefined) {
  return useQuery({
    queryKey: videoId
      ? queryKeys.videos.detail(videoId)
      : ["videos", "detail", "none"],
    queryFn: () => videosApi.get(videoId as string),
    enabled: Boolean(videoId),
    staleTime: 15_000,
  });
}

export function useVideoStatus(
  videoId: string | null | undefined,
  options?: { enabled?: boolean },
) {
  return useQuery({
    queryKey: videoId
      ? queryKeys.videos.status(videoId)
      : ["videos", "status", "none"],
    queryFn: () => videosApi.getStatus(videoId as string),
    enabled: Boolean(videoId) && (options?.enabled ?? true),
    refetchInterval: (query) => {
      const data = query.state.data;
      if (!data) return 2000;
      // Poll while processing.
      return ["PENDING", "UPLOADED", "PROCESSING", "UPLOADING"].includes(
        data.status,
      )
        ? 2000
        : false;
    },
  });
}

export function useVideoSearch(payload: VideoSearchPayload) {
  return useQuery({
    queryKey: queryKeys.videos.search(payload),
    queryFn: () => videosApi.search(payload),
    staleTime: 15_000,
  });
}

export function useUploadVideo() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (payload: UploadVideoPayload) => videosApi.upload(payload),
    onSuccess: (video) => {
      queryClient.invalidateQueries({ queryKey: queryKeys.videos.mine });
      queryClient.setQueryData(queryKeys.videos.detail(video.id), video);
      toast.success("Video uploaded. Processing has started.");
    },
    onError: (error) => {
      toast.error(getErrorMessage(error));
    },
  });
}

export function useRegisterYouTube() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (payload: RegisterYouTubePayload) =>
      videosApi.registerYouTube(payload),
    onSuccess: (video) => {
      queryClient.invalidateQueries({ queryKey: queryKeys.videos.mine });
      queryClient.setQueryData(queryKeys.videos.detail(video.id), video);
      toast.success("YouTube video registered. Processing has started.");
    },
    onError: (error) => {
      toast.error(getErrorMessage(error));
    },
  });
}

export function useUpdateVideo(videoId: string) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (payload: UpdateVideoPayload) =>
      videosApi.update(videoId, payload),
    onSuccess: (video) => {
      queryClient.setQueryData(queryKeys.videos.detail(videoId), video);
      queryClient.invalidateQueries({ queryKey: queryKeys.videos.mine });
      toast.success("Video updated.");
    },
    onError: (error) => {
      toast.error(getErrorMessage(error));
    },
  });
}

export function useDeleteVideo() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (videoId: string) => videosApi.remove(videoId),
    onSuccess: (_data, videoId) => {
      queryClient.removeQueries({ queryKey: queryKeys.videos.detail(videoId) });
      queryClient.invalidateQueries({ queryKey: queryKeys.videos.mine });
      toast.success("Video deleted.");
    },
    onError: (error) => {
      toast.error(getErrorMessage(error));
    },
  });
}
