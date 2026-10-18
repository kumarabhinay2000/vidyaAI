package org.vidyaai.aiservices.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.vidyaai.aiservices.enums.ContentType;
import org.vidyaai.aiservices.enums.LlmProvider;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "ai_contents",
        indexes = {
                @Index(name = "idx_ai_contents_video_id", columnList = "video_id"),
                @Index(name = "idx_ai_contents_content_type", columnList = "content_type"),
                @Index(name = "idx_ai_contents_is_cached", columnList = "is_cached"),
                @Index(name = "idx_ai_contents_created_at", columnList = "created_at")
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiContent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "video_id", nullable = false)
    private UUID videoId;

    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", nullable = false, length = 50)
    private ContentType contentType;

    @Column(name = "title", length = 500)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata;

    @Column(name = "tokens_used")
    private Integer tokensUsed;

    @Column(name = "processing_time_ms")
    private Long processingTimeMs;

    @Enumerated(EnumType.STRING)
    @Column(name = "llm_provider", length = 50)
    private LlmProvider llmProvider;

    @Column(name = "model_name", length = 100)
    private String modelName;

    @Column(name = "prompt_version", length = 50)
    private String promptVersion;

    @Column(name = "is_cached", nullable = false)
    @Builder.Default
    private Boolean isCached = false;

    @Column(name = "cache_hit", nullable = false)
    @Builder.Default
    private Boolean cacheHit = false;

    @Column(name = "relevance_score")
    private Double relevanceScore;

    @Column(name = "content_hash", length = 128)
    private String contentHash;

    @Column(name = "language", length = 10)
    private String language;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Business methods

    public boolean isSummary() {
        return this.contentType == ContentType.SUMMARY;
    }

    public boolean isQuiz() {
        return this.contentType == ContentType.QUIZ;
    }

    public boolean isNotes() {
        return this.contentType == ContentType.NOTES;
    }

    public boolean isFresh() {
        return this.createdAt != null &&
                this.createdAt.isAfter(LocalDateTime.now().minusDays(7));
    }
}