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
public class VideoUpdatedEvent {

    private UUID eventId;
    private UUID videoId;
    private UUID userId;
    private String updatedField;
    private LocalDateTime updatedAt;
}