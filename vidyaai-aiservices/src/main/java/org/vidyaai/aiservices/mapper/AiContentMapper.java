package org.vidyaai.aiservices.mapper;

import org.vidyaai.aiservices.dto.response.NotesResponse;
import org.vidyaai.aiservices.dto.response.QuizGenerationResponse;
import org.vidyaai.aiservices.dto.response.SummaryResponse;
import org.vidyaai.aiservices.entity.AiContent;

public interface AiContentMapper {

    SummaryResponse toSummaryResponse(AiContent entity);

    QuizGenerationResponse toQuizResponse(AiContent entity);

    NotesResponse toNotesResponse(AiContent entity);
}