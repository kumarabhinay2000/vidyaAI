package org.vidyaai.aiservices.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.vidyaai.aiservices.enums.Language;
import org.vidyaai.aiservices.enums.LlmProvider;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotesResponse {

    private UUID id;
    private UUID videoId;
    private String title;
    private List<NoteSection> sections;
    private String fullContent;
    private Language language;
    private LlmProvider llmProvider;
    private String modelName;
    private Integer tokensUsed;
    private Long processingTimeMs;
    private Boolean cached;
    private LocalDateTime createdAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NoteSection {
        private String heading;
        private List<String> bulletPoints;
        private Integer startTimeSeconds;
    }
}
