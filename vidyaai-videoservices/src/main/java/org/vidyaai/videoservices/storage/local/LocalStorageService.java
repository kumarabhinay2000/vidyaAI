package org.vidyaai.videoservices.storage.local;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.vidyaai.videoservices.config.StorageConfig;
import org.vidyaai.videoservices.exception.StorageException;
import org.vidyaai.videoservices.storage.StorageService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@ConditionalOnProperty(name = "storage.minio.enabled", havingValue = "false")
@Slf4j
public class LocalStorageService implements StorageService {

    private final StorageConfig storageConfig;
    private final Path basePath;

    public LocalStorageService(StorageConfig storageConfig) {
        this.storageConfig = storageConfig;
        this.basePath = Paths.get(storageConfig.getLocal().getBasePath());
        try {
            Files.createDirectories(basePath);
            log.info("Local storage initialized at {}", basePath);
        } catch (IOException e) {
            log.error("Failed initializing local storage: {}", e.getMessage(), e);
        }
    }

    @Override
    public String uploadVideo(UUID videoId, String filename, String contentType,
                              InputStream stream, long size) {
        String objectKey = buildObjectKey(videoId, "video", filename);
        return writeFile(objectKey, stream);
    }

    @Override
    public String uploadThumbnail(UUID videoId, String filename, InputStream stream,
                                  long size, String contentType) {
        String objectKey = buildObjectKey(videoId, "thumbnail", filename);
        return writeFile(objectKey, stream);
    }

    @Override
    public InputStream download(String objectKey) {
        try {
            Path target = basePath.resolve(objectKey);
            return Files.newInputStream(target);
        } catch (IOException e) {
            log.error("Failed downloading local file {}: {}", objectKey, e.getMessage(), e);
            throw new StorageException("Failed to read local object: " + objectKey, e);
        }
    }

    @Override
    public void delete(String objectKey) {
        try {
            Path target = basePath.resolve(objectKey);
            Files.deleteIfExists(target);
            log.info("Deleted local object: {}", objectKey);
        } catch (IOException e) {
            log.error("Failed deleting {}: {}", objectKey, e.getMessage(), e);
        }
    }

    @Override
    public void deleteByPrefix(String prefix) {
        Path target = basePath.resolve(prefix);
        if (!Files.exists(target)) {
            return;
        }
        try (Stream<Path> paths = Files.walk(target)) {
            paths.sorted((a, b) -> b.compareTo(a)).forEach(p -> {
                try {
                    Files.deleteIfExists(p);
                } catch (IOException e) {
                    log.warn("Failed deleting {}: {}", p, e.getMessage());
                }
            });
        } catch (IOException e) {
            log.error("Failed deleting prefix {}: {}", prefix, e.getMessage(), e);
        }
    }

    @Override
    public boolean exists(String objectKey) {
        return Files.exists(basePath.resolve(objectKey));
    }

    @Override
    public String getPresignedUrl(String objectKey, int expirySeconds) {
        return getPublicUrl(objectKey);
    }

    @Override
    public String getPublicUrl(String objectKey) {
        if (!StringUtils.hasText(objectKey)) {
            return null;
        }
        return storageConfig.getLocal().getPublicBaseUrl() + "/" + objectKey;
    }

    @Override
    public String getProviderName() {
        return "LOCAL";
    }

    private String writeFile(String objectKey, InputStream stream) {
        try {
            Path target = basePath.resolve(objectKey);
            Files.createDirectories(target.getParent());
            Files.copy(stream, target, StandardCopyOption.REPLACE_EXISTING);
            log.info("Wrote local file: {}", target);
            return objectKey;
        } catch (IOException e) {
            log.error("Failed writing local file {}: {}", objectKey, e.getMessage(), e);
            throw new StorageException("Failed to write local object: " + objectKey, e);
        }
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
}