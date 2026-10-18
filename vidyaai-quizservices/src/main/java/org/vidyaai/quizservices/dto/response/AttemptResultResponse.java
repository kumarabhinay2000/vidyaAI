package org.vidyaai.quizservices.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttemptResultResponse {

    private UUID attemptId;
    private UUID quizId;
    private Integer score;
    private Integer totalMarks;
    private Double percentage;
    private Boolean isPassed;
    private Integer correctAnswers;
    private Integer wrongAnswers;
    private Integer unanswered;
    private List<AnswerReview> reviews;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnswerReview {
        private UUID questionId;
        private String questionText;
        private String givenAnswer;
        private String correctAnswer;
        private Boolean isCorrect;
        private String explanation;
        private Integer marksAwarded;
    }
}