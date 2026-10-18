package org.vidyaai.aiservices.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Component
@Slf4j
public class HashUtil {

    private static final String HASH_ALGORITHM = "SHA-256";

    public String sha256(String input) {
        if (input == null) {
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            log.error("Hash algorithm {} not available: {}", HASH_ALGORITHM, e.getMessage(), e);
            throw new IllegalStateException("Failed to compute hash", e);
        }
    }

    public String contentHash(String content, String language, String type) {
        String composite = String.join("|",
                content != null ? content : "",
                language != null ? language : "",
                type != null ? type : "");
        return sha256(composite);
    }

    public String shortHash(String input) {
        String hash = sha256(input);
        return hash != null && hash.length() >= 16 ? hash.substring(0, 16) : hash;
    }
}