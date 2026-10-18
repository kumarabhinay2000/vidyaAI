package org.vidyaai.aiservices.strategy.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.vidyaai.aiservices.constant.PromptNames;
import org.vidyaai.aiservices.enums.SummaryType;
import org.vidyaai.aiservices.strategy.SummaryStrategy;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class TenPointSummaryStrategy implements SummaryStrategy {

    private static final String SCHEMA = """
            {
              "title": "string",
              "points": ["string"],
              "keywords": ["string"]
            }
            """;

    @Override
    public SummaryType getSupportedType() {
        return SummaryType.TEN_POINT;
    }

    @Override
    public String getPromptName() {
        return PromptNames.SUMMARY_TEN_POINT;
    }

    @Override
    public Map<String, Object> buildVariables(String content, String languageName) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("content", content);
        vars.put("language", languageName);
        vars.put("pointCount", 10);
        return vars;
    }

    @Override
    public String getExpectedSchema() {
        return SCHEMA;
    }
}