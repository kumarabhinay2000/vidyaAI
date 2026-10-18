package org.vidyaai.quizservices.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.vidyaai.quizservices.enums.ProgressStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "learning_progress",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_progress_user_video", columnNames = {"user_id", "video_id"})
        },
        indexes = {
                @Index(name = "idx_progress_user_id", columnList = "user_id"),
                @Index(name = "idx_progress_video_id", columnList = "video_id"),
                @Index(name = "idx_progress_status", columnList = "status")
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "video_id", nullable = false)
    private UUID videoId;

    @Column(name = "quiz_id")
    private UUID quizId;

    @Column(name = "best_attempt_id")
    private UUID bestAttemptId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ProgressStatus status = ProgressStatus.NOT_STARTED;

    @Column(name = "progress_percentage")
    @Builder.Default
    private Integer progressPercentage = 0;

    @Column(name = "attempts_count")
    @Builder.Default
    private Integer attemptsCount = 0;

    @Column(name = "best_score")
    @Builder.Default
    private Integer bestScore = 0;

    @Column(name = "last_accessed")
    private LocalDateTime lastAccessed;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Business methods

    public void start() {
        if (this.status == ProgressStatus.NOT_STARTED) {
            this.status = ProgressStatus.IN_PROGRESS;
        }
        this.lastAccessed = LocalDateTime.now();
    }

    public void complete() {
        this.status = ProgressStatus.COMPLETED;
        this.progressPercentage = 100;
        this.lastAccessed = LocalDateTime.now();
    }

    public void incrementAttempts() {
        this.attemptsCount = (this.attemptsCount == null ? 0 : this.attemptsCount) + 1;
        this.lastAccessed = LocalDateTime.now();
    }

    public void updateBestScore(int score) {
        if (this.bestScore == null || score > this.bestScore) {
            this.bestScore = score;
        }
    }
}