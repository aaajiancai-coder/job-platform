package com.job.ai;

public enum ChatType {
    CHAT("chat"),
    SYSTEM("system"),
    USER("user"),
    ASSISTANT("assistant");

    private String value;
    ChatType(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
