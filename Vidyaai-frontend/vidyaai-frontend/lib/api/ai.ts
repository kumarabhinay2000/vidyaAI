import { apiRequest } from "./client";
import type {
  ChatbotRequest,
  ChatbotResponse,
  GenerateNotesPayload,
  GenerateQuizPayload,
  GenerateSummaryPayload,
  Notes,
  Quiz,
  Summary,
  Transcript,
} from "@/types/ai";

export const aiApi = {
  async generateSummary(payload: GenerateSummaryPayload): Promise<Summary> {
    return await apiRequest<Summary>({
      url: "/ai/summary/generate",
      method: "POST",
      data: payload,
      timeout: 2 * 60_000,
    });
  },

  async getSummary(videoId: string): Promise<Summary> {
    return await apiRequest<Summary>({
      url: `/ai/summary/${videoId}`,
      method: "GET",
    });
  },

  async deleteSummary(videoId: string): Promise<void> {
    await apiRequest<void>({
      url: `/ai/summary/${videoId}`,
      method: "DELETE",
    });
  },

  async generateNotes(payload: GenerateNotesPayload): Promise<Notes> {
    return await apiRequest<Notes>({
      url: "/ai/notes/generate",
      method: "POST",
      data: payload,
      timeout: 2 * 60_000,
    });
  },

  async getNotes(videoId: string): Promise<Notes> {
    return await apiRequest<Notes>({
      url: `/ai/notes/${videoId}`,
      method: "GET",
    });
  },

  async generateQuiz(payload: GenerateQuizPayload): Promise<Quiz> {
    return await apiRequest<Quiz>({
      url: "/ai/quiz/generate",
      method: "POST",
      data: payload,
      timeout: 2 * 60_000,
    });
  },

  async getQuiz(videoId: string): Promise<Quiz> {
    return await apiRequest<Quiz>({
      url: `/ai/quiz/${videoId}`,
      method: "GET",
    });
  },

  async askChatbot(payload: ChatbotRequest): Promise<ChatbotResponse> {
    return await apiRequest<ChatbotResponse>({
      url: "/ai/chatbot/ask",
      method: "POST",
      data: payload,
      timeout: 2 * 60_000,
    });
  },

  async deleteChatHistory(videoId: string): Promise<void> {
    await apiRequest<void>({
      url: `/ai/chatbot/history/${videoId}`,
      method: "DELETE",
    });
  },

  async extractTranscript(
    videoId: string,
    sourceUrl?: string,
  ): Promise<Transcript> {
    return await apiRequest<Transcript>({
      url: "/ai/transcript/extract",
      method: "POST",
      data: { videoId, sourceUrl },
      timeout: 3 * 60_000,
    });
  },

  async getTranscript(videoId: string): Promise<Transcript> {
    return await apiRequest<Transcript>({
      url: `/ai/transcript/${videoId}`,
      method: "GET",
    });
  },
};
