package org.vidyaai.userservices.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginEvent {

    private UUID userId;
    private String email;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime loginAt;
}