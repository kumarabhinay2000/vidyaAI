package org.vidyaai.videoservices.mapper;

import org.vidyaai.videoservices.dto.response.VideoChapterResponse;
import org.vidyaai.videoservices.dto.response.VideoResponse;
import org.vidyaai.videoservices.dto.response.VideoSummaryResponse;
import org.vidyaai.videoservices.dto.response.VideoUploadResponse;
import org.vidyaai.videoservices.entity.Video;
import org.vidyaai.videoservices.entity.VideoChapter;

import java.util.List;

public interface VideoMapper {

    VideoResponse toResponse(Video entity, List<String> tags, List<VideoChapter> chapters);

    VideoSummaryResponse toSummaryResponse(Video entity);

    VideoUploadResponse toUploadResponse(Video entity);

    VideoChapterResponse toChapterResponse(VideoChapter entity);
}