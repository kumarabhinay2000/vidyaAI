package org.vidyaai.aiservices.service;

import org.vidyaai.aiservices.entity.PromptTemplate;
import org.vidyaai.aiservices.enums.PromptCategory;

import java.util.List;
import java.util.Map;

public interface PromptTemplateService {

    PromptTemplate getActiveTemplate(String name);

    PromptTemplate getTemplateOrNull(String name);

    String render(String name, Map<String, Object> variables);

    String renderWithSchema(String name, Map<String, Object> variables, String expectedSchema);

    List<PromptTemplate> listActive();

    List<PromptTemplate> listByCategory(PromptCategory category);

    PromptTemplate save(PromptTemplate template);

    void deactivate(String name);

    void updatePerformance(String name, Double score, Double successRate);
}