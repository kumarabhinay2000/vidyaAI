import { apiRequest } from "./client";
import type { PageResponse } from "@/types/api";
import type {
  RegisterYouTubePayload,
  UpdateVideoPayload,
  UploadVideoPayload,
  Video,
  VideoSearchPayload,
  VideoStatusResponse,
  VideoSummary,
} from "@/types/video";

export const videosApi = {
  async upload(payload: UploadVideoPayload): Promise<Video> {
    const form = new FormData();
    form.append(
      "metadata",
      new Blob(
        [
          JSON.stringify({
            title: payload.title,
            description: payload.description,
            language: payload.language,
          }),
        ],
        { type: "application/json" },
      ),
    );
    form.append("file", payload.file);

    const uploaded = await apiRequest<{ videoId: string }>({
      url: "/videos/upload",
      method: "POST",
      data: form,
      headers: { "Content-Type": "multipart/form-data" },
      timeout: 5 * 60_000,
    });
    return videosApi.get(uploaded.videoId);
  },

  async registerYouTube(payload: RegisterYouTubePayload): Promise<Video> {
    const registered = await apiRequest<{ videoId: string }>({
      url: "/videos/youtube",
      method: "POST",
      data: payload,
    });
    return videosApi.get(registered.videoId);
  },

  async get(videoId: string): Promise<Video> {
    return await apiRequest<Video>({
      url: `/videos/${videoId}`,
      method: "GET",
    });
  },

  async update(videoId: string, payload: UpdateVideoPayload): Promise<Video> {
    return await apiRequest<Video>({
      url: `/videos/${videoId}`,
      method: "PUT",
      data: payload,
    });
  },

  async remove(videoId: string): Promise<void> {
    await apiRequest<void>({
      url: `/videos/${videoId}`,
      method: "DELETE",
    });
  },

  async getStatus(videoId: string): Promise<VideoStatusResponse> {
    return await apiRequest<VideoStatusResponse>({
      url: `/videos/status/${videoId}`,
      method: "GET",
    });
  },

  async search(
    payload: VideoSearchPayload,
  ): Promise<PageResponse<VideoSummary>> {
    return await apiRequest<PageResponse<VideoSummary>>({
      url: "/videos/search",
      method: "POST",
      data: payload,
    });
  },

  async listMine(): Promise<VideoSummary[]> {
    return await apiRequest<VideoSummary[]>({
      url: "/videos/search/my",
      method: "GET",
    });
  },

  async incrementView(videoId: string): Promise<void> {
    await apiRequest<void>({
      url: `/videos/${videoId}/view`,
      method: "POST",
    });
  },

  streamUrl(videoId: string): string {
    const base = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080/api";
    return `${base}/videos/${videoId}/stream`;
  },
};
