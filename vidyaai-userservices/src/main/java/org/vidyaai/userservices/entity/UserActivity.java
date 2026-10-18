package org.vidyaai.userservices.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name=" user_activities",
        indexes = {
                @Index(name="idx_user_activities_user_id", columnList="userId"),
                @Index(name="idx_user_activities_created_at", columnList="createdAt")
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserActivity {
    @Id
    @GeneratedValue(strategy=GenerationType.UUID)
    @Column(name="id", columnDefinition="UUID DEFAULT gen_random_uuid()")
    private UUID id;

    @Column(name="user_id", nullable=false)
    private UUID userId;

    @Column(name="action_type", nullable=false, length=50)
    private String actionType;

    @Column(name="metadata", columnDefinition="JSONB")
    private String metadata;

    @Column(name="ip_address")
    private String ipAddress;

    @Column(name="user_agent", columnDefinition="TEXT")
    private String userAgent;

    @CreationTimestamp
    @Column(name="created_at", nullable=false, updatable=false)
    private LocalDateTime createdAt;

}
