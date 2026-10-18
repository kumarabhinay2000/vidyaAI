package org.vidyaai.videoservices.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.vidyaai.videoservices.client.YouTubeClient;
import org.vidyaai.videoservices.constant.CacheKeys;
import org.vidyaai.videoservices.constant.RedisTTL;
import org.vidyaai.videoservices.dto.request.UpdateVideoRequest;
import org.vidyaai.videoservices.dto.request.UploadVideoRequest;
import org.vidyaai.videoservices.dto.request.VideoSearchRequest;
import org.vidyaai.videoservices.dto.request.YouTubeVideoRequest;
import org.vidyaai.videoservices.dto.response.PageResponse;
import org.vidyaai.videoservices.dto.response.VideoResponse;
import org.vidyaai.videoservices.dto.response.VideoStatusResponse;
import org.vidyaai.videoservices.dto.response.VideoSummaryResponse;
import org.vidyaai.videoservices.dto.response.VideoUploadResponse;
import org.vidyaai.videoservices.dto.response.YouTubeMetadataResponse;
import org.vidyaai.videoservices.entity.Video;
import org.vidyaai.videoservices.entity.VideoChapter;
import org.vidyaai.videoservices.entity.VideoTag;
import org.vidyaai.videoservices.enums.SourceType;
import org.vidyaai.videoservices.enums.VideoStatus;
import org.vidyaai.videoservices.exception.AccessDeniedException;
import org.vidyaai.videoservices.exception.DuplicateVideoException;
import org.vidyaai.videoservices.exception.UploadFailedException;
import org.vidyaai.videoservices.exception.VideoNotFoundException;
import org.vidyaai.videoservices.mapper.VideoMapper;
import org.vidyaai.videoservices.producer.VideoEventProducer;
import org.vidyaai.videoservices.repository.VideoChapterRepository;
import org.vidyaai.videoservices.repository.VideoRepository;
import org.vidyaai.videoservices.repository.VideoTagRepository;
import org.vidyaai.videoservices.service.CacheService;
import org.vidyaai.videoservices.service.VideoService;
import org.vidyaai.videoservices.storage.StorageService;
import org.vidyaai.videoservices.util.FileUtil;
import org.vidyaai.videoservices.util.HashUtil;
import org.vidyaai.videoservices.validator.UploadVideoRequestValidator;
import org.vidyaai.videoservices.validator.UpdateVideoRequestValidator;
import org.vidyaai.videoservices.validator.YouTubeVideoRequestValidator;

