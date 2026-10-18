package org.vidyaai.quizservices.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.vidyaai.quizservices.entity.QuizAnalytics;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuizAnalyticsRepository extends JpaRepository<QuizAnalytics, UUID> {

    Optional<QuizAnalytics> findByQuizId(UUID quizId);

    boolean existsByQuizId(UUID quizId);

    void deleteByQuizId(UUID quizId);
}