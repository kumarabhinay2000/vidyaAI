"use client";

import { forwardRef, useImperativeHandle, useRef } from "react";

import type { Video } from "@/types/video";

export interface VideoPlayerHandle {
  seek: (seconds: number) => void;
  getCurrentTime: () => number;
}

interface VideoPlayerProps {
  video: Video;
}

export const VideoPlayer = forwardRef<VideoPlayerHandle, VideoPlayerProps>(
  function VideoPlayer({ video }, ref) {
    const videoRef = useRef<HTMLVideoElement>(null);
    const frameRef = useRef<HTMLIFrameElement>(null);

    useImperativeHandle(ref, () => ({
      seek: (seconds: number) => {
        frameRef.current?.contentWindow?.postMessage(
          JSON.stringify({
            event: "command",
            func: "seekTo",
            args: [seconds, true],
          }),
          "https://www.youtube.com",
        );
        if (videoRef.current) {
          videoRef.current.currentTime = seconds;
          void videoRef.current.play();
        }
      },
      getCurrentTime: () => videoRef.current?.currentTime ?? 0,
    }));

    const apiBase =
      process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080/api";

    return (
      <div className="overflow-hidden rounded-lg border border-border bg-black">
        <div className="relative aspect-video w-full">
          {video.sourceType === "YOUTUBE" && video.youtubeVideoId ? (
            <iframe
              ref={frameRef}
              src={`https://www.youtube.com/embed/${video.youtubeVideoId}?enablejsapi=1`}
              title={video.title}
              allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
              allowFullScreen
              className="absolute inset-0 h-full w-full"
            />
          ) : video.status === "COMPLETED" ? (
            <video
              ref={videoRef}
              controls
              preload="metadata"
              poster={video.thumbnailUrl ?? undefined}
              className="absolute inset-0 h-full w-full"
              src={`${apiBase}/videos/${video.id}/stream`}
            />
          ) : (
            <div className="absolute inset-0 flex items-center justify-center px-6 text-center text-xs text-text-tertiary">
              The video will be playable once AI processing is complete.
            </div>
          )}
        </div>
      </div>
    );
  },
);
