package com.sapms.pattern.factory;

public interface AiPrompt {
    String buildSystemPrompt();

    String buildUserPrompt(String data);
}
