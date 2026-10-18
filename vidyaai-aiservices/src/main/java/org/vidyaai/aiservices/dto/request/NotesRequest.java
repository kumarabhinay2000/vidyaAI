package org.vidyaai.aiservices.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.vidyaai.aiservices.enums.Language;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotesRequest {

    @NotNull(message = "Video ID is required")
    private UUID videoId;

    @Builder.Default
    private Language language = Language.ENGLISH;

    @Builder.Default
    private Boolean includeTimestamps = true;

    @Builder.Default
    private Boolean useCache = true;

    @Builder.Default
    private Boolean forceRegenerate = false;
}