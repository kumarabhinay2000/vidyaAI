package org.vidyaai.aiservices.mapper;

import org.vidyaai.aiservices.entity.PromptTemplate;

import java.util.Map;

public interface PromptTemplateMapper {

    String renderTemplate(PromptTemplate template, Map<String, Object> variables);
}