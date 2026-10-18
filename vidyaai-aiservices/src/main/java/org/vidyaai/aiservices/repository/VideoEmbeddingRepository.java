package org.vidyaai.aiservices.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.vidyaai.aiservices.entity.VideoEmbedding;

import java.util.List;
import java.util.UUID;

@Repository
public interface VideoEmbeddingRepository extends JpaRepository<VideoEmbedding, UUID> {

    List<VideoEmbedding> findByVideoIdOrderByChunkIndexAsc(UUID videoId);

    long countByVideoId(UUID videoId);

    @Modifying
    @Transactional
    @Query("DELETE FROM VideoEmbedding e WHERE e.videoId = :videoId")
    int deleteByVideoId(@Param("videoId") UUID videoId);

    /**
     * Native vector similarity search using pgvector's cosine distance operator.
     * The embedding string must be in pgvector format: '[0.1,0.2,...]'
     */
    @Query(value = "SELECT * FROM video_embeddings " +
            "WHERE video_id = CAST(:videoId AS uuid) " +
            "ORDER BY embedding <=> CAST(:embedding AS vector) " +
            "LIMIT :limit",
            nativeQuery = true)
    List<VideoEmbedding> findTopKSimilar(
            @Param("videoId") UUID videoId,
            @Param("embedding") String embedding,
            @Param("limit") int limit);

    @Query(value = "SELECT * FROM video_embeddings " +
            "WHERE video_id = CAST(:videoId AS uuid) " +
            "ORDER BY embedding <=> CAST(:embedding AS vector) " +
            "LIMIT :limit",
            nativeQuery = true)
    List<VideoEmbedding> searchSimilar(
            @Param("videoId") UUID videoId,
            @Param("embedding") String embedding,
            @Param("limit") int limit);
}
