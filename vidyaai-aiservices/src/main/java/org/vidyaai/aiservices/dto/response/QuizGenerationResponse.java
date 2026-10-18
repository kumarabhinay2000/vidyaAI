package org.vidyaai.aiservices.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.vidyaai.aiservices.enums.Language;
import org.vidyaai.aiservices.enums.LlmProvider;
import org.vidyaai.aiservices.enums.QuizDifficulty;
import org.vidyaai.aiservices.enums.QuizQuestionType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizGenerationResponse {

    private UUID id;
    private UUID videoId;
    private String title;
    private List<QuizQuestion> questions;
    private QuizDifficulty difficulty;
    private QuizQuestionType questionType;
    private Language language;
    private LlmProvider llmProvider;
    private String modelName;
    private Integer totalQuestions;
    private Integer tokensUsed;
    private Long processingTimeMs;
    private Boolean cached;
    private LocalDateTime createdAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuizQuestion {
        private Integer index;
        private String questionText;
        private List<String> options;
        private String correctAnswer;
        private String explanation;
        private Integer marks;
    }
}