package org.vidyaai.videoservices.storage.minio;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.minio.*;
import io.minio.messages.Item;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.vidyaai.videoservices.config.StorageConfig;
import org.vidyaai.videoservices.exception.StorageException;
import org.vidyaai.videoservices.storage.StorageService;

import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@ConditionalOnProperty(name = "storage.minio.enabled", havingValue = "true", matchIfMissing = true)
@Slf4j
public class MinioStorageService implements StorageService {

    private final MinioClient minioClient;
    private final StorageConfig storageConfig;

    public MinioStorageService(MinioClient minioClient, StorageConfig storageConfig) {
        this.minioClient = minioClient;
        this.storageConfig = storageConfig;
        ensureBucketExists();
    }

    private void ensureBucketExists() {
        try {
            String bucket = storageConfig.getMinio().getBucket();
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucket).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
                log.info("Created MinIO bucket: {}", bucket);
            }
        } catch (Exception e) {
            log.error("Failed ensuring MinIO bucket: {}", e.getMessage(), e);
        }
    }

    @Override
    @CircuitBreaker(name = "minioStorage", fallbackMethod = "uploadFallback")
    public String uploadVideo(UUID videoId, String filename, String contentType,
                              InputStream stream, long size) {
        String objectKey = buildObjectKey(videoId, "video", filename);
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(storageConfig.getMinio().getBucket())
                    .object(objectKey)
                    .stream(stream, size, -1)
                    .contentType(contentType)
                    .build());
            log.info("Uploaded video to MinIO: {} ({} bytes)", objectKey, size);
            return objectKey;
        } catch (Exception e) {
            log.error("Failed uploading video to MinIO: {}", e.getMessage(), e);
            throw new StorageException("Failed to upload video to storage", e);
        }
    }

    @Override
    @CircuitBreaker(name = "minioStorage", fallbackMethod = "uploadThumbFallback")
    public String uploadThumbnail(UUID videoId, String filename, InputStream stream,
                                  long size, String contentType) {
        String objectKey = buildObjectKey(videoId, "thumbnail", filename);
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(storageConfig.getMinio().getBucket())
                    .object(objectKey)
                    .stream(stream, size, -1)
                    .contentType(contentType)
                    .build());
            log.info("Uploaded thumbnail to MinIO: {}", objectKey);
            return objectKey;
        } catch (Exception e) {
            log.error("Failed uploading thumbnail: {}", e.getMessage(), e);
            throw new StorageException("Failed to upload thumbnail", e);
        }
    }

    @Override
    @CircuitBreaker(name = "minioStorage", fallbackMethod = "downloadFallback")
    public InputStream download(String objectKey) {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(storageConfig.getMinio().getBucket())
                    .object(objectKey)
                    .build());
        } catch (Exception e) {
            log.error("Failed downloading from MinIO: {}", e.getMessage(), e);
            throw new StorageException("Failed to download object: " + objectKey, e);
        }
    }

    @Override
    public void delete(String objectKey) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(storageConfig.getMinio().getBucket())
                    .object(objectKey)
                    .build());
            log.info("Deleted object from MinIO: {}", objectKey);
        } catch (Exception e) {
            log.error("Failed deleting from MinIO: {}", e.getMessage(), e);
            throw new StorageException("Failed to delete object: " + objectKey, e);
        }
    }

    @Override
    public void deleteByPrefix(String prefix) {
        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(storageConfig.getMinio().getBucket())
                            .prefix(prefix)
                            .recursive(true)
                            .build());
            for (Result<Item> result : results) {
                Item item = result.get();
                delete(item.objectName());
            }
            log.info("Deleted MinIO objects under prefix: {}", prefix);
        } catch (Exception e) {
            log.error("Failed deleting prefix {}: {}", prefix, e.getMessage(), e);
            throw new StorageException("Failed to delete objects under prefix: " + prefix, e);
        }
    }

    @Override
    public boolean exists(String objectKey) {
        try {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(storageConfig.getMinio().getBucket())
                    .object(objectKey)
                    .build());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getPresignedUrl(String objectKey, int expirySeconds) {
        try {
            int expiry = expirySeconds > 0
                    ? expirySeconds
                    : storageConfig.getMinio().getPresignedUrlExpirySeconds();
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(storageConfig.getMinio().getBucket())
                    .object(objectKey)
                    .expiry(expiry, TimeUnit.SECONDS)
                    .build());
        } catch (Exception e) {
            log.error("Failed generating presigned URL: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public String getPublicUrl(String objectKey) {
        if (!StringUtils.hasText(objectKey)) {
            return null;
        }
        return String.format("%s/%s/%s",
                storageConfig.getMinio().getEndpoint(),
                storageConfig.getMinio().getBucket(),
                objectKey);
    }

    @Override
    public String getProviderName() {
        return "MINIO";
    }

    private String buildObjectKey(UUID videoId, String category, String filename) {
        String ext = extractExtension(filename);
        return String.format("videos/%s/%s%s", videoId, category, ext);
    }

    private String extractExtension(String filename) {
        if (!StringUtils.hasText(filename)) {
            return "";
        }
        int dot = filename.lastIndexOf('.');
        return dot > 0 ? filename.substring(dot) : "";
    }

    // fallbacks

    public String uploadFallback(UUID videoId, String filename, String contentType,
                                 InputStream stream, long size, Throwable t) {
        log.error("Fallback: MinIO upload failed for videoId={}: {}", videoId, t.getMessage());
        throw new StorageException("Storage unavailable", t);
    }

    public String uploadThumbFallback(UUID videoId, String filename, InputStream stream,
                                      long size, String contentType, Throwable t) {
        log.error("Fallback: MinIO thumbnail upload failed for videoId={}", videoId);
        throw new StorageException("Storage unavailable", t);
    }

    public InputStream downloadFallback(String objectKey, Throwable t) {
        log.error("Fallback: MinIO download failed for {}", objectKey);
        throw new StorageException("Storage unavailable", t);
    }
}