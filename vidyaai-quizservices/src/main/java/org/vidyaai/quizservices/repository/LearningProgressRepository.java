package org.vidyaai.quizservices.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.vidyaai.quizservices.entity.LearningProgress;
import org.vidyaai.quizservices.enums.ProgressStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LearningProgressRepository extends JpaRepository<LearningProgress, UUID> {

    Optional<LearningProgress> findByUserIdAndVideoId(UUID userId, UUID videoId);

    List<LearningProgress> findByUserIdOrderByLastAccessedDesc(UUID userId);

    List<LearningProgress> findByUserIdAndStatus(UUID userId, ProgressStatus status);

    long countByUserIdAndStatus(UUID userId, ProgressStatus status);

    @Query("SELECT COUNT(p) FROM LearningProgress p WHERE p.userId = :userId AND p.status = 'COMPLETED'")
    long countCompletedByUser(@Param("userId") UUID userId);

    @Query("SELECT AVG(p.bestScore) FROM LearningProgress p WHERE p.userId = :userId")
    Double avgBestScoreByUser(@Param("userId") UUID userId);
}