package org.vidyaai.aiservices.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.vidyaai.aiservices.entity.LlmUsageLog;
import org.vidyaai.aiservices.enums.LlmProvider;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface LlmUsageLogRepository extends JpaRepository<LlmUsageLog, UUID> {

    List<LlmUsageLog> findByLlmProviderAndCreatedAtBetween(
            LlmProvider provider, LocalDateTime from, LocalDateTime to);

    List<LlmUsageLog> findBySuccessFalseAndCreatedAtAfter(LocalDateTime since);

    long countByCacheHitTrueAndCreatedAtAfter(LocalDateTime since);

    long countBySuccessFalseAndCreatedAtAfter(LocalDateTime since);

    @Query("SELECT SUM(l.totalTokens) FROM LlmUsageLog l " +
            "WHERE l.createdAt >= :since AND l.success = true")
    Long sumTotalTokensSince(@Param("since") LocalDateTime since);

    @Query("SELECT SUM(l.cost) FROM LlmUsageLog l " +
            "WHERE l.createdAt >= :since AND l.success = true")
    BigDecimal sumCostSince(@Param("since") LocalDateTime since);

    @Query("SELECT AVG(l.responseTimeMs) FROM LlmUsageLog l " +
            "WHERE l.success = true AND l.createdAt >= :since")
    Double avgResponseTimeSince(@Param("since") LocalDateTime since);

    @Query("SELECT l.llmProvider, COUNT(l) FROM LlmUsageLog l " +
            "WHERE l.createdAt >= :since GROUP BY l.llmProvider")
    List<Object[]> countByProviderSince(@Param("since") LocalDateTime since);

    @Modifying
    @Transactional
    @Query("DELETE FROM LlmUsageLog l WHERE l.createdAt < :cutoff")
    int deleteOlderThan(@Param("cutoff") LocalDateTime cutoff);
}