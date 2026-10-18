package org.vidyaai.aiservices.enums;

public enum Language {
    ENGLISH("en"),
    HINDI("hi"),
    TAMIL("ta"),
    TELUGU("te"),
    BENGALI("bn"),
    MARATHI("mr");

    private final String code;

    Language(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}