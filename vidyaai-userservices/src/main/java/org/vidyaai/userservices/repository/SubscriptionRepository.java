package org.vidyaai.userservices.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.vidyaai.userservices.entity.Subscription;
import org.vidyaai.userservices.enums.PlanType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

    Optional<Subscription> findByUserIdAndIsActiveTrue(UUID userId);

    Optional<Subscription> findByIdAndUserId(UUID id, UUID userId);

    List<Subscription> findByUserIdOrderByCreatedAtDesc(UUID userId);

    List<Subscription> findAllByIsActiveTrueAndEndDateBefore(LocalDateTime date);

    List<Subscription> findAllByPlanTypeAndIsActiveTrue(PlanType planType);

    boolean existsByUserIdAndIsActiveTrue(UUID userId);

    @Modifying
    @Transactional
    @Query("UPDATE Subscription s SET s.isActive = false WHERE s.userId = :userId")
    int deactivateAllSubscriptionsForUser(@Param("userId") UUID userId);

    @Modifying
    @Transactional
    @Query("UPDATE Subscription s SET s.videosUsed = s.videosUsed + 1 WHERE s.userId = :userId AND s.isActive = true")
    int incrementVideoUsage(@Param("userId") UUID userId);

    @Query("SELECT s FROM Subscription s WHERE s.userId = :userId AND s.isActive = true AND s.endDate > CURRENT_TIMESTAMP")
    Optional<Subscription> findActiveValidSubscription(@Param("userId") UUID userId);

    @Query("SELECT s FROM Subscription s WHERE s.userId = :userId AND s.isActive = true ORDER BY s.createdAt DESC LIMIT 1")
    Optional<Subscription> findLatestActiveSubscription(@Param("userId") UUID userId);

    @Query("SELECT s FROM Subscription s WHERE s.isActive = true AND s.endDate IS NOT NULL AND s.endDate <= :date")
    List<Subscription> findExpiredActiveSubscriptions(@Param("date") LocalDateTime date);

    @Modifying
    @Transactional
    @Query("UPDATE Subscription s SET s.planType = :planType, s.isActive = true, s.endDate = :endDate, s.videoLimit = :videoLimit, s.updatedAt = CURRENT_TIMESTAMP WHERE s.id = :subscriptionId")
    int updateSubscriptionPlan(
            @Param("subscriptionId") UUID subscriptionId,
            @Param("planType") PlanType planType,
            @Param("endDate") LocalDateTime endDate,
            @Param("videoLimit") Integer videoLimit
    );
}