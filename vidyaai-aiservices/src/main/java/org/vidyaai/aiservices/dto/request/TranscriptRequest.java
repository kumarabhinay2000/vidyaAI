package org.vidyaai.aiservices.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TranscriptRequest {

    @NotNull(message = "Video ID is required")
    private UUID videoId;

    private String sourceUrl;

    @Builder.Default
    private Boolean forceRegenerate = false;
}