import java.io.InputStream;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoServiceImpl implements VideoService {

    private final VideoRepository videoRepository;
    private final VideoTagRepository videoTagRepository;
    private final VideoChapterRepository videoChapterRepository;
    private final StorageService storageService;
    private final YouTubeClient youTubeClient;
    private final VideoMapper videoMapper;
    private final CacheService cacheService;
    private final VideoEventProducer videoEventProducer;
    private final UploadVideoRequestValidator uploadValidator;
    private final YouTubeVideoRequestValidator youtubeValidator;
    private final UpdateVideoRequestValidator updateValidator;
    private final FileUtil fileUtil;
    private final HashUtil hashUtil;

    @Override
    @Transactional
    public VideoUploadResponse uploadVideo(UploadVideoRequest request, MultipartFile file, UUID userId) {
        log.info("Upload video request: userId={} filename={} size={}",
                userId, file.getOriginalFilename(), file.getSize());
        try {
            uploadValidator.validate(request, file.getOriginalFilename(),
                    file.getContentType(), file.getSize());

            Video video = Video.builder()
                    .userId(userId)
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .language(request.getLanguage())
                    .sourceType(SourceType.UPLOAD)
                    .contentType(file.getContentType())
                    .fileSize(file.getSize())
                    .status(VideoStatus.UPLOADING)
                    .processingProgress(0)
                    .isDeleted(false)
                    .build();

            Video saved = videoRepository.save(video);

            String safeName = fileUtil.sanitizeFilename(file.getOriginalFilename());
            String objectKey = storageService.uploadVideo(saved.getId(), safeName,
                    file.getContentType(), file.getInputStream(), file.getSize());

            saved.setFilePath(objectKey);
            saved.setThumbnailUrl(storageService.getPublicUrl(objectKey));
            saved.setStatus(VideoStatus.UPLOADED);
            Video persisted = videoRepository.save(saved);

            cacheService.delete(CacheKeys.userVideos(userId.toString()));

            if (Boolean.TRUE.equals(true)) {
                videoEventProducer.publishVideoUploaded(persisted);
            }

            log.info("Video uploaded: id={} userId={}", persisted.getId(), userId);
            return videoMapper.toUploadResponse(persisted);
        } catch (Exception e) {
            log.error("Upload failed for user {}: {}", userId, e.getMessage(), e);
            throw new UploadFailedException("Video upload failed: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public VideoUploadResponse registerYouTubeVideo(YouTubeVideoRequest request, UUID userId) {
        log.info("Register YouTube video: userId={} url={}", userId, request.getYoutubeUrl());
        try {
            youtubeValidator.validate(request);

            String ytId = youTubeClient.extractVideoId(request.getYoutubeUrl());
            if (ytId == null) {
                throw new DuplicateVideoException("Invalid YouTube ID");
            }
            if (videoRepository.existsByUserIdAndYoutubeVideoIdAndIsDeletedFalse(userId, ytId)) {
                throw new DuplicateVideoException("YouTube video already exists: " + ytId);
            }

            YouTubeMetadataResponse meta = youTubeClient.fetchMetadata(request.getYoutubeUrl());

            Video video = Video.builder()
                    .userId(userId)
                    .title(StringUtils.hasText(request.getTitle()) ? request.getTitle() : meta.getTitle())
                    .description(StringUtils.hasText(request.getDescription())
                            ? request.getDescription() : meta.getDescription())
                    .language(meta.getLanguage())
                    .sourceType(SourceType.YOUTUBE)
                    .sourceUrl(request.getYoutubeUrl())
                    .youtubeVideoId(ytId)
                    .durationSeconds(meta.getDurationSeconds())
                    .thumbnailUrl(meta.getThumbnailUrl())
                    .status(VideoStatus.UPLOADED)
                    .processingProgress(0)
                    .isDeleted(false)
                    .build();

            Video saved = videoRepository.save(video);
            cacheService.delete(CacheKeys.userVideos(userId.toString()));

            if (Boolean.TRUE.equals(request.getAutoProcess())) {
                videoEventProducer.publishVideoUploaded(saved);
            }

            log.info("YouTube video registered: id={} ytId={}", saved.getId(), ytId);
            return videoMapper.toUploadResponse(saved);
        } catch (DuplicateVideoException e) {
            throw e;
        } catch (Exception e) {
            log.error("YouTube registration failed: {}", e.getMessage(), e);
            throw new UploadFailedException("Failed to register YouTube video", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public VideoResponse getVideo(UUID videoId, UUID requesterId) {
        try {
            Video video = videoRepository.findByIdAndIsDeletedFalse(videoId)
                    .orElseThrow(() -> new VideoNotFoundException(videoId));

            if (!video.isOwnedBy(requesterId) && !video.isReady()) {
                throw new AccessDeniedException("Access denied for unpublished video");
            }

            List<String> tags = videoTagRepository.findByVideoIdOrderByWeightDesc(videoId)
                    .stream().map(VideoTag::getTagName).toList();
            List<VideoChapter> chapters = videoChapterRepository.findByVideoIdOrderByChapterOrderAsc(videoId);

            return videoMapper.toResponse(video, tags, chapters);
        } catch (VideoNotFoundException | AccessDeniedException e) {
            throw e;
        } catch (Exception e) {
            log.error("Get video failed: {}", e.getMessage(), e);
            throw new UploadFailedException("Failed to fetch video", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public VideoResponse getVideoByIdAdmin(UUID videoId) {
        Video video = videoRepository.findByIdAndIsDeletedFalse(videoId)
                .orElseThrow(() -> new VideoNotFoundException(videoId));
        List<String> tags = videoTagRepository.findByVideoIdOrderByWeightDesc(videoId)
                .stream().map(VideoTag::getTagName).toList();
        List<VideoChapter> chapters = videoChapterRepository.findByVideoIdOrderByChapterOrderAsc(videoId);
        return videoMapper.toResponse(video, tags, chapters);
    }

    @Override
    @Transactional(readOnly = true)
    public VideoStatusResponse getStatus(UUID videoId, UUID requesterId) {
        try {
            Video video = videoRepository.findByIdAndIsDeletedFalse(videoId)
                    .orElseThrow(() -> new VideoNotFoundException(videoId));
            if (!video.isOwnedBy(requesterId)) {
                throw new AccessDeniedException("Access denied for video status");
            }
            return VideoStatusResponse.builder()
                    .videoId(video.getId())
                    .status(video.getStatus())
                    .progress(video.getProcessingProgress())
                    .message("Status: " + video.getStatus())
                    .updatedAt(video.getUpdatedAt())
                    .build();
        } catch (VideoNotFoundException | AccessDeniedException e) {
            throw e;
        } catch (Exception e) {
            log.error("Get status failed: {}", e.getMessage(), e);
            throw new UploadFailedException("Failed to fetch status", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<VideoSummaryResponse> search(VideoSearchRequest request, UUID userId) {
        try {
            Sort sort = Sort.by(
                    "DESC".equalsIgnoreCase(request.getSortDirection())
                            ? Sort.Direction.DESC : Sort.Direction.ASC,
                    request.getSortBy() != null ? request.getSortBy() : "createdAt");
            Pageable pageable = PageRequest.of(
                    request.getPage() != null ? request.getPage() : 0,
                    request.getSize() != null ? request.getSize() : 20,
                    sort);

            Page<Video> page;
            if (StringUtils.hasText(request.getQuery())) {
                page = videoRepository.searchByKeyword(request.getQuery(), pageable);
            } else {
                page = videoRepository.findByUserIdAndIsDeletedFalse(userId, pageable);
            }

            List<VideoSummaryResponse> content = page.getContent().stream()
                    .map(videoMapper::toSummaryResponse).toList();
            return PageResponse.of(content, page.getNumber(), page.getSize(),
                    page.getTotalElements(), page.getTotalPages());
        } catch (Exception e) {
            log.error("Search failed: {}", e.getMessage(), e);
            throw new UploadFailedException("Failed to search videos", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<VideoSummaryResponse> listUserVideos(UUID userId) {
        try {
            return videoRepository.findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(userId)
                    .stream().map(videoMapper::toSummaryResponse).toList();
        } catch (Exception e) {
            log.error("List user videos failed: {}", e.getMessage(), e);
            throw new UploadFailedException("Failed to list videos", e);
        }
    }

    @Override
    @Transactional
    public VideoResponse updateVideo(UUID videoId, UpdateVideoRequest request, UUID userId) {
        updateValidator.validate(request);
        try {
            Video video = videoRepository.findByIdAndIsDeletedFalse(videoId)
                    .orElseThrow(() -> new VideoNotFoundException(videoId));
            if (!video.isOwnedBy(userId)) {
                throw new AccessDeniedException("Access denied to update video");
            }
            StringBuilder updatedFields = new StringBuilder();
            if (StringUtils.hasText(request.getTitle())) {
                video.setTitle(request.getTitle());
                updatedFields.append("title,");
            }
            if (StringUtils.hasText(request.getDescription())) {
                video.setDescription(request.getDescription());
                updatedFields.append("description,");
            }
            if (StringUtils.hasText(request.getLanguage())) {
                video.setLanguage(request.getLanguage());
                updatedFields.append("language,");
            }
            Video saved = videoRepository.save(video);
            cacheService.delete(CacheKeys.video(videoId.toString()));
            cacheService.delete(CacheKeys.userVideos(userId.toString()));
            videoEventProducer.publishVideoUpdated(saved, updatedFields.toString());

            List<String> tags = videoTagRepository.findByVideoIdOrderByWeightDesc(videoId)
                    .stream().map(VideoTag::getTagName).toList();
            List<VideoChapter> chapters = videoChapterRepository.findByVideoIdOrderByChapterOrderAsc(videoId);
            return videoMapper.toResponse(saved, tags, chapters);
        } catch (VideoNotFoundException | AccessDeniedException e) {
            throw e;
        } catch (Exception e) {
            log.error("Update failed: {}", e.getMessage(), e);
            throw new UploadFailedException("Failed to update video", e);
        }
    }

    @Override
    @Transactional
    public void deleteVideo(UUID videoId, UUID userId) {
        try {
            Video video = videoRepository.findByIdAndIsDeletedFalse(videoId)
                    .orElseThrow(() -> new VideoNotFoundException(videoId));
            if (!video.isOwnedBy(userId)) {
                throw new AccessDeniedException("Access denied to delete video");
            }
            if (video.getFilePath() != null) {
                try {
                    storageService.delete(video.getFilePath());
                } catch (Exception e) {
                    log.warn("Storage delete failed for {}: {}", video.getFilePath(), e.getMessage());
                }
            }
            video.softDelete();
            videoRepository.save(video);
            videoTagRepository.deleteByVideoId(videoId);
            videoChapterRepository.deleteByVideoId(videoId);

            cacheService.delete(CacheKeys.video(videoId.toString()));
            cacheService.delete(CacheKeys.userVideos(userId.toString()));
            videoEventProducer.publishVideoDeleted(video);
            log.info("Video deleted: {}", videoId);
        } catch (VideoNotFoundException | AccessDeniedException e) {
            throw e;
        } catch (Exception e) {
            log.error("Delete failed: {}", e.getMessage(), e);
            throw new UploadFailedException("Failed to delete video", e);
        }
    }

    @Override
    @Transactional
    public void incrementView(UUID videoId) {
        try {
            videoRepository.incrementViewCount(videoId);
            cacheService.delete(CacheKeys.video(videoId.toString()));
        } catch (Exception e) {
            log.warn("Failed incrementing view for {}: {}", videoId, e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public InputStream streamVideo(UUID videoId, UUID requesterId) {
        try {
            Video video = videoRepository.findByIdAndIsDeletedFalse(videoId)
                    .orElseThrow(() -> new VideoNotFoundException(videoId));
            if (!video.isOwnedBy(requesterId) && !video.isReady()) {
                throw new AccessDeniedException("Access denied to stream video");
            }
            if (video.getFilePath() == null) {
                throw new VideoNotFoundException("Video file not available: " + videoId);
            }
            return storageService.download(video.getFilePath());
        } catch (VideoNotFoundException | AccessDeniedException e) {
            throw e;
        } catch (Exception e) {
            log.error("Stream failed: {}", e.getMessage(), e);
            throw new UploadFailedException("Failed to stream video", e);
        }
    }

    @SuppressWarnings("unused")
    private String hashUrl(String url) {
        return hashUtil.shortHash(url);
    }

    @SuppressWarnings("unused")
    private Duration videoTtl() {
        return Duration.ofSeconds(RedisTTL.VIDEO_SECONDS);
    }

    @SuppressWarnings("unused")
    private Optional<Video> tryFind(UUID id) {
        return videoRepository.findByIdAndIsDeletedFalse(id);
    }
}