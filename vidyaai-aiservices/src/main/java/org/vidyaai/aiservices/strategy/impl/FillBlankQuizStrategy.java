package org.vidyaai.aiservices.strategy.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.vidyaai.aiservices.constant.PromptNames;
import org.vidyaai.aiservices.enums.QuizQuestionType;
import org.vidyaai.aiservices.strategy.QuizStrategy;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class FillBlankQuizStrategy implements QuizStrategy {

    private static final String SCHEMA = """
            {
              "title": "string",
              "questions": [
                {
                  "index": 1,
                  "question": "The _____ is responsible for ...",
                  "options": [],
                  "correctAnswer": "string",
                  "explanation": "string",
                  "marks": 1
                }
              ]
            }
            """;

    @Override
    public QuizQuestionType getSupportedType() {
        return QuizQuestionType.FILL_BLANK;
    }

    @Override
    public String getPromptName() {
        return PromptNames.QUIZ_FILL_BLANK;
    }

    @Override
    public Map<String, Object> buildVariables(String content, String difficulty,
                                              int numberOfQuestions, String languageName) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("content", content);
        vars.put("difficulty", difficulty);
        vars.put("numberOfQuestions", numberOfQuestions);
        vars.put("language", languageName);
        return vars;
    }

    @Override
    public String getExpectedSchema() {
        return SCHEMA;
    }
}