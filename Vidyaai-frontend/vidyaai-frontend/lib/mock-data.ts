import type { User, AuthSession } from "@/types/user";
import type { Video } from "@/types/video";
import type { Summary, Notes, Quiz, Transcript } from "@/types/ai";
import type { LeaderboardEntry, UserStats, LearningProgress, QuizAttempt } from "@/types/quiz";

export const MOCK_ADMIN_USER: User = {
    id: "9f264dbb-6150-49f0-a357-8f36da0006ae",
    email: "admin@vidyaai.com",
    firstName: "Admin",
    lastName: "VidyaAI",
    fullName: "Admin VidyaAI",
    role: "ADMIN",
    planType: "ENTERPRISE",
    status: "ACTIVE",
    emailVerified: true,
    profilePicUrl: null,
    createdAt: new Date(Date.now() - 30 * 86400000).toISOString(),
    updatedAt: new Date().toISOString(),
};

export const MOCK_STUDENT_USER: User = {
    id: "abffa6b0-12c8-487f-b3ee-a1ae70abb51c",
    email: "kshitij@vidyaai.com",
    firstName: "Kshitij",
    lastName: "Sharma",
    fullName: "Kshitij Sharma",
    role: "STUDENT",
    planType: "PRO",
    status: "ACTIVE",
    emailVerified: true,
    profilePicUrl: null,
    createdAt: new Date(Date.now() - 14 * 86400000).toISOString(),
    updatedAt: new Date().toISOString(),
};

export function createMockSession(user: User): AuthSession {
    return {
        accessToken: `demo-jwt-${user.role.toLowerCase()}-${Date.now()}`,
        refreshToken: `demo-refresh-${user.role.toLowerCase()}-${Date.now()}`,
        tokenType: "Bearer",
        expiresIn: 86400,
        user,
    };
}

export const MOCK_VIDEOS: Video[] = [
    {
        id: "demo-123",
        userId: MOCK_STUDENT_USER.id,
        title: "Deep Learning & Neural Networks Fundamentals",
        description: "A comprehensive guide to backpropagation, gradient descent, activations, and deep neural network architectures.",
        sourceType: "YOUTUBE",
        sourceUrl: "https://www.youtube.com/watch?v=aircAruvnKk",
        youtubeVideoId: "aircAruvnKk",
        filePath: null,
        fileSize: 45000000,
        contentType: "video/mp4",
        durationSeconds: 1142,
        resolution: "1080p",
        thumbnailUrl: null,
        language: "ENGLISH",
        status: "COMPLETED",
        processingProgress: 100,
        viewCount: 142,
        tags: ["AI", "Deep Learning", "Neural Networks"],
        chapters: [
            { id: "c1", title: "Introduction to Perceptrons", startTime: 0, endTime: 120, chapterOrder: 1 },
            { id: "c2", title: "Activation Functions", startTime: 120, endTime: 340, chapterOrder: 2 },
            { id: "c3", title: "Loss Functions & Optimization", startTime: 340, endTime: 610, chapterOrder: 3 },
            { id: "c4", title: "Backpropagation Calculus", startTime: 610, endTime: 980, chapterOrder: 4 },
        ],
        createdAt: new Date(Date.now() - 2 * 86400000).toISOString(),
        updatedAt: new Date(Date.now() - 2 * 86400000).toISOString(),
    },
    {
        id: "demo-124",
        userId: MOCK_STUDENT_USER.id,
        title: "Full Stack Next.js 16 & Server Components Architecture",
        description: "Master modern React 19 patterns, Turbopack, App Router layouts, and edge rendering strategies.",
        sourceType: "YOUTUBE",
        sourceUrl: "https://www.youtube.com/watch?v=ZVnjOPwW4ZA",
        youtubeVideoId: "ZVnjOPwW4ZA",
        filePath: null,
        fileSize: 82000000,
        contentType: "video/mp4",
        durationSeconds: 1845,
        resolution: "1080p",
        thumbnailUrl: null,
        language: "ENGLISH",
        status: "COMPLETED",
        processingProgress: 100,
        viewCount: 88,
        tags: ["Web Dev", "Next.js", "React"],
        chapters: [
            { id: "c21", title: "Turbopack Bundling", startTime: 0, endTime: 300, chapterOrder: 1 },
            { id: "c22", title: "Server Actions & Mutate", startTime: 300, endTime: 900, chapterOrder: 2 },
        ],
        createdAt: new Date(Date.now() - 4 * 86400000).toISOString(),
        updatedAt: new Date(Date.now() - 4 * 86400000).toISOString(),
    },
    {
        id: "demo-125",
        userId: MOCK_STUDENT_USER.id,
        title: "Microservices Architecture with Spring Boot & Kafka",
        description: "Designing event-driven distributed systems, circuit breakers, service discovery, and API gateway routing.",
        sourceType: "YOUTUBE",
        sourceUrl: "https://www.youtube.com/watch?v=mXZxO_WbA7c",
        youtubeVideoId: "mXZxO_WbA7c",
        filePath: null,
        fileSize: 110000000,
        contentType: "video/mp4",
        durationSeconds: 2420,
        resolution: "1080p",
        thumbnailUrl: null,
        language: "ENGLISH",
        status: "COMPLETED",
        processingProgress: 100,
        viewCount: 204,
        tags: ["Java", "Spring Boot", "Kafka"],
        chapters: [
            { id: "c31", title: "Eureka Discovery", startTime: 0, endTime: 450, chapterOrder: 1 },
            { id: "c32", title: "Kafka Event Streams", startTime: 450, endTime: 1200, chapterOrder: 2 },
        ],
        createdAt: new Date(Date.now() - 7 * 86400000).toISOString(),
        updatedAt: new Date(Date.now() - 7 * 86400000).toISOString(),
    },
    {
        id: "demo-126",
        userId: MOCK_STUDENT_USER.id,
        title: "Data Structures & Dynamic Programming Masterclass",
        description: "Deep dive into memoization, tabulation, DAG shortest paths, and knapsack problem variants.",
        sourceType: "UPLOAD",
        sourceUrl: null,
        youtubeVideoId: null,
        filePath: "/uploads/dsa-lecture-04.mp4",
        fileSize: 350000000,
        contentType: "video/mp4",
        durationSeconds: 3120,
        resolution: "720p",
        thumbnailUrl: null,
        language: "ENGLISH",
        status: "PROCESSING",
        processingProgress: 68,
        viewCount: 15,
        tags: ["Algorithms", "Data Structures"],
        chapters: [],
        createdAt: new Date(Date.now() - 1000 * 60 * 30).toISOString(),
        updatedAt: new Date().toISOString(),
    },
];

