package org.vidyaai.aiservices.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vidyaai.aiservices.entity.VideoEmbedding;
import org.vidyaai.aiservices.exception.VectorStoreException;
import org.vidyaai.aiservices.repository.VideoEmbeddingRepository;
import org.vidyaai.aiservices.service.LlmProviderService;
import org.vidyaai.aiservices.service.VectorStoreService;
import org.vidyaai.aiservices.util.TextChunker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class VectorStoreServiceImpl implements VectorStoreService {

    private final VideoEmbeddingRepository videoEmbeddingRepository;
    private final TextChunker textChunker;
    private final LlmProviderService llmProviderService;

    @Override
    @Async("aiTaskExecutor")
    @Transactional
    public void storeEmbeddings(UUID videoId, String content) {
        log.info("Starting embedding storage for videoId={}", videoId);
        try {
            if (content == null || content.isBlank()) {
                log.warn("Content is blank, skipping embedding for videoId={}", videoId);
                return;
            }

            videoEmbeddingRepository.deleteByVideoId(videoId);

            List<String> chunks = textChunker.chunk(content);
            if (chunks.isEmpty()) {
                log.warn("No chunks generated for videoId={}", videoId);
                return;
            }

            List<VideoEmbedding> batch = new ArrayList<>(100);
            int index = 0;

            for (String chunk : chunks) {
                try {
                    float[] embedding = llmProviderService.embed(chunk);
                    String vectorLiteral = toVectorLiteral(embedding);

                    VideoEmbedding entity = VideoEmbedding.builder()
                            .videoId(videoId)
                            .contentChunk(chunk)
                            .embedding(vectorLiteral)
                            .chunkIndex(index++)
                            .chunkType("BODY")
                            .build();

                    batch.add(entity);

                    if (batch.size() >= 100) {
                        videoEmbeddingRepository.saveAll(batch);
                        batch.clear();
                    }
                } catch (Exception e) {
                    log.warn("Skipping chunk {} for videoId={}: {}", index, videoId, e.getMessage());
                }
            }

            if (!batch.isEmpty()) {
                videoEmbeddingRepository.saveAll(batch);
            }

            log.info("Stored {} embeddings for videoId={}", index, videoId);
        } catch (Exception e) {
            log.error("Failed to store embeddings for videoId {}: {}", videoId, e.getMessage(), e);
            throw new VectorStoreException("Failed to store embeddings for video: " + videoId, e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> findSimilarChunks(UUID videoId, String query, int topK) {
        log.debug("Finding top {} similar chunks for videoId={}", topK, videoId);
        try {
            if (query == null || query.isBlank()) {
                return List.of();
            }

            float[] queryEmbedding = llmProviderService.embed(query);
            String vectorLiteral = toVectorLiteral(queryEmbedding);

            List<VideoEmbedding> matches = videoEmbeddingRepository.findTopKSimilar(
                    videoId, vectorLiteral, topK);

            return matches.stream()
                    .map(VideoEmbedding::getContentChunk)
                    .toList();
        } catch (Exception e) {
            log.error("Similarity search failed for videoId {}: {}", videoId, e.getMessage(), e);
            throw new VectorStoreException("Similarity search failed", e);
        }
    }

    @Override
    @Transactional
    public void deleteEmbeddings(UUID videoId) {
        try {
            int deleted = videoEmbeddingRepository.deleteByVideoId(videoId);
            log.info("Deleted {} embeddings for videoId={}", deleted, videoId);
        } catch (Exception e) {
            log.error("Failed to delete embeddings for videoId {}: {}", videoId, e.getMessage(), e);
            throw new VectorStoreException("Failed to delete embeddings", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long countEmbeddings(UUID videoId) {
        try {
            return videoEmbeddingRepository.countByVideoId(videoId);
        } catch (Exception e) {
            log.error("Failed to count embeddings for videoId {}: {}", videoId, e.getMessage(), e);
            return 0L;
        }
    }

    private String toVectorLiteral(float[] embedding) {
        if (embedding == null) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < embedding.length; i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(embedding[i]);
        }
        sb.append(']');
        return sb.toString();
    }

    @SuppressWarnings("unused")
    private String debugVector(float[] embedding) {
        return Arrays.toString(embedding);
    }
}