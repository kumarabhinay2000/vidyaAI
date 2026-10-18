package org.vidyaai.aiservices.service;

import java.util.List;
import java.util.UUID;

public interface VectorStoreService {

    void storeEmbeddings(UUID videoId, String content);

    List<String> findSimilarChunks(UUID videoId, String query, int topK);

    void deleteEmbeddings(UUID videoId);

    long countEmbeddings(UUID videoId);
}