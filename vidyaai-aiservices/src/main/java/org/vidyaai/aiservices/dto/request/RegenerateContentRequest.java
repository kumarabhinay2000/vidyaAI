package org.vidyaai.aiservices.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.vidyaai.aiservices.enums.ContentType;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegenerateContentRequest {

    @NotNull(message = "Video ID is required")
    private UUID videoId;

    @NotNull(message = "Content type is required")
    private ContentType contentType;

    private String additionalInstructions;
}