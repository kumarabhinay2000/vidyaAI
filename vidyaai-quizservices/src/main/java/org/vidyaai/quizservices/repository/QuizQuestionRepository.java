package org.vidyaai.quizservices.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.vidyaai.quizservices.entity.QuizQuestion;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, UUID> {

    List<QuizQuestion> findByQuizIdOrderByQuestionOrderAsc(UUID quizId);

    long countByQuizId(UUID quizId);

    @Modifying
    @Transactional
    @Query("DELETE FROM QuizQuestion q WHERE q.quizId = :quizId")
    int deleteByQuizId(@Param("quizId") UUID quizId);

    @Query("SELECT q FROM QuizQuestion q WHERE q.quizId = :quizId AND q.difficultyScore >= :minDifficulty")
    List<QuizQuestion> findDifficultQuestions(@Param("quizId") UUID quizId,
                                              @Param("minDifficulty") double minDifficulty);
}