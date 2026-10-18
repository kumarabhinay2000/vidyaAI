package org.vidyaai.videoservices.service;

import org.springframework.web.multipart.MultipartFile;
import org.vidyaai.videoservices.dto.request.UpdateVideoRequest;
import org.vidyaai.videoservices.dto.request.UploadVideoRequest;
import org.vidyaai.videoservices.dto.request.VideoSearchRequest;
import org.vidyaai.videoservices.dto.request.YouTubeVideoRequest;
import org.vidyaai.videoservices.dto.response.PageResponse;
import org.vidyaai.videoservices.dto.response.VideoResponse;
import org.vidyaai.videoservices.dto.response.VideoStatusResponse;
import org.vidyaai.videoservices.dto.response.VideoSummaryResponse;
import org.vidyaai.videoservices.dto.response.VideoUploadResponse;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

public interface VideoService {

    VideoUploadResponse uploadVideo(UploadVideoRequest request, MultipartFile file, UUID userId);

    VideoUploadResponse registerYouTubeVideo(YouTubeVideoRequest request, UUID userId);

    VideoResponse getVideo(UUID videoId, UUID requesterId);

    VideoResponse getVideoByIdAdmin(UUID videoId);

    VideoStatusResponse getStatus(UUID videoId, UUID requesterId);

    PageResponse<VideoSummaryResponse> search(VideoSearchRequest request, UUID userId);

    List<VideoSummaryResponse> listUserVideos(UUID userId);

    VideoResponse updateVideo(UUID videoId, UpdateVideoRequest request, UUID userId);

    void deleteVideo(UUID videoId, UUID userId);

    void incrementView(UUID videoId);

    InputStream streamVideo(UUID videoId, UUID requesterId);
}