package org.vidyaai.videoservices.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.vidyaai.videoservices.enums.SourceType;
import org.vidyaai.videoservices.enums.VideoStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoSearchRequest {

    private String query;

    private SourceType sourceType;

    private VideoStatus status;

    private String tag;

    @Min(value = 0, message = "Page must be at least 0")
    @Builder.Default
    private Integer page = 0;

    @Min(value = 1, message = "Size must be at least 1")
    @Max(value = 100, message = "Size must be at most 100")
    @Builder.Default
    private Integer size = 20;

    @Builder.Default
    private String sortBy = "createdAt";

    @Builder.Default
    private String sortDirection = "DESC";
}