export const MOCK_SUMMARIES: Record<string, Summary> = {
    "demo-123": {
        id: "sum-123",
        videoId: "demo-123",
        title: "Deep Learning Foundations Summary",
        points: [
            "Neural networks operate through forward passes calculating weighted sums through non-linear activation functions.",
            "Backpropagation calculates partial derivatives of the loss function with respect to weights using the chain rule.",
            "Gradient descent optimizes loss by taking steps in the direction of steepest descent, governed by learning rate.",
            "Vanishing gradients in deep architectures led to modern activations like ReLU, GELU, and residual skip connections.",
            "Overfitting is mitigated using dropout regularization, weight decay (L2 penalty), and batch normalization."
        ],
        fullContent: "This lecture covers the complete foundational theory of artificial neural networks. Starting from mathematical biological perceptron models, it demonstrates matrix dot-product layer transformations and the role of non-linear activations. The core tutorial derives gradient descent weight updates via backpropagation with step-by-step calculus.",
        keywords: ["Neural Networks", "Backpropagation", "Gradient Descent", "Activations", "ReLU"],
        type: "FIVE_POINT",
        language: "ENGLISH",
        cached: true,
        createdAt: new Date(Date.now() - 2 * 86400000).toISOString(),
    },
};

export const MOCK_NOTES: Record<string, Notes> = {
    "demo-123": {
        id: "notes-123",
        videoId: "demo-123",
        title: "Deep Learning Foundations — Study Notes",
        sections: [
            {
                heading: "1. Mathematical Representation",
                bulletPoints: [
                    "A neuron computes weighted sum: z = W^T * X + b",
                    "Activation output: a = sigma(z)",
                    "Non-linearities prevent collapsing into single linear maps."
                ],
                startTimeSeconds: 0,
            },
            {
                heading: "2. Common Activation Functions",
                bulletPoints: [
                    "ReLU: max(0, x), fast computation, overcomes vanishing gradients.",
                    "GELU: smooth Gaussian gating, state of the art in Transformers.",
                    "Sigmoid: outputs probability in range (0, 1)."
                ],
                startTimeSeconds: 240,
            },
            {
                heading: "3. Backpropagation Flow",
                bulletPoints: [
                    "Forward pass evaluates intermediate layer activations.",
                    "Loss function calculates error against target label.",
                    "Chain rule computes partial derivatives backward to each layer."
                ],
                startTimeSeconds: 610,
            },
        ],
        fullContent: `# Deep Learning Foundations — Study Notes

## 1. Mathematical Representation
A neuron computes:
\`\`\`text
z = w₁x₁ + w₂x₂ + ... + wₙxₙ + b = WᵀX + b
a = σ(z)
\`\`\`

## 2. Common Activation Functions
- **ReLU**: max(0, x) - solves vanishing gradients for x > 0.
- **GELU**: x * Phi(x) - smooth activation used in modern Transformers.
- **Sigmoid**: 1 / (1 + e^-x) - maps inputs to (0, 1).

## 3. Backpropagation & Chain Rule
1. Forward pass computes activations.
2. Cross-entropy loss calculates prediction deviation.
3. Backward pass applies chain rule to compute weight gradients.
4. Parameters updated: W := W - alpha * dL/dW.
`,
        language: "ENGLISH",
        cached: true,
        createdAt: new Date(Date.now() - 2 * 86400000).toISOString(),
    },
};

