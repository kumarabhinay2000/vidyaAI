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
        name = "video_tags",
        indexes = {
                @Index(name = "idx_video_tags_video_id", columnList = "video_id"),
                @Index(name = "idx_video_tags_name", columnList = "tag_name")
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoTag {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "video_id", nullable = false)
    private UUID videoId;

    @Column(name = "tag_name", nullable = false, length = 100)
    private String tagName;

    @Column(name = "weight")
    @Builder.Default
    private Double weight = 1.0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}