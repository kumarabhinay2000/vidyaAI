package org.vidyaai.quizservices.service;

import org.vidyaai.quizservices.entity.QuizAttempt;

import java.util.UUID;

public interface QuizEventProducer {

    void publishQuizAttempted(QuizAttempt attempt, UUID videoId);

    void publishQuizCompleted(QuizAttempt attempt, UUID videoId);
}