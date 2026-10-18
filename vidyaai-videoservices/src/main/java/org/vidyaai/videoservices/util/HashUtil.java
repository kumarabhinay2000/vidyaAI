package org.vidyaai.videoservices.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

@Component
@Slf4j
public class HashUtil {

    public String sha256(String input) {
        if (input == null) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            log.error("Failed computing hash: {}", e.getMessage(), e);
            return null;
        }
    }

    public String shortHash(String input) {
        String h = sha256(input);
        return h != null && h.length() >= 16 ? h.substring(0, 16) : h;
    }
}