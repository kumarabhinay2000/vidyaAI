package org.vidyaai.quizservices.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.vidyaai.quizservices.enums.DifficultyLevel;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateQuizRequest {

    @Size(max = 500, message = "Title must not exceed 500 characters")
    private String title;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    private DifficultyLevel difficultyLevel;

    @Min(value = 1, message = "Passing score must be at least 1")
    @Max(value = 100, message = "Passing score must be at most 100")
    private Integer passingScore;

    @Min(value = 1, message = "Time limit must be at least 1 minute")
    @Max(value = 300, message = "Time limit must be at most 300 minutes")
    private Integer timeLimitMinutes;

    private Boolean published;
}