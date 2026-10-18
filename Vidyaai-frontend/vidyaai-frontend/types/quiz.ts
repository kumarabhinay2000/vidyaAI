export type AttemptStatus = "IN_PROGRESS" | "COMPLETED" | "ABANDONED" | "TIMED_OUT";
export type ProgressStatus = "NOT_STARTED" | "IN_PROGRESS" | "COMPLETED";

export interface QuizAttempt {
    id: string;
    quizId: string;
    userId: string;
    score: number;
    totalMarks: number;
    correctAnswers: number;
    wrongAnswers: number;
    unanswered: number;
    percentage?: number | null;
    timeTakenSeconds?: number | null;
    status: AttemptStatus;
    isPassed?: boolean | null;
    answers: Record<string, string>;
    startedAt: string;
    completedAt?: string | null;
    createdAt: string;
}

export interface AnswerReview {
    questionId: string;
    questionText: string;
    givenAnswer?: string | null;
    correctAnswer: string;
    isCorrect: boolean;
    explanation?: string | null;
    marksAwarded: number;
}

export interface AttemptResult {
    attemptId: string;
    quizId: string;
    score: number;
    totalMarks: number;
    percentage?: number | null;
    isPassed?: boolean | null;
    correctAnswers: number;
    wrongAnswers: number;
    unanswered: number;
    reviews: AnswerReview[];
}

export interface QuizAnalytics {
    quizId: string;
    totalAttempts: number;
    avgScore: number;
    highestScore: number;
    lowestScore: number;
    passRate: number;
    avgTimeTaken: number;
    difficultQuestions: Record<string, number>;
    easyQuestions: Record<string, number>;
    updatedAt: string;
}

export interface LeaderboardEntry {
    rank: number;
    userId: string;
    totalPoints: number;
    quizzesTaken: number;
    quizzesPassed: number;
    avgScore: number;
}

export interface UserStats {
    userId: string;
    totalAttempts: number;
    completedAttempts: number;
    passedAttempts: number;
    passRate: number;
    avgScore: number;
    totalPoints: number;
    rank?: number | null;
}

export interface LearningProgress {
    id: string;
    userId: string;
    videoId: string;
    quizId?: string | null;
    status: ProgressStatus;
    progressPercentage: number;
    attemptsCount: number;
    bestScore: number;
    lastAccessed?: string | null;
    createdAt: string;
    updatedAt: string;
}

export interface StartAttemptPayload {
    quizId: string;
}

export interface SubmitQuizPayload {
    quizId: string;
    attemptId: string;
    answers: Record<string, string>;
    timeTakenSeconds?: number;
}