export const MOCK_QUIZZES: Record<string, Quiz> = {
    "demo-123": {
        id: "quiz-123",
        videoId: "demo-123",
        title: "Neural Networks & Backpropagation Assessment",
        questions: [
            {
                index: 1,
                questionText: "Why is non-linearity required in deep neural network activation functions?",
                options: [
                    "Without non-linearity, multiple layers collapse mathematically into a single linear transformation.",
                    "Non-linearity increases floating-point performance on GPUs.",
                    "Linear functions cannot calculate floating-point weights.",
                    "It ensures that weights never exceed 1.0."
                ],
                correctAnswer: "Without non-linearity, multiple layers collapse mathematically into a single linear transformation.",
                explanation: "Any composition of linear transformations is itself a single linear transformation. Non-linearities are required to approximate complex non-linear functions.",
                marks: 10,
            },
            {
                index: 2,
                questionText: "Which activation function is most susceptible to the 'Dying Neuron' problem?",
                options: ["Standard ReLU", "Leaky ReLU", "Softmax", "GELU"],
                correctAnswer: "Standard ReLU",
                explanation: "When inputs to a standard ReLU are negative, its gradient is strictly 0.",
                marks: 10,
            },
            {
                index: 3,
                questionText: "True or False: Initializing all weights in a neural network to zero is fine as long as biases are non-zero.",
                options: ["True", "False"],
                correctAnswer: "False",
                explanation: "Initializing all weights to zero causes symmetric gradients across all hidden neurons in a layer, preventing individual neurons from learning distinct features.",
                marks: 10,
            },
            {
                index: 4,
                questionText: "What does the chain rule compute in backpropagation?",
                options: [
                    "The derivative of the final loss with respect to intermediate weights by multiplying local gradients.",
                    "The sum of all inputs entering a layer.",
                    "The number of floating point operations per training epoch.",
                    "The optimal learning rate schedule."
                ],
                correctAnswer: "The derivative of the final loss with respect to intermediate weights by multiplying local gradients.",
                explanation: "Backpropagation is a direct application of the calculus chain rule.",
                marks: 10,
            },
            {
                index: 5,
                questionText: "Which parameter in Adam optimization controls the exponential decay rate for running momentum averages?",
                options: ["Beta 1 (β₁)", "Epsilon (ε)", "Batch size", "Weight decay penalty"],
                correctAnswer: "Beta 1 (β₁)",
                explanation: "β₁ controls the first moment vector (exponential decay average of past gradients), typically set to 0.9.",
                marks: 10,
            },
        ],
        difficulty: "MEDIUM",
        questionType: "MCQ",
        language: "ENGLISH",
        totalQuestions: 5,
        cached: true,
        createdAt: new Date(Date.now() - 2 * 86400000).toISOString(),
    },
};

