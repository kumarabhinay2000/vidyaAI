export const queryKeys = {
    auth: {
        me: ["auth", "me"] as const,
    },
    videos: {
        all: ["videos"] as const,
        mine: ["videos", "mine"] as const,
        detail: (id: string) => ["videos", "detail", id] as const,
        status: (id: string) => ["videos", "status", id] as const,
        search: (payload: unknown) => ["videos", "search", payload] as const,
    },
    ai: {
        summary: (videoId: string) => ["ai", "summary", videoId] as const,
        notes: (videoId: string) => ["ai", "notes", videoId] as const,
        quiz: (videoId: string) => ["ai", "quiz", videoId] as const,
        transcript: (videoId: string) => ["ai", "transcript", videoId] as const,
    },
    quizzes: {
        attempts: {
            mine: ["quizzes", "attempts", "mine"] as const,
            detail: (id: string) => ["quizzes", "attempts", "detail", id] as const,
            byQuiz: (quizId: string) => ["quizzes", "attempts", "quiz", quizId] as const,
        },
        analytics: (quizId: string) => ["quizzes", "analytics", quizId] as const,
        leaderboard: (limit: number) => ["quizzes", "leaderboard", limit] as const,
        myStats: ["quizzes", "leaderboard", "me"] as const,
        progress: {
            mine: ["quizzes", "progress", "mine"] as const,
            byVideo: (videoId: string) => ["quizzes", "progress", "video", videoId] as const,
        },
    },
} as const;