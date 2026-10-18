-- =========================================================
-- VidyaAI Video Service - Initial Schema
-- =========================================================

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ---------------- videos ----------------
CREATE TABLE IF NOT EXISTS videos (
                                      id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id               UUID NOT NULL,
    title                 VARCHAR(500) NOT NULL,
    description           TEXT,
    source_type           VARCHAR(20) NOT NULL,
    source_url            VARCHAR(1000),
    youtube_video_id      VARCHAR(20),
    file_path             VARCHAR(500),
    file_size             BIGINT,
    content_type          VARCHAR(100),
    duration_seconds      INTEGER,
    resolution            VARCHAR(20),
    thumbnail_url         VARCHAR(500),
    language              VARCHAR(10),
    status                VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    processing_progress   INTEGER DEFAULT 0,
    transcript            TEXT,
    metadata              JSONB,
    view_count            INTEGER NOT NULL DEFAULT 0,
    is_deleted            BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at            TIMESTAMP,
    created_at            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

CREATE INDEX IF NOT EXISTS idx_videos_user_id       ON videos(user_id);
CREATE INDEX IF NOT EXISTS idx_videos_status        ON videos(status);
CREATE INDEX IF NOT EXISTS idx_videos_source_type   ON videos(source_type);
CREATE INDEX IF NOT EXISTS idx_videos_created_at    ON videos(created_at);
CREATE INDEX IF NOT EXISTS idx_videos_is_deleted    ON videos(is_deleted);
CREATE INDEX IF NOT EXISTS idx_videos_yt_id         ON videos(youtube_video_id);

-- ---------------- video_processing_logs ----------------
CREATE TABLE IF NOT EXISTS video_processing_logs (
                                                     id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    video_id       UUID NOT NULL REFERENCES videos(id) ON DELETE CASCADE,
    step           VARCHAR(50) NOT NULL,
    status         VARCHAR(30) NOT NULL,
    message        TEXT,
    error_code     VARCHAR(50),
    retry_count    INTEGER DEFAULT 0,
    started_at     TIMESTAMP,
    completed_at   TIMESTAMP,
    created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

CREATE INDEX IF NOT EXISTS idx_vpl_video_id   ON video_processing_logs(video_id);
CREATE INDEX IF NOT EXISTS idx_vpl_step       ON video_processing_logs(step);
CREATE INDEX IF NOT EXISTS idx_vpl_status     ON video_processing_logs(status);

-- ---------------- video_tags ----------------
CREATE TABLE IF NOT EXISTS video_tags (
                                          id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    video_id     UUID NOT NULL REFERENCES videos(id) ON DELETE CASCADE,
    tag_name     VARCHAR(100) NOT NULL,
    weight       DOUBLE PRECISION DEFAULT 1.0,
    created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

CREATE INDEX IF NOT EXISTS idx_video_tags_video_id ON video_tags(video_id);
CREATE INDEX IF NOT EXISTS idx_video_tags_name     ON video_tags(tag_name);

-- ---------------- video_chapters ----------------
CREATE TABLE IF NOT EXISTS video_chapters (
                                              id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    video_id      UUID NOT NULL REFERENCES videos(id) ON DELETE CASCADE,
    title         VARCHAR(255) NOT NULL,
    start_time    INTEGER NOT NULL,
    end_time      INTEGER,
    chapter_order INTEGER,
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

CREATE INDEX IF NOT EXISTS idx_video_chapters_video_id ON video_chapters(video_id);