package org.vidyaai.quizservices.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitQuizRequest {

    @NotNull(message = "Quiz ID is required")
    private UUID quizId;

    @NotNull(message = "Attempt ID is required")
    private UUID attemptId;

    @NotNull(message = "Answers are required")
    private Map<UUID, String> answers;

    private Integer timeTakenSeconds;
}