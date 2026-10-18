package org.vidyaai.aiservices.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vidyaai.aiservices.entity.PromptTemplate;
import org.vidyaai.aiservices.enums.PromptCategory;
import org.vidyaai.aiservices.exception.PromptTemplateException;
import org.vidyaai.aiservices.factory.PromptTemplateFactory;
import org.vidyaai.aiservices.repository.PromptTemplateRepository;
import org.vidyaai.aiservices.service.PromptTemplateService;
import org.vidyaai.aiservices.util.PromptBuilder;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromptTemplateServiceImpl implements PromptTemplateService {

    private final PromptTemplateRepository promptTemplateRepository;
    private final PromptTemplateFactory promptTemplateFactory;
    private final PromptBuilder promptBuilder;

    @Override
    @Transactional(readOnly = true)
    public PromptTemplate getActiveTemplate(String name) {
        try {
            return promptTemplateFactory.getActiveTemplate(name);
        } catch (PromptTemplateException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error fetching active prompt template '{}': {}", name, e.getMessage(), e);
            throw new PromptTemplateException("Failed to load prompt template: " + name, e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PromptTemplate getTemplateOrNull(String name) {
        try {
            return promptTemplateFactory.getTemplateOrNull(name);
        } catch (Exception e) {
            log.warn("Error fetching template '{}': {}", name, e.getMessage());
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public String render(String name, Map<String, Object> variables) {
        PromptTemplate template = getActiveTemplate(name);
        return promptBuilder.build(template, variables);
    }

    @Override
    @Transactional(readOnly = true)
    public String renderWithSchema(String name, Map<String, Object> variables, String expectedSchema) {
        PromptTemplate template = getActiveTemplate(name);
        return promptBuilder.buildWithJsonInstruction(template, variables, expectedSchema);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromptTemplate> listActive() {
        try {
            return promptTemplateRepository.findByIsActiveTrue();
        } catch (Exception e) {
            log.error("Error listing active prompt templates: {}", e.getMessage(), e);
            throw new PromptTemplateException("Failed to list prompt templates", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromptTemplate> listByCategory(PromptCategory category) {
        try {
            return promptTemplateRepository.findByCategoryAndIsActiveTrue(category);
        } catch (Exception e) {
            log.error("Error listing prompt templates by category {}: {}", category, e.getMessage(), e);
            throw new PromptTemplateException("Failed to list prompt templates by category", e);
        }
    }

    @Override
    @Transactional
    public PromptTemplate save(PromptTemplate template) {
        try {
            if (template == null) {
                throw new PromptTemplateException("Prompt template cannot be null");
            }
            PromptTemplate saved = promptTemplateRepository.save(template);
            log.info("Prompt template saved: {} (v{})", saved.getName(), saved.getVersion());
            return saved;
        } catch (PromptTemplateException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error saving prompt template: {}", e.getMessage(), e);
            throw new PromptTemplateException("Failed to save prompt template", e);
        }
    }

    @Override
    @Transactional
    public void deactivate(String name) {
        try {
            int updated = promptTemplateRepository.deactivateAllVersionsOfName(name);
            log.info("Deactivated {} prompt template versions for name '{}'", updated, name);
        } catch (Exception e) {
            log.error("Error deactivating prompt template '{}': {}", name, e.getMessage(), e);
            throw new PromptTemplateException("Failed to deactivate prompt template", e);
        }
    }

    @Override
    @Transactional
    public void updatePerformance(String name, Double score, Double successRate) {
        try {
            PromptTemplate template = getActiveTemplate(name);
            UUID id = template.getId();
            promptTemplateRepository.updatePerformanceMetrics(id, score, successRate);
            log.info("Updated prompt performance metrics for '{}': score={}, rate={}",
                    name, score, successRate);
        } catch (Exception e) {
            log.error("Error updating prompt performance for '{}': {}", name, e.getMessage(), e);
            throw new PromptTemplateException("Failed to update prompt performance", e);
        }
    }
}