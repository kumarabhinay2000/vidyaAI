"use client";

import Link from "next/link";
import { CheckCircle2, RotateCcw, XCircle } from "lucide-react";

import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";
import type { AttemptResult } from "@/types/quiz";

interface QuizResultProps {
    result: AttemptResult;
    videoId: string;
    onRetake: () => void;
}

export function QuizResult({ result, videoId, onRetake }: QuizResultProps) {
    const percentage = Math.round(result.percentage ?? 0);

    return (
        <div className="space-y-6">
            <div className="flex flex-col items-center py-6 text-center">
                <p className="text-4xl font-semibold tracking-tight text-text-primary">
                    {percentage}%
                </p>
                <p className="mt-1 text-xs text-text-secondary">
                    {result.correctAnswers} of {result.correctAnswers + result.wrongAnswers + result.unanswered} correct
                </p>
                <span
                    className={cn(
                        "mt-3 inline-flex items-center gap-1.5 rounded-full px-2.5 py-1 text-[11px] font-medium uppercase tracking-wide",
                        result.isPassed
                            ? "bg-success-muted text-success"
                            : "bg-warning-muted text-warning",
                    )}
                >
          {result.isPassed ? "Passed" : "Not passed"}
        </span>
            </div>

            <div className="grid grid-cols-3 gap-3 rounded-lg border border-border bg-surface/40 p-3 text-center">
                <div>
                    <p className="text-[10px] uppercase tracking-widest text-text-tertiary">
                        Correct
                    </p>
                    <p className="mt-1 text-lg font-semibold text-success">
                        {result.correctAnswers}
                    </p>
                </div>
                <div>
                    <p className="text-[10px] uppercase tracking-widest text-text-tertiary">
                        Wrong
                    </p>
                    <p className="mt-1 text-lg font-semibold text-danger">
                        {result.wrongAnswers}
                    </p>
                </div>
                <div>
                    <p className="text-[10px] uppercase tracking-widest text-text-tertiary">
                        Skipped
                    </p>
                    <p className="mt-1 text-lg font-semibold text-text-secondary">
                        {result.unanswered}
                    </p>
                </div>
            </div>

            <div className="space-y-2">
                <p className="text-xs font-medium text-text-primary">Review</p>
                {result.reviews.map((review, idx) => (
                    <div
                        key={review.questionId}
                        className={cn(
                            "rounded-md border bg-surface/40 p-3",
                            review.isCorrect ? "border-border" : "border-danger/40",
                        )}
                    >
                        <div className="flex items-start gap-2">
                            {review.isCorrect ? (
                                <CheckCircle2 className="mt-0.5 h-4 w-4 shrink-0 text-success" />
                            ) : (
                                <XCircle className="mt-0.5 h-4 w-4 shrink-0 text-danger" />
                            )}
                            <div className="min-w-0 flex-1">
                                <p className="text-sm text-text-primary">
                  <span className="mr-2 font-mono text-[11px] text-text-tertiary">
                    Q{idx + 1}
                  </span>
                                    {review.questionText}
                                </p>
                                <p className="mt-1 text-[11px] text-text-tertiary">
                                    Your answer:{" "}
                                    <span
                                        className={cn(
                                            review.isCorrect ? "text-success" : "text-danger",
                                        )}
                                    >
                    {review.givenAnswer ?? "—"}
                  </span>
                                    {!review.isCorrect ? (
                                        <>
                                            {" · "}
                                            Correct: <span className="text-success">{review.correctAnswer}</span>
                                        </>
                                    ) : null}
                                </p>
                                {review.explanation ? (
                                    <p className="mt-2 text-[11px] text-text-secondary">
                                        {review.explanation}
                                    </p>
                                ) : null}
                            </div>
                        </div>
                    </div>
                ))}
            </div>

            <div className="flex items-center justify-between pt-2">
                <Button
                    type="button"
                    variant="ghost"
                    onClick={onRetake}
                    className="h-8 text-xs text-text-secondary hover:bg-surface-2 hover:text-text-primary"
                >
                    <RotateCcw className="mr-1.5 h-3.5 w-3.5" />
                    Retake
                </Button>
                <Link
                    href={`/dashboard/videos/${videoId}`}
                    className="inline-flex h-8 items-center rounded-md bg-accent px-3 text-xs font-medium text-accent-fg transition-colors hover:bg-accent-hover"
                >
                    Back to overview
                </Link>
            </div>
        </div>
    );
}