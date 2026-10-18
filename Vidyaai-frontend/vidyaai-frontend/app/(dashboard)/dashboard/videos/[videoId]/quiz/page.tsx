"use client";
import { use, useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { apiRequest } from "@/lib/api/client";
import { useStartAttempt, useSubmitAttempt } from "@/hooks/use-attempts";
import { QuizRunner } from "@/components/ai/quiz-runner";
import { QuizResult } from "@/components/ai/quiz-result";
import { QueryError } from "@/components/common/query-error";
import { isApiError } from "@/lib/api";
import type { Quiz, QuizQuestion } from "@/types/ai";
import type { AttemptResult } from "@/types/quiz";
interface ReadyQuiz {
  id: string;
  videoId: string;
  title: string;
  difficultyLevel: Quiz["difficulty"];
  isPublished: boolean;
  passingScore: number;
  timeLimitMinutes?: number;
  questions: (QuizQuestion & { id: string; questionOrder: number })[];
}
export default function Page({
  params,
}: {
  params: Promise<{ videoId: string }>;
}) {
  const { videoId } = use(params);
  const query = useQuery({
    queryKey: ["quizzes", "ready", videoId],
    queryFn: () =>
      apiRequest<ReadyQuiz>({
        url: "/quizzes/video/" + videoId,
        method: "GET",
      }),
    retry: 0,
  });
  const start = useStartAttempt();
  const submit = useSubmitAttempt();
  const [attemptId, setAttemptId] = useState<string | null>(null);
  const [result, setResult] = useState<AttemptResult | null>(null);
  const q = query.data;
  const quiz: Quiz | null = q
    ? {
        id: q.id,
        videoId: q.videoId,
        title: q.title,
        difficulty: q.difficultyLevel ?? "MEDIUM",
        questionType: "MCQ",
        language: "ENGLISH",
        totalQuestions: q.questions.length,
        questions: q.questions.map((item, i) => ({
          ...item,
          index: i + 1,
          options: item.options ?? [],
          correctAnswer: "",
        })),
        cached: false,
        createdAt: "",
        passingScore: q.passingScore,
        timeLimitMinutes: q.timeLimitMinutes,
      }
    : null;
  if (query.isLoading) return <p role="status">Loading practice questions…</p>;
  if (query.isError) {
    if (isApiError(query.error) && query.error.status === 404)
      return (
        <div>
          <h2 className="text-xl font-medium">Practice is not ready yet.</h2>
          <p className="mt-4 leading-7 text-text-secondary">
            No attemptable quiz is available for this lecture. Return when
            preparation is complete.
          </p>
          <button
            className="quiet-link mt-5 border border-border"
            onClick={() => void query.refetch()}
          >
            Check again
          </button>
        </div>
      );
    return <QueryError retry={() => void query.refetch()} />;
  }
  if (!q || !quiz) return null;
  if (result)
    return (
      <QuizResult
        result={result}
        videoId={videoId}
        onRetake={() => {
          setResult(null);
          setAttemptId(null);
        }}
      />
    );
  return (
    <div className="space-y-6">
      <div>
        <p className="eyebrow">Put it into practice</p>
        <h2 className="mt-3 text-xl font-medium">
          {q.title || "Check your understanding"}
        </h2>
      </div>
      {start.isError && (
        <p role="alert" className="text-sm text-danger">
          {start.error.message}
        </p>
      )}
      {submit.isError && (
        <p
          role="alert"
          className="rounded-md border border-danger/30 p-4 text-sm text-danger"
        >
          {submit.error.message} Your selected answers are still here. Try
          submitting again.
        </p>
      )}
      {attemptId ? (
        <QuizRunner
          quiz={quiz}
          attemptId={attemptId}
          submitting={submit.isPending}
          onSubmit={(payload) =>
            submit.mutate(
              { ...payload, quizId: q.id, attemptId },
              {
                onSuccess: (saved) => {
                  setResult(saved);
                  setAttemptId(null);
                },
              },
            )
          }
        />
      ) : (
        <>
          <p className="text-text-secondary">
            {q.questions.length} questions · Passing score {q.passingScore}%
            {q.timeLimitMinutes
              ? " · " + q.timeLimitMinutes + " minute limit"
              : " · No time limit specified"}
          </p>
          <p className="text-sm leading-7 text-text-secondary">
            Review each question before submitting. Eligibility and attempts are
            checked when you start.
          </p>
          <button
            className="primary-link"
            disabled={!q.isPublished || !q.questions.length || start.isPending}
            onClick={() =>
              start.mutate(
                { quizId: q.id },
                { onSuccess: (a) => setAttemptId(a.id) },
              )
            }
          >
            {start.isPending
              ? "Starting…"
              : q.isPublished
                ? "Start practice"
                : "Quiz not published"}
          </button>
        </>
      )}
    </div>
  );
}
