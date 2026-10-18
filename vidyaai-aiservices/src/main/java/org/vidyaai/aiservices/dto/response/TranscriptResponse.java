package org.vidyaai.aiservices.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TranscriptResponse {

    private UUID videoId;
    private String transcript;
    private String language;
    private Integer wordCount;
    private Integer characterCount;
    private Boolean cached;
    private List<TranscriptSegment> segments;
    private LocalDateTime createdAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TranscriptSegment {
        private Integer startSeconds;
        private Integer endSeconds;
        private String text;
    }
}