package org.vidyaai.userservices.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.vidyaai.userservices.entity.UserActivity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface UserActivityRepository extends JpaRepository<UserActivity, UUID> {

    List<UserActivity> findByUserIdOrderByCreatedAtDesc(UUID userId);

    List<UserActivity> findByUserIdAndActionTypeOrderByCreatedAtDesc(UUID userId, String actionType);

    @Query("SELECT ua FROM UserActivity ua WHERE ua.userId = :userId AND ua.createdAt >= :fromDate AND ua.createdAt <= :toDate ORDER BY ua.createdAt DESC")
    List<UserActivity> findUserActivitiesBetweenDates(
            @Param("userId") UUID userId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );

    @Query("SELECT ua FROM UserActivity ua WHERE ua.userId = :userId ORDER BY ua.createdAt DESC LIMIT :limit")
    List<UserActivity> findRecentUserActivities(@Param("userId") UUID userId, @Param("limit") int limit);

    long countByUserIdAndActionType(UUID userId, String actionType);

    @Query("SELECT COUNT(ua) FROM UserActivity ua WHERE ua.userId = :userId AND ua.actionType = :actionType AND ua.createdAt >= :since")
    long countUserActionsSince(
            @Param("userId") UUID userId,
            @Param("actionType") String actionType,
            @Param("since") LocalDateTime since
    );
}