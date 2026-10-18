package org.vidyaai.videoservices.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YouTubeMetadataResponse {

    private String videoId;
    private String title;
    private String description;
    private String channelTitle;
    private String thumbnailUrl;
    private Integer durationSeconds;
    private Long viewCount;
    private String publishedAt;
    private String language;
}