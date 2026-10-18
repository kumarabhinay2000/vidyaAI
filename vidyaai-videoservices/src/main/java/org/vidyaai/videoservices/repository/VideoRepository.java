package org.vidyaai.videoservices.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.vidyaai.videoservices.entity.Video;
import org.vidyaai.videoservices.enums.VideoStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VideoRepository extends JpaRepository<Video, UUID> {

    Optional<Video> findByIdAndIsDeletedFalse(UUID id);

    List<Video> findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(UUID userId);

    Page<Video> findByUserIdAndIsDeletedFalse(UUID userId, Pageable pageable);

    Optional<Video> findByYoutubeVideoIdAndIsDeletedFalse(String youtubeVideoId);

    Optional<Video> findFirstBySourceUrlAndUserIdAndIsDeletedFalse(String sourceUrl, UUID userId);

    List<Video> findByStatusAndIsDeletedFalse(VideoStatus status);

    List<Video> findByUserIdAndStatusAndIsDeletedFalse(UUID userId, VideoStatus status);

    long countByUserIdAndIsDeletedFalse(UUID userId);

    long countByStatusAndIsDeletedFalse(VideoStatus status);

    long countByIsDeletedFalse();

    boolean existsByUserIdAndYoutubeVideoIdAndIsDeletedFalse(UUID userId, String youtubeVideoId);

    @Query("SELECT v FROM Video v WHERE v.isDeleted = false " +
            "AND (LOWER(v.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(v.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Video> searchByKeyword(@Param("query") String query, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Video v SET v.isDeleted = true, v.deletedAt = :deletedAt, " +
            "v.status = 'DELETED' WHERE v.id = :id")
    int softDeleteById(@Param("id") UUID id, @Param("deletedAt") LocalDateTime deletedAt);

    @Modifying
    @Transactional
    @Query("UPDATE Video v SET v.viewCount = v.viewCount + 1 WHERE v.id = :id")
    int incrementViewCount(@Param("id") UUID id);

    @Modifying
    @Transactional
    @Query("UPDATE Video v SET v.status = :status, v.processingProgress = :progress WHERE v.id = :id")
    int updateStatusAndProgress(@Param("id") UUID id,
                                @Param("status") VideoStatus status,
                                @Param("progress") int progress);

    @Query("SELECT v FROM Video v WHERE v.isDeleted = false " +
            "AND v.status IN :statuses AND v.createdAt < :before")
    List<Video> findStaleByStatuses(@Param("statuses") List<VideoStatus> statuses,
                                    @Param("before") LocalDateTime before);
}