export const MOCK_LEADERBOARD: LeaderboardEntry[] = [
    { rank: 1, userId: "u-101", totalPoints: 1840, quizzesTaken: 42, quizzesPassed: 40, avgScore: 94.2 },
    { rank: 2, userId: "u-102", totalPoints: 1710, quizzesTaken: 39, quizzesPassed: 36, avgScore: 91.8 },
    { rank: 3, userId: MOCK_STUDENT_USER.id, totalPoints: 1540, quizzesTaken: 31, quizzesPassed: 29, avgScore: 89.5 },
    { rank: 4, userId: "u-104", totalPoints: 1390, quizzesTaken: 28, quizzesPassed: 25, avgScore: 86.4 },
    { rank: 5, userId: "u-105", totalPoints: 1260, quizzesTaken: 24, quizzesPassed: 22, avgScore: 88.0 },
    { rank: 6, userId: "u-106", totalPoints: 1120, quizzesTaken: 20, quizzesPassed: 18, avgScore: 83.2 },
    { rank: 7, userId: "u-107", totalPoints: 980, quizzesTaken: 18, quizzesPassed: 16, avgScore: 85.0 },
    { rank: 8, userId: "u-108", totalPoints: 850, quizzesTaken: 15, quizzesPassed: 13, avgScore: 81.5 },
];

export const MOCK_MY_STATS: UserStats = {
    userId: MOCK_STUDENT_USER.id,
    rank: 3,
    totalAttempts: 31,
    completedAttempts: 31,
    passedAttempts: 29,
    passRate: 93.5,
    avgScore: 89.5,
    totalPoints: 1540,
};

export const MOCK_MY_PROGRESS: LearningProgress[] = [
    {
        id: "prog-1",
        userId: MOCK_STUDENT_USER.id,
        videoId: "demo-123",
        status: "COMPLETED",
        progressPercentage: 100,
        attemptsCount: 3,
        bestScore: 90,
        createdAt: new Date(Date.now() - 86400000 * 3).toISOString(),
        updatedAt: new Date().toISOString(),
    },
    {
        id: "prog-2",
        userId: MOCK_STUDENT_USER.id,
        videoId: "demo-124",
        status: "COMPLETED",
        progressPercentage: 90,
        attemptsCount: 2,
        bestScore: 85,
        createdAt: new Date(Date.now() - 86400000 * 2).toISOString(),
        updatedAt: new Date().toISOString(),
    },
    {
        id: "prog-3",
        userId: MOCK_STUDENT_USER.id,
        videoId: "demo-125",
        status: "IN_PROGRESS",
        progressPercentage: 65,
        attemptsCount: 1,
        bestScore: 80,
        createdAt: new Date(Date.now() - 86400000).toISOString(),
        updatedAt: new Date().toISOString(),
    },
];

export const MOCK_ATTEMPTS: QuizAttempt[] = [
    {
        id: "att-001",
        quizId: "quiz-123",
        userId: MOCK_STUDENT_USER.id,
        score: 45,
        totalMarks: 50,
        correctAnswers: 4,
        wrongAnswers: 1,
        unanswered: 0,
        percentage: 90,
        status: "COMPLETED",
        isPassed: true,
        answers: { "0": "Without non-linearity...", "1": "Standard ReLU" },
        startedAt: new Date(Date.now() - 7200000).toISOString(),
        completedAt: new Date(Date.now() - 6600000).toISOString(),
        createdAt: new Date(Date.now() - 7200000).toISOString(),
    },
    {
        id: "att-002",
        quizId: "quiz-124",
        userId: MOCK_STUDENT_USER.id,
        score: 40,
        totalMarks: 50,
        correctAnswers: 4,
        wrongAnswers: 1,
        unanswered: 0,
        percentage: 80,
        status: "COMPLETED",
        isPassed: true,
        answers: {},
        startedAt: new Date(Date.now() - 86400000 * 2).toISOString(),
        completedAt: new Date(Date.now() - 86400000 * 2 + 600000).toISOString(),
        createdAt: new Date(Date.now() - 86400000 * 2).toISOString(),
    },
];

export const MOCK_TRANSCRIPT: Transcript = {
    videoId: "demo-123",
    transcript: "Welcome everyone to our deep learning lecture series. Today we will break down artificial neural networks and backpropagation from scratch.",
    language: "ENGLISH",
    wordCount: 22,
    characterCount: 145,
    cached: true,
    segments: [
        { text: "Welcome everyone to our deep learning lecture series.", startSeconds: 0, endSeconds: 5 },
        { text: "Today we will break down artificial neural networks and backpropagation from scratch.", startSeconds: 5, endSeconds: 12 },
        { text: "Let's first examine what a single perceptron computes.", startSeconds: 12, endSeconds: 22 },
    ],
    createdAt: new Date().toISOString(),
};
