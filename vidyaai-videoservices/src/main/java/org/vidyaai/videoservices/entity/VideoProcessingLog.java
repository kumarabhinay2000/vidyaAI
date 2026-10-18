package org.vidyaai.videoservices.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.vidyaai.videoservices.enums.ProcessingStep;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "video_processing_logs",
        indexes = {
                @Index(name = "idx_vpl_video_id", columnList = "video_id"),
                @Index(name = "idx_vpl_step", columnList = "step"),
                @Index(name = "idx_vpl_status", columnList = "status")
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoProcessingLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "video_id", nullable = false)
    private UUID videoId;

    @Enumerated(EnumType.STRING)
    @Column(name = "step", nullable = false, length = 50)
    private ProcessingStep step;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "error_code", length = 50)
    private String errorCode;

    @Column(name = "retry_count")
    @Builder.Default
    private Integer retryCount = 0;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Business methods

    public void start() {
        this.startedAt = LocalDateTime.now();
        this.status = "STARTED";
    }

    public void complete() {
        this.completedAt = LocalDateTime.now();
        this.status = "COMPLETED";
    }

    public void fail(String errorCode, String message) {
        this.completedAt = LocalDateTime.now();
        this.status = "FAILED";
        this.errorCode = errorCode;
        this.message = message;
    }

    public void incrementRetry() {
        this.retryCount = (this.retryCount == null ? 0 : this.retryCount) + 1;
    }
}