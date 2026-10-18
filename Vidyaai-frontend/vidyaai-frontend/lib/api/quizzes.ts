import { apiRequest } from "./client";
import type {
  AttemptResult,
  LeaderboardEntry,
  LearningProgress,
  QuizAnalytics,
  QuizAttempt,
  StartAttemptPayload,
  SubmitQuizPayload,
  UserStats,
} from "@/types/quiz";

export const quizzesApi = {
  async startAttempt(payload: StartAttemptPayload): Promise<QuizAttempt> {
    return await apiRequest<QuizAttempt>({
      url: "/quizzes/attempts/start",
      method: "POST",
      data: payload,
    });
  },

  async submitAttempt(payload: SubmitQuizPayload): Promise<AttemptResult> {
    return await apiRequest<AttemptResult>({
      url: "/quizzes/attempts/submit",
      method: "POST",
      data: payload,
    });
  },

  async getAttempt(attemptId: string): Promise<QuizAttempt> {
    return await apiRequest<QuizAttempt>({
      url: `/quizzes/attempts/${attemptId}`,
      method: "GET",
    });
  },

  async listMyAttempts(): Promise<QuizAttempt[]> {
    return await apiRequest<QuizAttempt[]>({
      url: "/quizzes/attempts/my",
      method: "GET",
    });
  },

  async listQuizAttempts(quizId: string): Promise<QuizAttempt[]> {
    return await apiRequest<QuizAttempt[]>({
      url: `/quizzes/attempts/quiz/${quizId}`,
      method: "GET",
    });
  },

  async getAnalytics(quizId: string): Promise<QuizAnalytics> {
    return await apiRequest<QuizAnalytics>({
      url: `/quizzes/analytics/${quizId}`,
      method: "GET",
    });
  },

  async refreshAnalytics(quizId: string): Promise<void> {
    await apiRequest<void>({
      url: `/quizzes/analytics/${quizId}/refresh`,
      method: "POST",
    });
  },

  async getLeaderboard(limit = 10): Promise<LeaderboardEntry[]> {
    return await apiRequest<LeaderboardEntry[]>({
      url: `/quizzes/leaderboard?limit=${limit}`,
      method: "GET",
    });
  },

  async getMyStats(): Promise<UserStats> {
    return await apiRequest<UserStats>({
      url: "/quizzes/leaderboard/me",
      method: "GET",
    });
  },

  async getProgressForVideo(videoId: string): Promise<LearningProgress> {
    return await apiRequest<LearningProgress>({
      url: `/quizzes/progress/video/${videoId}`,
      method: "GET",
    });
  },

  async listMyProgress(): Promise<LearningProgress[]> {
    return await apiRequest<LearningProgress[]>({
      url: "/quizzes/progress/my",
      method: "GET",
    });
  },
};
