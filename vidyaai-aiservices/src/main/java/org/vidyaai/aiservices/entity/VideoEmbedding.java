package org.vidyaai.aiservices.entity;

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
        name = "video_embeddings",
        indexes = {
                @Index(name = "idx_embeddings_video_id", columnList = "video_id"),
                @Index(name = "idx_embeddings_chunk_index", columnList = "video_id,chunk_index")
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoEmbedding {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "video_id", nullable = false)
    private UUID videoId;

    @Column(name = "content_chunk", nullable = false, columnDefinition = "TEXT")
    private String contentChunk;

    /**
     * Stored as native vector in PostgreSQL (pgvector).
     * Hibernate will handle conversion via a custom UserType if needed.
     * For simplicity we store as text and cast in native queries.
     */
    @Column(name = "embedding", columnDefinition = "vector(768)")
    private String embedding;

    @Column(name = "chunk_index", nullable = false)
    private Integer chunkIndex;

    @Column(name = "chunk_type", length = 50)
    private String chunkType;

    @Column(name = "token_count")
    private Integer tokenCount;

    @Column(name = "start_time_seconds")
    private Integer startTimeSeconds;

    @Column(name = "end_time_seconds")
    private Integer endTimeSeconds;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}