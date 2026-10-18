package org.vidyaai.videoservices.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoChapterResponse {

    private UUID id;
    private String title;
    private Integer startTime;
    private Integer endTime;
    private Integer chapterOrder;
}