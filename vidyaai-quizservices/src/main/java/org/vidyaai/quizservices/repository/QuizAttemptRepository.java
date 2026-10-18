package org.vidyaai.quizservices.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.vidyaai.quizservices.entity.QuizAttempt;
import org.vidyaai.quizservices.enums.AttemptStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, UUID> {

    List<QuizAttempt> findByQuizIdAndUserIdOrderByCreatedAtDesc(UUID quizId, UUID userId);

    List<QuizAttempt> findByUserIdOrderByCreatedAtDesc(UUID userId);

    List<QuizAttempt> findByQuizIdOrderByCreatedAtDesc(UUID quizId);

    Optional<QuizAttempt> findByIdAndUserId(UUID id, UUID userId);

    long countByQuizIdAndUserId(UUID quizId, UUID userId);

    long countByUserId(UUID userId);

    long countByUserIdAndStatus(UUID userId, AttemptStatus status);

    @Query("SELECT COUNT(a) FROM QuizAttempt a WHERE a.userId = :userId AND a.quizId = :quizId " +
            "AND a.createdAt >= :since")
    long countRecentAttempts(@Param("userId") UUID userId,
                             @Param("quizId") UUID quizId,
                             @Param("since") LocalDateTime since);

    @Query("SELECT a FROM QuizAttempt a WHERE a.userId = :userId AND a.quizId = :quizId " +
            "ORDER BY a.score DESC, a.completedAt ASC")
    List<QuizAttempt> findBestAttempts(@Param("userId") UUID userId,
                                       @Param("quizId") UUID quizId);

    @Query("SELECT AVG(a.percentage) FROM QuizAttempt a WHERE a.quizId = :quizId " +
            "AND a.status = 'COMPLETED'")
    Double avgPercentageByQuiz(@Param("quizId") UUID quizId);

    @Query("SELECT MAX(a.score) FROM QuizAttempt a WHERE a.quizId = :quizId " +
            "AND a.status = 'COMPLETED'")
    Integer maxScoreByQuiz(@Param("quizId") UUID quizId);

    @Query("SELECT MIN(a.score) FROM QuizAttempt a WHERE a.quizId = :quizId " +
            "AND a.status = 'COMPLETED'")
    Integer minScoreByQuiz(@Param("quizId") UUID quizId);

    @Query("SELECT COUNT(a) FROM QuizAttempt a WHERE a.quizId = :quizId " +
            "AND a.status = 'COMPLETED'")
    long countCompletedByQuiz(@Param("quizId") UUID quizId);

    @Query("SELECT COUNT(a) FROM QuizAttempt a WHERE a.quizId = :quizId " +
            "AND a.isPassed = true AND a.status = 'COMPLETED'")
    long countPassedByQuiz(@Param("quizId") UUID quizId);

    @Query("SELECT AVG(a.timeTakenSeconds) FROM QuizAttempt a WHERE a.quizId = :quizId " +
            "AND a.status = 'COMPLETED'")
    Double avgTimeTakenByQuiz(@Param("quizId") UUID quizId);
}