-- =========================================================
-- VidyaAI Quiz Service - Initial Schema
-- =========================================================

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ---------------- quizzes ----------------
CREATE TABLE IF NOT EXISTS quizzes (
                                       id                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    video_id           UUID NOT NULL,
    user_id            UUID,
    title              VARCHAR(500) NOT NULL,
    description        TEXT,
    difficulty_level   VARCHAR(20),
    total_questions    INTEGER NOT NULL DEFAULT 0,
    passing_score      INTEGER NOT NULL DEFAULT 70,
    time_limit_minutes INTEGER,
    metadata           JSONB,
    is_published       BOOLEAN NOT NULL DEFAULT TRUE,
    is_deleted         BOOLEAN NOT NULL DEFAULT FALSE,
    created_at         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

CREATE INDEX IF NOT EXISTS idx_quizzes_video_id     ON quizzes(video_id);
CREATE INDEX IF NOT EXISTS idx_quizzes_difficulty   ON quizzes(difficulty_level);
CREATE INDEX IF NOT EXISTS idx_quizzes_created_at   ON quizzes(created_at);
CREATE INDEX IF NOT EXISTS idx_quizzes_user_id      ON quizzes(user_id);

-- ---------------- quiz_questions ----------------
CREATE TABLE IF NOT EXISTS quiz_questions (
                                              id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    quiz_id           UUID NOT NULL REFERENCES quizzes(id) ON DELETE CASCADE,
    question_type     VARCHAR(20) NOT NULL,
    question_text     TEXT NOT NULL,
    options           JSONB,
    correct_answer    VARCHAR(500) NOT NULL,
    explanation       TEXT,
    difficulty_score  DOUBLE PRECISION,
    marks             INTEGER NOT NULL DEFAULT 1,
    question_order    INTEGER,
    created_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

CREATE INDEX IF NOT EXISTS idx_quiz_questions_quiz_id ON quiz_questions(quiz_id);
CREATE INDEX IF NOT EXISTS idx_quiz_questions_type    ON quiz_questions(question_type);

-- ---------------- quiz_attempts ----------------
CREATE TABLE IF NOT EXISTS quiz_attempts (
                                             id                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    quiz_id            UUID NOT NULL REFERENCES quizzes(id) ON DELETE CASCADE,
    user_id            UUID NOT NULL,
    score              INTEGER DEFAULT 0,
    total_marks        INTEGER DEFAULT 0,
    correct_answers    INTEGER DEFAULT 0,
    wrong_answers      INTEGER DEFAULT 0,
    unanswered         INTEGER DEFAULT 0,
    percentage         DOUBLE PRECISION,
    time_taken_seconds INTEGER,
    answers            JSONB,
    status             VARCHAR(20) NOT NULL DEFAULT 'IN_PROGRESS',
    is_passed          BOOLEAN,
    started_at         TIMESTAMP NOT NULL,
    completed_at       TIMESTAMP,
    created_at         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

CREATE INDEX IF NOT EXISTS idx_quiz_attempts_quiz_id     ON quiz_attempts(quiz_id);
CREATE INDEX IF NOT EXISTS idx_quiz_attempts_user_id     ON quiz_attempts(user_id);
CREATE INDEX IF NOT EXISTS idx_quiz_attempts_status      ON quiz_attempts(status);
CREATE INDEX IF NOT EXISTS idx_quiz_attempts_created_at  ON quiz_attempts(created_at);

-- ---------------- quiz_analytics ----------------
CREATE TABLE IF NOT EXISTS quiz_analytics (
                                              id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    quiz_id             UUID NOT NULL UNIQUE REFERENCES quizzes(id) ON DELETE CASCADE,
    total_attempts      INTEGER DEFAULT 0,
    avg_score           DOUBLE PRECISION DEFAULT 0,
    highest_score       INTEGER DEFAULT 0,
    lowest_score        INTEGER DEFAULT 100,
    pass_rate           DOUBLE PRECISION DEFAULT 0,
    avg_time_taken      INTEGER DEFAULT 0,
    difficult_questions JSONB,
    easy_questions      JSONB,
    topic_wise_score    JSONB,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

CREATE INDEX IF NOT EXISTS idx_quiz_analytics_quiz_id ON quiz_analytics(quiz_id);

-- ---------------- learning_progress ----------------
CREATE TABLE IF NOT EXISTS learning_progress (
                                                 id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id             UUID NOT NULL,
    video_id            UUID NOT NULL,
    quiz_id             UUID,
    best_attempt_id     UUID,
    status              VARCHAR(20) NOT NULL DEFAULT 'NOT_STARTED',
    progress_percentage INTEGER DEFAULT 0,
    attempts_count      INTEGER DEFAULT 0,
    best_score          INTEGER DEFAULT 0,
    last_accessed       TIMESTAMP,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_progress_user_video UNIQUE (user_id, video_id)
    );

CREATE INDEX IF NOT EXISTS idx_progress_user_id    ON learning_progress(user_id);
CREATE INDEX IF NOT EXISTS idx_progress_video_id   ON learning_progress(video_id);
CREATE INDEX IF NOT EXISTS idx_progress_status     ON learning_progress(status);

-- ---------------- leaderboard ----------------
CREATE TABLE IF NOT EXISTS leaderboard (
                                           id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id          UUID NOT NULL UNIQUE,
    total_points     INTEGER NOT NULL DEFAULT 0,
    quizzes_taken    INTEGER NOT NULL DEFAULT 0,
    quizzes_passed   INTEGER NOT NULL DEFAULT 0,
    avg_score        DOUBLE PRECISION NOT NULL DEFAULT 0,
    rank             INTEGER,
    updated_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

CREATE INDEX IF NOT EXISTS idx_leaderboard_user_id     ON leaderboard(user_id);
CREATE INDEX IF NOT EXISTS idx_leaderboard_rank        ON leaderboard(rank);
CREATE INDEX IF NOT EXISTS idx_leaderboard_points      ON leaderboard(total_points);