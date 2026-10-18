package org.vidyaai.aiservices.factory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.vidyaai.aiservices.entity.PromptTemplate;
import org.vidyaai.aiservices.exception.PromptTemplateException;
import org.vidyaai.aiservices.repository.PromptTemplateRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class PromptTemplateFactory {

    private final PromptTemplateRepository promptTemplateRepository;

    public PromptTemplate getActiveTemplate(String name) {
        try {
            return promptTemplateRepository.findByNameAndIsActiveTrue(name)
                    .orElseThrow(() -> new PromptTemplateException(
                            "No active prompt template found: " + name));
        } catch (PromptTemplateException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error fetching prompt template '{}': {}", name, e.getMessage(), e);
            throw new PromptTemplateException("Failed to load prompt template: " + name, e);
        }
    }

    public PromptTemplate getTemplateOrNull(String name) {
        try {
            return promptTemplateRepository.findByNameAndIsActiveTrue(name).orElse(null);
        } catch (Exception e) {
            log.warn("Failed to fetch prompt template '{}': {}", name, e.getMessage());
            return null;
        }
    }
}