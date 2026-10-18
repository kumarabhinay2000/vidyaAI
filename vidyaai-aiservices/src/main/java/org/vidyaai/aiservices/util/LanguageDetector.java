package org.vidyaai.aiservices.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.vidyaai.aiservices.enums.Language;

@Component
@Slf4j
public class LanguageDetector {

    public Language detect(String text) {
        if (text == null || text.isBlank()) {
            return Language.ENGLISH;
        }
        try {
            boolean hasDevanagari = text.chars().anyMatch(c -> c >= 0x0900 && c <= 0x097F);
            if (hasDevanagari) {
                return Language.HINDI;
            }
            boolean hasTamil = text.chars().anyMatch(c -> c >= 0x0B80 && c <= 0x0BFF);
            if (hasTamil) {
                return Language.TAMIL;
            }
            boolean hasTelugu = text.chars().anyMatch(c -> c >= 0x0C00 && c <= 0x0C7F);
            if (hasTelugu) {
                return Language.TELUGU;
            }
            boolean hasBengali = text.chars().anyMatch(c -> c >= 0x0980 && c <= 0x09FF);
            if (hasBengali) {
                return Language.BENGALI;
            }
            return Language.ENGLISH;
        } catch (Exception e) {
            log.warn("Error detecting language: {}", e.getMessage());
            return Language.ENGLISH;
        }
    }

    public String getLanguageName(Language language) {
        if (language == null) {
            return "English";
        }
        return switch (language) {
            case ENGLISH -> "English";
            case HINDI -> "Hindi";
            case TAMIL -> "Tamil";
            case TELUGU -> "Telugu";
            case BENGALI -> "Bengali";
            case MARATHI -> "Marathi";
        };
    }
}