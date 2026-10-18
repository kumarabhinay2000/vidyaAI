package org.vidyaai.quizservices.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.vidyaai.quizservices.entity.Quiz;
import org.vidyaai.quizservices.enums.DifficultyLevel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, UUID> {

    Optional<Quiz> findByIdAndIsDeletedFalse(UUID id);

    Optional<Quiz> findFirstByVideoIdAndIsDeletedFalseOrderByCreatedAtDesc(UUID videoId);

    List<Quiz> findByVideoIdAndIsDeletedFalse(UUID videoId);

    List<Quiz> findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(UUID userId);

    Page<Quiz> findByIsPublishedTrueAndIsDeletedFalse(Pageable pageable);

    List<Quiz> findByDifficultyLevelAndIsDeletedFalse(DifficultyLevel difficultyLevel);

    boolean existsByVideoIdAndIsDeletedFalse(UUID videoId);

    long countByIsDeletedFalse();

    long countByUserIdAndIsDeletedFalse(UUID userId);

    @Modifying
    @Transactional
    @Query("UPDATE Quiz q SET q.isDeleted = true, q.isPublished = false WHERE q.id = :id")
    int softDeleteById(@Param("id") UUID id);

    @Modifying
    @Transactional
    @Query("UPDATE Quiz q SET q.totalQuestions = :count WHERE q.id = :id")
    int updateQuestionCount(@Param("id") UUID id, @Param("count") int count);
}