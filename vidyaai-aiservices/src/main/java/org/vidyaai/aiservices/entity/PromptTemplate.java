package org.vidyaai.aiservices.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;
import org.vidyaai.aiservices.enums.PromptCategory;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "prompt_templates",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_prompt_name_version", columnNames = {"name", "version"})
        },
        indexes = {
                @Index(name = "idx_prompt_name", columnList = "name"),
                @Index(name = "idx_prompt_active", columnList = "is_active"),
                @Index(name = "idx_prompt_category", columnList = "category")
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromptTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 50)
    private PromptCategory category;

    @Column(name = "template", nullable = false, columnDefinition = "TEXT")
    private String template;

    @Column(name = "version", nullable = false)
    @Builder.Default
    private Integer version = 1;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "variables", columnDefinition = "jsonb")
    private String variables;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "performance_score")
    private Double performanceScore;

    @Column(name = "success_rate")
    private Double successRate;

    @Column(name = "avg_tokens")
    private Integer avgTokens;

    @Column(name = "description", length = 500)
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void incrementVersion() {
        this.version = this.version + 1;
    }
}