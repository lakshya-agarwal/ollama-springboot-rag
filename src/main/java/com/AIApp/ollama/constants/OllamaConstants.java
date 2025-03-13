package com.AIApp.ollama.constants;

public class OllamaConstants {

    private OllamaConstants() {
        throw new IllegalStateException("constants class");
    }

    public static final String OLLAMA_BASE_URL = "http://localhost:11434/api";
    public static final String RESOURCE_PATH_EMBEDDINGS = "/embeddings";
    public static final String RESOURCE_PATH_GENERATE = "/generate";
    public static final String SUMMARY_MODEL = "mistral";
}
