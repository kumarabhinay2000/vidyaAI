package org.vidyaai.videoservices.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YouTubeVideoRequest {

    @NotBlank(message = "YouTube URL is required")
    @Size(max = 1000, message = "URL must not exceed 1000 characters")
    private String youtubeUrl;

    @Size(max = 500, message = "Title must not exceed 500 characters")
    private String title;

    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;

    @Builder.Default
    private Boolean autoProcess = true;
}