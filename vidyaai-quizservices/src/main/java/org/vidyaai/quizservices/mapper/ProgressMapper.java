package org.vidyaai.quizservices.mapper;

import org.vidyaai.quizservices.dto.response.LearningProgressResponse;
import org.vidyaai.quizservices.entity.LearningProgress;

public interface ProgressMapper {

    LearningProgressResponse toResponse(LearningProgress entity);
}