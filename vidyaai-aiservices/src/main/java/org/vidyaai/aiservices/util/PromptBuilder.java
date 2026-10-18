package org.vidyaai.aiservices.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.vidyaai.aiservices.entity.PromptTemplate;
import org.vidyaai.aiservices.exception.PromptTemplateException;
import org.vidyaai.aiservices.mapper.PromptTemplateMapper;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class PromptBuilder {

    private final PromptTemplateMapper promptTemplateMapper;
    private final JsonUtil jsonUtil;

    public String build(PromptTemplate template, Map<String, Object> variables) {
        if (template == null) {
            throw new PromptTemplateException("Prompt template is null");
        }
        try {
            String rendered = promptTemplateMapper.renderTemplate(template, variables);
            log.debug("Built prompt using template '{}' (v{})", template.getName(), template.getVersion());
            return rendered;
        } catch (Exception e) {
            log.error("Error building prompt from template '{}': {}", template.getName(), e.getMessage(), e);
            throw new PromptTemplateException("Failed to build prompt", e);
        }
    }

    public String buildWithJsonInstruction(PromptTemplate template, Map<String, Object> variables,
                                           String expectedSchema) {
        String rendered = build(template, variables);
        if (expectedSchema == null || expectedSchema.isBlank()) {
            return rendered;
        }
        return rendered + "\n\nRespond ONLY with valid JSON matching this schema:\n" + expectedSchema;
    }

    public String appendContext(String basePrompt, String context) {
        if (context == null || context.isBlank()) {
            return basePrompt;
        }
        return basePrompt + "\n\nContext:\n" + context;
    }

    public String appendInstructions(String basePrompt, String instructions) {
        if (instructions == null || instructions.isBlank()) {
            return basePrompt;
        }
        return basePrompt + "\n\nAdditional instructions:\n" + instructions;
    }

    public String appendLanguageDirective(String basePrompt, String languageName) {
        if (languageName == null || languageName.isBlank()) {
            return basePrompt;
        }
        return basePrompt + "\n\nRespond in " + languageName + ".";
    }

    @SuppressWarnings("unused")
    private String safeStringify(Map<String, Object> variables) {
        return jsonUtil.toJson(variables);
    }
}