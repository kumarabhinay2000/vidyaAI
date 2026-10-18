package org.vidyaai.aiservices.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.vidyaai.aiservices.config.AiConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TextChunker {

    private final AiConfig aiConfig;

    public List<String> chunk(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        int chunkSize = aiConfig.getProcessing().getChunkSize();
        int overlap = aiConfig.getProcessing().getChunkOverlap();

        if (text.length() <= chunkSize) {
            return List.of(text);
        }

        List<String> chunks = new ArrayList<>();
        int start = 0;
        int textLength = text.length();

        while (start < textLength) {
            int end = Math.min(start + chunkSize, textLength);
            if (end < textLength) {
                int lastPeriod = text.lastIndexOf(". ", end);
                if (lastPeriod > start + (chunkSize / 2)) {
                    end = lastPeriod + 1;
                }
            }
            chunks.add(text.substring(start, end).trim());
            start = end - overlap;
            if (start < 0) {
                start = 0;
            }
            if (end == textLength) {
                break;
            }
        }

        log.debug("Chunked text of {} chars into {} chunks", textLength, chunks.size());
        return chunks;
    }

    public List<String> chunkWithOverlap(String text, int customSize, int customOverlap) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        List<String> chunks = new ArrayList<>();
        int start = 0;
        int length = text.length();

        while (start < length) {
            int end = Math.min(start + customSize, length);
            chunks.add(text.substring(start, end).trim());
            start = end - customOverlap;
            if (end == length) {
                break;
            }
        }
        return chunks;
    }
}