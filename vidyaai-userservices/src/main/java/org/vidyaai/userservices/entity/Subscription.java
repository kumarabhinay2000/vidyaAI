package org.vidyaai.userservices.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.vidyaai.userservices.enums.PlanType;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "user_subscriptions",
        indexes = {
                @Index(name = "idx_user_subscriptions_user_id", columnList = "userId"),
                @Index(name = "idx_user_subscriptions_is_active", columnList = "isActive")
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "UUID DEFAULT gen_random_uuid()")
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan_type", nullable = false, length = 50)
    @Builder.Default
    private PlanType planType = PlanType.FREE;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "payment_id", length = 255)
    private String paymentId;

    @Column(name = "video_limit")
    @Builder.Default
    private Integer videoLimit = 5;

    @Column(name = "videos_used")
    @Builder.Default
    private Integer videosUsed = 0;

    @Column(name = "features", columnDefinition = "JSONB")
    private String features;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Business Methods
    public boolean hasRemainingVideos() {
        return videosUsed < videoLimit;
    }

    public void incrementVideosUsed() {
        this.videosUsed++;
    }

    public int getRemainingVideos() {
        return Math.max(0, videoLimit - videosUsed);
    }

    public boolean isExpired() {
        return endDate != null && LocalDateTime.now().isAfter(endDate);
    }

    public boolean canRenew() {
        return isExpired() && Boolean.TRUE.equals(isActive);
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void activate() {
        this.isActive = true;
    }
}