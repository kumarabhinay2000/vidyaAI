package org.vidyaai.aiservices.strategy;

import org.vidyaai.aiservices.enums.QuizQuestionType;

import java.util.Map;

public interface QuizStrategy {

    QuizQuestionType getSupportedType();

    String getPromptName();

    Map<String, Object> buildVariables(String content, String difficulty,
                                       int numberOfQuestions, String languageName);

    String getExpectedSchema();
}