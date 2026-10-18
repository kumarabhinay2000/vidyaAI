package org.vidyaai.videoservices.event;

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
public class VideoDeletedEvent {

    private UUID eventId;
    private UUID videoId;
    private UUID userId;
    private LocalDateTime deletedAt;
}