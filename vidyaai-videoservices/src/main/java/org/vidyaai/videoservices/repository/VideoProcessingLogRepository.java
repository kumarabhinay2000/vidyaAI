package org.vidyaai.videoservices.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.vidyaai.videoservices.entity.VideoProcessingLog;
import org.vidyaai.videoservices.enums.ProcessingStep;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface VideoProcessingLogRepository extends JpaRepository<VideoProcessingLog, UUID> {

    List<VideoProcessingLog> findByVideoIdOrderByCreatedAtAsc(UUID videoId);

    List<VideoProcessingLog> findByVideoIdAndStep(UUID videoId, ProcessingStep step);

    @Query("SELECT l FROM VideoProcessingLog l WHERE l.videoId = :videoId " +
            "ORDER BY l.createdAt DESC LIMIT 1")
    VideoProcessingLog findLatestByVideoId(@Param("videoId") UUID videoId);

    long countByVideoIdAndStatus(UUID videoId, String status);

    @Modifying
    @Transactional
    @Query("DELETE FROM VideoProcessingLog l WHERE l.videoId = :videoId")
    int deleteByVideoId(@Param("videoId") UUID videoId);

    @Modifying
    @Transactional
    @Query("DELETE FROM VideoProcessingLog l WHERE l.createdAt < :cutoff")
    int deleteOlderThan(@Param("cutoff") LocalDateTime cutoff);
}