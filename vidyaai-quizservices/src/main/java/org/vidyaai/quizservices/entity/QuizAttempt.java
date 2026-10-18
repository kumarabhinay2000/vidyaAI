package org.vidyaai.quizservices.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.vidyaai.quizservices.enums.AttemptStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "quiz_attempts",
        indexes = {
                @Index(name = "idx_quiz_attempts_quiz_id", columnList = "quiz_id"),
                @Index(name = "idx_quiz_attempts_user_id", columnList = "user_id"),
                @Index(name = "idx_quiz_attempts_status", columnList = "status"),
                @Index(name = "idx_quiz_attempts_created_at", columnList = "created_at")
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "quiz_id", nullable = false)
    private UUID quizId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "score")
    @Builder.Default
    private Integer score = 0;

    @Column(name = "total_marks")
    @Builder.Default
    private Integer totalMarks = 0;

    @Column(name = "correct_answers")
    @Builder.Default
    private Integer correctAnswers = 0;

    @Column(name = "wrong_answers")
    @Builder.Default
    private Integer wrongAnswers = 0;

    @Column(name = "unanswered")
    @Builder.Default
    private Integer unanswered = 0;

    @Column(name = "percentage")
    private Double percentage;

    @Column(name = "time_taken_seconds")
    private Integer timeTakenSeconds;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "answers", columnDefinition = "jsonb")
    private String answers;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private AttemptStatus status = AttemptStatus.IN_PROGRESS;

    @Column(name = "is_passed")
    private Boolean isPassed;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Business methods

    public void markCompleted() {
        this.status = AttemptStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    public void markTimedOut() {
        this.status = AttemptStatus.TIMED_OUT;
        this.completedAt = LocalDateTime.now();
    }

    public void markAbandoned() {
        this.status = AttemptStatus.ABANDONED;
        this.completedAt = LocalDateTime.now();
    }

    public boolean isCompleted() {
        return this.status == AttemptStatus.COMPLETED;
    }

    public boolean isPassed() {
        return Boolean.TRUE.equals(isPassed);
    }

    public void calculatePercentage() {
        if (totalMarks != null && totalMarks > 0) {
            this.percentage = (score * 100.0) / totalMarks;
        } else {
            this.percentage = 0.0;
        }
    }
}