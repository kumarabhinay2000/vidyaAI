package org.vidyaai.aiservices.strategy;

import org.vidyaai.aiservices.enums.SummaryType;

import java.util.Map;

public interface SummaryStrategy {

    SummaryType getSupportedType();

    String getPromptName();

    Map<String, Object> buildVariables(String content, String languageName);

    String getExpectedSchema();
}