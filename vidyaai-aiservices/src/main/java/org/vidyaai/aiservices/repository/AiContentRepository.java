package org.vidyaai.aiservices.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.vidyaai.aiservices.entity.AiContent;
import org.vidyaai.aiservices.enums.ContentType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AiContentRepository extends JpaRepository<AiContent, UUID> {

    List<AiContent> findByVideoIdOrderByCreatedAtDesc(UUID videoId);

    List<AiContent> findByVideoIdAndContentType(UUID videoId, ContentType contentType);

    Optional<AiContent> findFirstByVideoIdAndContentTypeOrderByCreatedAtDesc(
            UUID videoId, ContentType contentType);

    Optional<AiContent> findFirstByContentHashAndContentType(
            String contentHash, ContentType contentType);

    Page<AiContent> findByContentType(ContentType contentType, Pageable pageable);

    @Query("SELECT ac FROM AiContent ac WHERE ac.videoId = :videoId AND ac.contentType = :type " +
            "AND ac.language = :language ORDER BY ac.createdAt DESC")
    List<AiContent> findByVideoIdAndTypeAndLanguage(
            @Param("videoId") UUID videoId,
            @Param("type") ContentType type,
            @Param("language") String language);

    boolean existsByVideoIdAndContentType(UUID videoId, ContentType contentType);

    long countByVideoId(UUID videoId);

    @Modifying
    @Transactional
    @Query("DELETE FROM AiContent ac WHERE ac.videoId = :videoId")
    int deleteByVideoId(@Param("videoId") UUID videoId);

    @Modifying
    @Transactional
    @Query("DELETE FROM AiContent ac WHERE ac.createdAt < :cutoff")
    int deleteOlderThan(@Param("cutoff") LocalDateTime cutoff);

    @Query("SELECT COUNT(ac) FROM AiContent ac WHERE ac.isCached = true AND ac.createdAt >= :since")
    long countCachedSince(@Param("since") LocalDateTime since);
}