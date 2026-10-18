package org.vidyaai.videoservices.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "video_chapters",
        indexes = {
                @Index(name = "idx_video_chapters_video_id", columnList = "video_id")
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoChapter {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "video_id", nullable = false)
    private UUID videoId;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "start_time", nullable = false)
    private Integer startTime;

    @Column(name = "end_time")
    private Integer endTime;

    @Column(name = "chapter_order")
    private Integer chapterOrder;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Business methods

    public Integer getDurationSeconds() {
        if (startTime != null && endTime != null) {
            return endTime - startTime;
        }
        return null;
    }
}