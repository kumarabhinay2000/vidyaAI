package org.vidyaai.videoservices.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.vidyaai.videoservices.entity.VideoTag;

import java.util.List;
import java.util.UUID;

@Repository
public interface VideoTagRepository extends JpaRepository<VideoTag, UUID> {

    List<VideoTag> findByVideoIdOrderByWeightDesc(UUID videoId);

    List<VideoTag> findByTagName(String tagName);

    @Modifying
    @Transactional
    @Query("DELETE FROM VideoTag t WHERE t.videoId = :videoId")
    int deleteByVideoId(@Param("videoId") UUID videoId);

    @Query("SELECT DISTINCT t.tagName FROM VideoTag t ORDER BY t.tagName")
    List<String> findAllDistinctTagNames();
}