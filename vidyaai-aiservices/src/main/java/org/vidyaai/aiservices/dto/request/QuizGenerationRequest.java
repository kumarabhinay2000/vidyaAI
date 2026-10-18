package org.vidyaai.aiservices.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.vidyaai.aiservices.enums.Language;
import org.vidyaai.aiservices.enums.QuizDifficulty;
import org.vidyaai.aiservices.enums.QuizQuestionType;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizGenerationRequest {

    @NotNull(message = "Video ID is required")
    private UUID videoId;

    @NotNull(message = "Question type is required")
    @Builder.Default
    private QuizQuestionType questionType = QuizQuestionType.MCQ;

    @Builder.Default
    private QuizDifficulty difficulty = QuizDifficulty.MEDIUM;

    @Min(value = 3, message = "Minimum 3 questions required")
    @Max(value = 20, message = "Maximum 20 questions allowed")
    @Builder.Default
    private Integer numberOfQuestions = 5;

    @Builder.Default
    private Language language = Language.ENGLISH;

    @Builder.Default
    private Boolean useCache = true;

    @Builder.Default
    private Boolean forceRegenerate = false;
}