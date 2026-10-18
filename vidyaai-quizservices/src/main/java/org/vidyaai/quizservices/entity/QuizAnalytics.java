package org.vidyaai.quizservices.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "quiz_analytics",
        indexes = {
                @Index(name = "idx_quiz_analytics_quiz_id", columnList = "quiz_id")
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizAnalytics {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "quiz_id", nullable = false, unique = true)
    private UUID quizId;

    @Column(name = "total_attempts")
    @Builder.Default
    private Integer totalAttempts = 0;

    @Column(name = "avg_score")
    @Builder.Default
    private Double avgScore = 0.0;

    @Column(name = "highest_score")
    @Builder.Default
    private Integer highestScore = 0;

    @Column(name = "lowest_score")
    @Builder.Default
    private Integer lowestScore = 100;

    @Column(name = "pass_rate")
    @Builder.Default
    private Double passRate = 0.0;

    @Column(name = "avg_time_taken")
    @Builder.Default
    private Integer avgTimeTaken = 0;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "difficult_questions", columnDefinition = "jsonb")
    private String difficultQuestions;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "easy_questions", columnDefinition = "jsonb")
    private String easyQuestions;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "topic_wise_score", columnDefinition = "jsonb")
    private String topicWiseScore;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Business methods

    public void incrementAttempts() {
        this.totalAttempts = (this.totalAttempts == null ? 0 : this.totalAttempts) + 1;
    }

    public void recalculateAverages() {
        if (this.totalAttempts != null && this.totalAttempts > 0) {
            this.avgScore = this.avgScore / this.totalAttempts;
        }
    }
}