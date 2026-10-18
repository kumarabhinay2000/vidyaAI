package org.vidyaai.videoservices.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Locale;

@Component
@Slf4j
public class FileUtil {

    public String sanitizeFilename(String filename) {
        if (!StringUtils.hasText(filename)) {
            return "video";
        }
        String sanitized = filename.replaceAll("[^a-zA-Z0-9._-]", "_");
        if (sanitized.length() > 200) {
            sanitized = sanitized.substring(0, 200);
        }
        return sanitized;
    }

    public String getExtension(String filename) {
        if (!StringUtils.hasText(filename)) {
            return "";
        }
        int dot = filename.lastIndexOf('.');
        return dot > 0 ? filename.substring(dot + 1).toLowerCase(Locale.ROOT) : "";
    }

    public boolean isVideoMimeType(String mimeType) {
        return StringUtils.hasText(mimeType) && mimeType.startsWith("video/");
    }

    public String formatSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        char unit = "KMGTPE".charAt(exp - 1);
        return String.format(Locale.ROOT, "%.1f %cB", bytes / Math.pow(1024, exp), unit);
    }

    public String formatDuration(int seconds) {
        int h = seconds / 3600;
        int m = (seconds % 3600) / 60;
        int s = seconds % 60;
        return h > 0
                ? String.format("%d:%02d:%02d", h, m, s)
                : String.format("%d:%02d", m, s);
    }
}