package org.vidyaai.quizservices.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.vidyaai.quizservices.enums.QuestionType;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "quiz_questions",
        indexes = {
                @Index(name = "idx_quiz_questions_quiz_id", columnList = "quiz_id"),
                @Index(name = "idx_quiz_questions_type", columnList = "question_type")
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "quiz_id", nullable = false)
    private UUID quizId;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false, length = 20)
    private QuestionType questionType;

    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    private String questionText;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "options", columnDefinition = "jsonb")
    private String options;

    @Column(name = "correct_answer", nullable = false, length = 500)
    private String correctAnswer;

    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation;

    @Column(name = "difficulty_score")
    private Double difficultyScore;

    @Column(name = "marks", nullable = false)
    @Builder.Default
    private Integer marks = 1;

    @Column(name = "question_order")
    private Integer questionOrder;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Business methods

    public boolean isCorrect(String givenAnswer) {
        if (givenAnswer == null || correctAnswer == null) {
            return false;
        }
        return correctAnswer.trim().equalsIgnoreCase(givenAnswer.trim());
    }
}