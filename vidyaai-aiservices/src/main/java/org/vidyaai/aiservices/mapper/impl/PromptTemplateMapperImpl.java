package org.vidyaai.aiservices.mapper.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.vidyaai.aiservices.entity.PromptTemplate;
import org.vidyaai.aiservices.exception.PromptTemplateException;
import org.vidyaai.aiservices.mapper.PromptTemplateMapper;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class PromptTemplateMapperImpl implements PromptTemplateMapper {

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{(\\w+)}");

    @Override
    public String renderTemplate(PromptTemplate template, Map<String, Object> variables) {
        if (template == null || template.getTemplate() == null) {
            throw new PromptTemplateException("Prompt template or its body is null");
        }
        try {
            String rendered = template.getTemplate();
            Matcher matcher = VARIABLE_PATTERN.matcher(rendered);

            StringBuilder sb = new StringBuilder();
            while (matcher.find()) {
                String varName = matcher.group(1);
                Object value = variables != null ? variables.get(varName) : null;
                String replacement = value != null ? value.toString() : "";
                matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
            }
            matcher.appendTail(sb);

            return sb.toString();
        } catch (Exception e) {
            log.error("Error rendering prompt template {}: {}", template.getName(), e.getMessage(), e);
            throw new PromptTemplateException("Failed to render prompt template: " + template.getName(), e);
        }
    }
}