package org.vidyaai.videoservices.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.vidyaai.videoservices.entity.VideoChapter;

import java.util.List;
import java.util.UUID;

@Repository
public interface VideoChapterRepository extends JpaRepository<VideoChapter, UUID> {

    List<VideoChapter> findByVideoIdOrderByChapterOrderAsc(UUID videoId);

    @Modifying
    @Transactional
    @Query("DELETE FROM VideoChapter c WHERE c.videoId = :videoId")
    int deleteByVideoId(@Param("videoId") UUID videoId);

    long countByVideoId(UUID videoId);
}