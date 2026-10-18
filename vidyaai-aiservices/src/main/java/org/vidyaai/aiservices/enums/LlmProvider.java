package org.vidyaai.aiservices.enums;

public enum LlmProvider {
    GROQ("groq"),
    OPENAI("openai"),
    GOOGLE_GENAI("google-genai"),
    DEEPSEEK("deepseek"),
    OLLAMA("ollama");

    private final String code;

    LlmProvider(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static LlmProvider fromCode(String code) {
        for (LlmProvider p : values()) {
            if (p.code.equalsIgnoreCase(code)) {
                return p;
            }
        }
        throw new IllegalArgumentException("Unknown LLM provider: " + code);
    }
}