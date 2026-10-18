package org.vidyaai.videoservices.storage;

import java.io.InputStream;
import java.util.UUID;

public interface StorageService {

    String uploadVideo(UUID videoId, String filename, String contentType, InputStream stream, long size);

    String uploadThumbnail(UUID videoId, String filename, InputStream stream, long size, String contentType);

    InputStream download(String objectKey);

    void delete(String objectKey);

    void deleteByPrefix(String prefix);

    boolean exists(String objectKey);

    String getPresignedUrl(String objectKey, int expirySeconds);

    String getPublicUrl(String objectKey);

    String getProviderName();
}