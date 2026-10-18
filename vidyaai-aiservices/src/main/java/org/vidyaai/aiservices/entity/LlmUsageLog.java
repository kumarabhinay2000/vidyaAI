package org.vidyaai.aiservices.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.vidyaai.aiservices.enums.LlmProvider;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "llm_usage_logs",
        indexes = {
                @Index(name = "idx_usage_provider", columnList = "llm_provider"),
                @Index(name = "idx_usage_created_at", columnList = "created_at"),
                @Index(name = "idx_usage_success", columnList = "success"),
                @Index(name = "idx_usage_cache_hit", columnList = "cache_hit")
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LlmUsageLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "service_name", nullable = false, length = 100)
    private String serviceName;

    @Column(name = "prompt_template_id")
    private UUID promptTemplateId;

    @Column(name = "operation", length = 50)
    private String operation;

    @Enumerated(EnumType.STRING)
    @Column(name = "llm_provider", length = 50)
    private LlmProvider llmProvider;

    @Column(name = "model_name", length = 100)
    private String modelName;

    @Column(name = "input_tokens")
    private Integer inputTokens;

    @Column(name = "output_tokens")
    private Integer outputTokens;

    @Column(name = "total_tokens")
    private Integer totalTokens;

    @Column(name = "response_time_ms")
    private Long responseTimeMs;

    @Column(name = "cost", precision = 10, scale = 6)
    private BigDecimal cost;

    @Column(name = "success", nullable = false)
    @Builder.Default
    private Boolean success = true;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "cache_hit", nullable = false)
    @Builder.Default
    private Boolean cacheHit = false;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "video_id")
    private UUID videoId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public boolean hasCacheHit() {
        return Boolean.TRUE.equals(this.cacheHit);
    }

    public boolean isFailed() {
        return Boolean.FALSE.equals(this.success);
    }
}