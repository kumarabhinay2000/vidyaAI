package org.vidyaai.quizservices.mapper;

import org.vidyaai.quizservices.dto.response.QuestionResponse;
import org.vidyaai.quizservices.dto.response.QuizResponse;
import org.vidyaai.quizservices.entity.Quiz;
import org.vidyaai.quizservices.entity.QuizQuestion;

import java.util.List;

public interface QuizMapper {

    QuizResponse toResponse(Quiz entity, List<QuizQuestion> questions);

    QuizResponse toSummaryResponse(Quiz entity);

    QuestionResponse toQuestionResponse(QuizQuestion entity);
}