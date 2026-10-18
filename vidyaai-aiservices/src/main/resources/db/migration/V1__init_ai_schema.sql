-- =========================================================
-- VidyaAI AI Service - Initial Schema
-- =========================================================

CREATE EXTENSION IF NOT EXISTS vector;
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ---------------- ai_contents ----------------
CREATE TABLE IF NOT EXISTS ai_contents (
                                           id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    video_id            UUID NOT NULL,
    content_type        VARCHAR(50) NOT NULL,
    title               VARCHAR(500),
    content             TEXT NOT NULL,
    metadata            JSONB,
    tokens_used         INTEGER,
    processing_time_ms  BIGINT,
    llm_provider        VARCHAR(50),
    model_name          VARCHAR(100),
    prompt_version      VARCHAR(50),
    is_cached           BOOLEAN NOT NULL DEFAULT FALSE,
    cache_hit           BOOLEAN NOT NULL DEFAULT FALSE,
    relevance_score     DOUBLE PRECISION,
    content_hash        VARCHAR(128),
    language            VARCHAR(10),
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

CREATE INDEX IF NOT EXISTS idx_ai_contents_video_id      ON ai_contents(video_id);
CREATE INDEX IF NOT EXISTS idx_ai_contents_content_type  ON ai_contents(content_type);
CREATE INDEX IF NOT EXISTS idx_ai_contents_is_cached     ON ai_contents(is_cached);
CREATE INDEX IF NOT EXISTS idx_ai_contents_created_at    ON ai_contents(created_at);
CREATE INDEX IF NOT EXISTS idx_ai_contents_content_hash  ON ai_contents(content_hash);

-- ---------------- ai_chat_history ----------------
CREATE TABLE IF NOT EXISTS ai_chat_history (
                                               id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    video_id            UUID,
    user_id             UUID NOT NULL,
    session_id          VARCHAR(100),
    question            TEXT NOT NULL,
    answer              TEXT NOT NULL,
    context_used        TEXT,
    tokens_used         INTEGER,
    response_time_ms    BIGINT,
    feedback_sentiment  VARCHAR(20),
    is_helpful          BOOLEAN,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

CREATE INDEX IF NOT EXISTS idx_chat_user_id       ON ai_chat_history(user_id);
CREATE INDEX IF NOT EXISTS idx_chat_video_id      ON ai_chat_history(video_id);
CREATE INDEX IF NOT EXISTS idx_chat_session_id    ON ai_chat_history(session_id);
CREATE INDEX IF NOT EXISTS idx_chat_created_at    ON ai_chat_history(created_at);

-- ---------------- video_embeddings ----------------
CREATE TABLE IF NOT EXISTS video_embeddings (
                                                id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    video_id            UUID NOT NULL,
    content_chunk       TEXT NOT NULL,
    embedding           vector(768),
    chunk_index         INTEGER NOT NULL,
    chunk_type          VARCHAR(50),
    token_count         INTEGER,
    start_time_seconds  INTEGER,
    end_time_seconds    INTEGER,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

CREATE INDEX IF NOT EXISTS idx_embeddings_video_id       ON video_embeddings(video_id);
CREATE INDEX IF NOT EXISTS idx_embeddings_chunk_index    ON video_embeddings(video_id, chunk_index);

-- HNSW index for cosine similarity
CREATE INDEX IF NOT EXISTS idx_embeddings_hnsw_cosine
    ON video_embeddings
    USING hnsw (embedding vector_cosine_ops);

-- ---------------- prompt_templates ----------------
CREATE TABLE IF NOT EXISTS prompt_templates (
                                                id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name                VARCHAR(100) NOT NULL,
    category            VARCHAR(50) NOT NULL,
    template            TEXT NOT NULL,
    version             INTEGER NOT NULL DEFAULT 1,
    variables           JSONB,
    is_active           BOOLEAN NOT NULL DEFAULT TRUE,
    performance_score   DOUBLE PRECISION,
    success_rate        DOUBLE PRECISION,
    avg_tokens          INTEGER,
    description         VARCHAR(500),
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_prompt_name_version UNIQUE (name, version)
    );

CREATE INDEX IF NOT EXISTS idx_prompt_name      ON prompt_templates(name);
CREATE INDEX IF NOT EXISTS idx_prompt_active    ON prompt_templates(is_active);
CREATE INDEX IF NOT EXISTS idx_prompt_category  ON prompt_templates(category);

-- ---------------- llm_usage_logs ----------------
CREATE TABLE IF NOT EXISTS llm_usage_logs (
                                              id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    service_name        VARCHAR(100) NOT NULL,
    prompt_template_id  UUID,
    operation           VARCHAR(50),
    llm_provider        VARCHAR(50),
    model_name          VARCHAR(100),
    input_tokens        INTEGER,
    output_tokens       INTEGER,
    total_tokens        INTEGER,
    response_time_ms    BIGINT,
    cost                NUMERIC(10, 6),
    success             BOOLEAN NOT NULL DEFAULT TRUE,
    error_message       TEXT,
    cache_hit           BOOLEAN NOT NULL DEFAULT FALSE,
    user_id             UUID,
    video_id            UUID,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

CREATE INDEX IF NOT EXISTS idx_usage_provider     ON llm_usage_logs(llm_provider);
CREATE INDEX IF NOT EXISTS idx_usage_created_at   ON llm_usage_logs(created_at);
CREATE INDEX IF NOT EXISTS idx_usage_success      ON llm_usage_logs(success);
CREATE INDEX IF NOT EXISTS idx_usage_cache_hit    ON llm_usage_logs(cache_hit);