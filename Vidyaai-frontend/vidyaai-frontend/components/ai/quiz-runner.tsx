"use client";

import { useMemo, useState } from "react";
import { ArrowLeft, ArrowRight, Loader2 } from "lucide-react";

import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";
import type { Quiz } from "@/types/ai";
import type { SubmitQuizPayload } from "@/types/quiz";

interface QuizRunnerProps {
  quiz: Quiz;
  attemptId: string;
  submitting: boolean;
  onSubmit: (payload: Omit<SubmitQuizPayload, "attemptId" | "quizId">) => void;
}

export function QuizRunner({
  quiz,
  attemptId,
  submitting,
  onSubmit,
}: QuizRunnerProps) {
  const total = quiz.questions.length;
  const [index, setIndex] = useState(0);
  const [answers, setAnswers] = useState<Record<string, string>>({});
  const [startedAt] = useState(() => Date.now());

  const current = quiz.questions[index];
  const currentQuestionKey = current?.id ?? "";

  const selectedAnswer = useMemo(
    () => (current ? answers[currentQuestionKey] : undefined),
    [answers, current, currentQuestionKey],
  );

  const setAnswer = (value: string) => {
    setAnswers((prev) => ({ ...prev, [currentQuestionKey]: value }));
  };

  const onNext = () => {
    if (index < total - 1) setIndex((i) => i + 1);
  };

  const onPrev = () => {
    if (index > 0) setIndex((i) => i - 1);
  };

  const handleSubmit = () => {
    if (
      submitting ||
      !window.confirm(
        "Submit your answers? " +
          (total - Object.keys(answers).length) +
          " unanswered question(s).",
      )
    )
      return;
    const timeTakenSeconds = Math.max(
      1,
      Math.round((Date.now() - startedAt) / 1000),
    );
    onSubmit({ answers, timeTakenSeconds });
  };

  if (!current) {
    return (
      <p className="text-sm text-text-secondary">
        This quiz has no available questions.
      </p>
    );
  }

  const isTrueFalse =
    current.options.length === 2 &&
    current.options.includes("True") &&
    current.options.includes("False");

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap gap-2" aria-label="Question navigator">
        {quiz.questions.map((q, i) => (
          <button
            type="button"
            key={q.id ?? i}
            disabled={submitting}
            aria-label={
              "Question " +
              (i + 1) +
              (answers[q.id ?? ""] ? ", answered" : ", unanswered")
            }
            aria-current={i === index ? "step" : undefined}
            onClick={() => setIndex(i)}
            className={
              "h-11 w-11 rounded-md border text-sm " +
              (index === i ? "border-accent bg-accent-muted" : "border-border")
            }
          >
            {i + 1}
            {answers[q.id ?? ""] ? " ✓" : ""}
          </button>
        ))}
      </div>
      <div className="flex items-center justify-between">
        <p className="text-xs text-text-tertiary">
          Question {index + 1} of {total}
        </p>
        <p className="font-mono text-xs text-text-tertiary">
          attempt {attemptId.slice(0, 8)}
        </p>
      </div>

      <div className="h-1 overflow-hidden rounded-full bg-surface-2">
        <div
          className="h-full bg-accent transition-all"
          style={{ width: `${((index + 1) / total) * 100}%` }}
        />
      </div>

      <div className="space-y-5">
        <p className="text-sm leading-relaxed text-text-primary">
          {current.questionText}
        </p>

        <div className="space-y-2">
          {isTrueFalse
            ? current.options.map((opt) => {
                const selected = selectedAnswer === opt;
                return (
                  <button
                    key={opt}
                    type="button"
                    aria-pressed={selected}
                    onClick={() => setAnswer(opt)}
                    className={cn(
                      "flex w-full items-center justify-between rounded-md border px-3 py-2 text-left text-sm transition-colors",
                      selected
                        ? "border-accent bg-accent-muted text-text-primary"
                        : "border-border bg-surface/40 text-text-secondary hover:border-border-strong hover:bg-surface-2",
                    )}
                  >
                    <span>{opt}</span>
                    <span
                      className={cn(
                        "flex h-4 w-4 items-center justify-center rounded-full border",
                        selected ? "border-accent" : "border-border-strong",
                      )}
                    >
                      {selected ? (
                        <span className="h-2 w-2 rounded-full bg-accent" />
                      ) : null}
                    </span>
                  </button>
                );
              })
            : current.options.map((opt, i) => {
                const selected = selectedAnswer === opt;
                const label = String.fromCharCode(65 + i);
                return (
                  <button
                    key={opt}
                    type="button"
                    onClick={() => setAnswer(opt)}
                    className={cn(
                      "flex w-full items-center gap-3 rounded-md border px-3 py-2 text-left text-sm transition-colors",
                      selected
                        ? "border-accent bg-accent-muted text-text-primary"
                        : "border-border bg-surface/40 text-text-secondary hover:border-border-strong hover:bg-surface-2",
                    )}
                  >
                    <span
                      className={cn(
                        "flex h-5 w-5 shrink-0 items-center justify-center rounded-full border text-[11px] font-medium",
                        selected
                          ? "border-accent bg-accent text-accent-fg"
                          : "border-border-strong text-text-tertiary",
                      )}
                    >
                      {label}
                    </span>
                    <span>{opt}</span>
                  </button>
                );
              })}
        </div>
      </div>

      <div className="flex items-center justify-between pt-2">
        <Button
          type="button"
          variant="ghost"
          disabled={index === 0 || submitting}
          onClick={onPrev}
          className="h-8 text-xs text-text-secondary hover:bg-surface-2 hover:text-text-primary"
        >
          <ArrowLeft className="mr-1.5 h-3.5 w-3.5" />
          Previous
        </Button>

        {index < total - 1 ? (
          <Button
            type="button"
            disabled={submitting}
            onClick={onNext}
            className="h-8 bg-accent px-3 text-xs font-medium text-accent-fg transition-colors hover:bg-accent-hover"
          >
            Next
            <ArrowRight className="ml-1.5 h-3.5 w-3.5" />
          </Button>
        ) : (
          <Button
            type="button"
            disabled={submitting}
            onClick={handleSubmit}
            className="h-8 bg-accent px-3 text-xs font-medium text-accent-fg transition-colors hover:bg-accent-hover"
          >
            {submitting ? (
              <>
                <Loader2 className="mr-2 h-3.5 w-3.5 animate-spin" />
                Submitting…
              </>
            ) : (
              "Submit quiz"
            )}
          </Button>
        )}
      </div>

      <div className="flex flex-wrap gap-1 pt-2">
        {quiz.questions.map((q, i) => {
          const key = `q-${q.index ?? i}`;
          const answered = Boolean(answers[key]);
          const active = i === index;
          return (
            <button
              key={key}
              type="button"
              onClick={() => setIndex(i)}
              disabled={submitting}
              className={cn(
                "h-6 w-6 rounded text-[10px] font-mono transition-colors",
                active
                  ? "bg-accent text-accent-fg"
                  : answered
                    ? "bg-surface-3 text-text-primary"
                    : "bg-surface-2 text-text-tertiary hover:text-text-secondary",
              )}
            >
              {i + 1}
            </button>
          );
        })}
      </div>
    </div>
  );
}
