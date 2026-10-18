package org.vidyaai.videoservices.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;
import org.vidyaai.videoservices.enums.SourceType;
import org.vidyaai.videoservices.enums.VideoStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "videos",
        indexes = {
                @Index(name = "idx_videos_user_id", columnList = "user_id"),
                @Index(name = "idx_videos_status", columnList = "status"),
                @Index(name = "idx_videos_source_type", columnList = "source_type"),
                @Index(name = "idx_videos_created_at", columnList = "created_at"),
                @Index(name = "idx_videos_is_deleted", columnList = "is_deleted")
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "title", nullable = false, length = 500)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false, length = 20)
    private SourceType sourceType;

    @Column(name = "source_url", length = 1000)
    private String sourceUrl;

    @Column(name = "youtube_video_id", length = 20)
    private String youtubeVideoId;

    @Column(name = "file_path", length = 500)
    private String filePath;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "content_type", length = 100)
    private String contentType;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Column(name = "resolution", length = 20)
    private String resolution;

    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;

    @Column(name = "language", length = 10)
    private String language;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private VideoStatus status = VideoStatus.PENDING;

    @Column(name = "processing_progress")
    @Builder.Default
    private Integer processingProgress = 0;

    @Column(name = "transcript", columnDefinition = "TEXT")
    private String transcript;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata;

    @Column(name = "view_count", nullable = false)
    @Builder.Default
    private Integer viewCount = 0;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Business methods

    public boolean isYouTube() {
        return this.sourceType == SourceType.YOUTUBE;
    }

    public boolean isUploaded() {
        return this.sourceType == SourceType.UPLOAD;
    }

    public boolean isReady() {
        return this.status == VideoStatus.COMPLETED;
    }

    public boolean isProcessing() {
        return this.status == VideoStatus.PROCESSING
                || this.status == VideoStatus.UPLOADING;
    }

    public boolean isFailed() {
        return this.status == VideoStatus.FAILED;
    }

    public void markProcessing() {
        this.status = VideoStatus.PROCESSING;
        this.processingProgress = 0;
    }

    public void markCompleted() {
        this.status = VideoStatus.COMPLETED;
        this.processingProgress = 100;
    }

    public void markFailed() {
        this.status = VideoStatus.FAILED;
    }

    public void updateProgress(int progress) {
        this.processingProgress = Math.max(0, Math.min(100, progress));
    }

    public void incrementView() {
        this.viewCount = (this.viewCount == null ? 0 : this.viewCount) + 1;
    }

    public void softDelete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
        this.status = VideoStatus.DELETED;
    }

    public boolean isOwnedBy(UUID otherUserId) {
        return this.userId != null && this.userId.equals(otherUserId);
    }
}