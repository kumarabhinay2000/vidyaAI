package org.vidyaai.quizservices.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.vidyaai.quizservices.enums.DifficultyLevel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizResponse {

    private UUID id;
    private UUID videoId;
    private UUID userId;
    private String title;
    private String description;
    private DifficultyLevel difficultyLevel;
    private Integer totalQuestions;
    private Integer passingScore;
    private Integer timeLimitMinutes;
    private Boolean isPublished;
    private List<QuestionResponse> questions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}