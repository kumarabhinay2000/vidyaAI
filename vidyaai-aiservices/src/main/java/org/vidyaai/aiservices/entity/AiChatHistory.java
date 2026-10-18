package org.vidyaai.aiservices.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.vidyaai.aiservices.enums.ChatFeedback;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "ai_chat_history",
        indexes = {
                @Index(name = "idx_chat_user_id", columnList = "user_id"),
                @Index(name = "idx_chat_video_id", columnList = "video_id"),
                @Index(name = "idx_chat_created_at", columnList = "created_at")
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiChatHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "video_id")
    private UUID videoId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "session_id", length = 100)
    private String sessionId;

    @Column(name = "question", nullable = false, columnDefinition = "TEXT")
    private String question;

    @Column(name = "answer", nullable = false, columnDefinition = "TEXT")
    private String answer;

    @Column(name = "context_used", columnDefinition = "TEXT")
    private String contextUsed;

    @Column(name = "tokens_used")
    private Integer tokensUsed;

    @Column(name = "response_time_ms")
    private Long responseTimeMs;

    @Enumerated(EnumType.STRING)
    @Column(name = "feedback_sentiment", length = 20)
    private ChatFeedback feedbackSentiment;

    @Column(name = "is_helpful")
    private Boolean isHelpful;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public void markHelpful() {
        this.isHelpful = true;
        this.feedbackSentiment = ChatFeedback.POSITIVE;
    }

    public void markNotHelpful() {
        this.isHelpful = false;
        this.feedbackSentiment = ChatFeedback.NEGATIVE;
    }
}