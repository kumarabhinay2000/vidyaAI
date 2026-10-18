package org.vidyaai.quizservices.entity;

import jakarta.persistence.*;
        import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "leaderboard",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_leaderboard_user", columnNames = "user_id")
        },
        indexes = {
                @Index(name = "idx_leaderboard_user_id", columnList = "user_id"),
                @Index(name = "idx_leaderboard_rank", columnList = "rank"),
                @Index(name = "idx_leaderboard_points", columnList = "total_points")
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Leaderboard {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(name = "total_points", nullable = false)
    @Builder.Default
    private Integer totalPoints = 0;

    @Column(name = "quizzes_taken", nullable = false)
    @Builder.Default
    private Integer quizzesTaken = 0;

    @Column(name = "quizzes_passed", nullable = false)
    @Builder.Default
    private Integer quizzesPassed = 0;

    @Column(name = "avg_score", nullable = false)
    @Builder.Default
    private Double avgScore = 0.0;

    @Column(name = "rank")
    private Integer rank;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Business methods

    public void addPoints(int points) {
        this.totalPoints = (this.totalPoints == null ? 0 : this.totalPoints) + points;
    }

    public void incrementQuizzesTaken() {
        this.quizzesTaken = (this.quizzesTaken == null ? 0 : this.quizzesTaken) + 1;
    }

    public void incrementQuizzesPassed() {
        this.quizzesPassed = (this.quizzesPassed == null ? 0 : this.quizzesPassed) + 1;
    }

    public void recalculateAverage(int newScore) {
        if (this.quizzesTaken == null || this.quizzesTaken == 0) {
            this.avgScore = (double) newScore;
            return;
        }
        double total = this.avgScore * (this.quizzesTaken - 1) + newScore;
        this.avgScore = total / this.quizzesTaken;
    }
}