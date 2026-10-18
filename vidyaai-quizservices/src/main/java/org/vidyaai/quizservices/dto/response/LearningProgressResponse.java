package org.vidyaai.quizservices.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.vidyaai.quizservices.enums.ProgressStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningProgressResponse {

    private UUID id;
    private UUID userId;
    private UUID videoId;
    private UUID quizId;
    private ProgressStatus status;
    private Integer progressPercentage;
    private Integer attemptsCount;
    private Integer bestScore;
    private LocalDateTime lastAccessed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}