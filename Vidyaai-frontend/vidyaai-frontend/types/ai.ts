export type SummaryType = "FIVE_POINT" | "TEN_POINT" | "DETAILED";
export type LanguageCode =
  "ENGLISH" | "HINDI" | "TAMIL" | "TELUGU" | "BENGALI" | "MARATHI";
export type QuizDifficulty = "EASY" | "MEDIUM" | "HARD";
export type QuizQuestionType = "MCQ" | "TRUE_FALSE" | "FILL_BLANK";
export type LlmProvider =
  "GROQ" | "OPENAI" | "GOOGLE_GENAI" | "DEEPSEEK" | "OLLAMA";

export interface Summary {
  id: string;
  videoId: string;
  title?: string | null;
  points: string[];
  fullContent: string;
  keywords: string[];
  type: SummaryType;
  language: LanguageCode;
  llmProvider?: LlmProvider | null;
  modelName?: string | null;
  tokensUsed?: number | null;
  processingTimeMs?: number | null;
  cached: boolean;
  createdAt: string;
}

export interface NoteSection {
  heading: string;
  bulletPoints: string[];
  startTimeSeconds?: number | null;
}

export interface Notes {
  id: string;
  videoId: string;
  title?: string | null;
  sections: NoteSection[];
  fullContent: string;
  language: LanguageCode;
  llmProvider?: LlmProvider | null;
  modelName?: string | null;
  tokensUsed?: number | null;
  processingTimeMs?: number | null;
  cached: boolean;
  createdAt: string;
}

export interface QuizQuestion {
  id?: string;
  index: number;
  questionText: string;
  options: string[];
  correctAnswer: string;
  explanation?: string | null;
  marks: number;
}

export interface Quiz {
  passingScore?: number;
  timeLimitMinutes?: number;
  id: string;
  videoId: string;
  title?: string | null;
  questions: QuizQuestion[];
  difficulty: QuizDifficulty;
  questionType: QuizQuestionType;
  language: LanguageCode;
  llmProvider?: LlmProvider | null;
  modelName?: string | null;
  totalQuestions: number;
  tokensUsed?: number | null;
  processingTimeMs?: number | null;
  cached: boolean;
  createdAt: string;
}

export interface ChatMessage {
  role: "user" | "assistant";
  content: string;
}

export interface ChatbotRequest {
  videoId: string;
  question: string;
  language?: LanguageCode;
  sessionId?: string;
  chatHistory?: ChatMessage[];
}

export interface ChatbotResponse {
  chatId: string;
  videoId: string;
  sessionId: string;
  answer: string;
  sources: string[];
  confidence?: number | null;
  llmProvider?: LlmProvider | null;
  modelName?: string | null;
  tokensUsed?: number | null;
  responseTimeMs?: number | null;
  cached: boolean;
  createdAt: string;
}

export interface TranscriptSegment {
  startSeconds: number;
  endSeconds?: number | null;
  text: string;
}

export interface Transcript {
  videoId: string;
  transcript: string;
  language: string;
  wordCount: number;
  characterCount: number;
  cached: boolean;
  segments: TranscriptSegment[];
  createdAt: string;
}

export interface GenerateSummaryPayload {
  videoId: string;
  type: SummaryType;
  language?: LanguageCode;
  useCache?: boolean;
  forceRegenerate?: boolean;
}

export interface GenerateNotesPayload {
  videoId: string;
  language?: LanguageCode;
  includeTimestamps?: boolean;
  useCache?: boolean;
  forceRegenerate?: boolean;
}

export interface GenerateQuizPayload {
  videoId: string;
  questionType: QuizQuestionType;
  difficulty: QuizDifficulty;
  numberOfQuestions: number;
  language?: LanguageCode;
  useCache?: boolean;
  forceRegenerate?: boolean;
}
