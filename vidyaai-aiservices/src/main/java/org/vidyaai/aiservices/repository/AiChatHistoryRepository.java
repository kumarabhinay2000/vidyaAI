package org.vidyaai.aiservices.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.vidyaai.aiservices.entity.AiChatHistory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AiChatHistoryRepository extends JpaRepository<AiChatHistory, UUID> {

    List<AiChatHistory> findByUserIdAndVideoIdOrderByCreatedAtAsc(UUID userId, UUID videoId);

    List<AiChatHistory> findBySessionIdOrderByCreatedAtAsc(String sessionId);

    @Query("SELECT ch FROM AiChatHistory ch WHERE ch.userId = :userId " +
            "AND ch.videoId = :videoId ORDER BY ch.createdAt DESC")
    List<AiChatHistory> findRecentByUserAndVideo(
            @Param("userId") UUID userId,
            @Param("videoId") UUID videoId,
            org.springframework.data.domain.Pageable pageable);

    long countByUserId(UUID userId);

    long countByVideoId(UUID videoId);

    @Modifying
    @Transactional
    @Query("DELETE FROM AiChatHistory ch WHERE ch.createdAt < :cutoff")
    int deleteOlderThan(@Param("cutoff") LocalDateTime cutoff);

    @Query("SELECT AVG(ch.responseTimeMs) FROM AiChatHistory ch WHERE ch.userId = :userId")
    Double avgResponseTimeByUser(@Param("userId") UUID userId);
}