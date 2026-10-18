package org.vidyaai.aiservices.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.vidyaai.aiservices.entity.PromptTemplate;
import org.vidyaai.aiservices.enums.PromptCategory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PromptTemplateRepository extends JpaRepository<PromptTemplate, UUID> {

    Optional<PromptTemplate> findByNameAndIsActiveTrue(String name);

    Optional<PromptTemplate> findFirstByNameOrderByVersionDesc(String name);

    List<PromptTemplate> findByIsActiveTrue();

    List<PromptTemplate> findByCategoryAndIsActiveTrue(PromptCategory category);

    boolean existsByNameAndVersion(String name, Integer version);

    @Modifying
    @Transactional
    @Query("UPDATE PromptTemplate p SET p.isActive = false WHERE p.name = :name")
    int deactivateAllVersionsOfName(@Param("name") String name);

    @Modifying
    @Transactional
    @Query("UPDATE PromptTemplate p SET p.performanceScore = :score, p.successRate = :rate " +
            "WHERE p.id = :id")
    int updatePerformanceMetrics(
            @Param("id") UUID id,
            @Param("score") Double score,
            @Param("rate") Double rate);

    @Query("SELECT p FROM PromptTemplate p WHERE p.isActive = true " +
            "ORDER BY p.performanceScore DESC NULLS LAST")
    List<PromptTemplate> findActiveByPerformance();
}