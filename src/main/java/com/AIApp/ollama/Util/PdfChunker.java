package com.AIApp.ollama.Util;

import java.util.ArrayList;
import java.util.List;

public class PdfChunker {
	private static final int TOKEN_LIMIT = 300; // Adjust as needed

    public static List<String> chunkText(String text) {
        String[] sentences = text.split("(?<=[.!?])\\s+"); // Splitting at sentence boundaries
        List<String> chunks = new ArrayList<>();
        StringBuilder currentChunk = new StringBuilder();
        int currentTokenCount = 0;

        for (String sentence : sentences) {
            int tokenCount = countTokens(sentence);

            if (currentTokenCount + tokenCount > TOKEN_LIMIT) {
                chunks.add(currentChunk.toString().trim());
                currentChunk = new StringBuilder();
                currentTokenCount = 0;
            }

            currentChunk.append(sentence).append(" ");
            currentTokenCount += tokenCount;
        }

        if (!currentChunk.isEmpty()) {
            chunks.add(currentChunk.toString().trim());
        }

        return chunks;
    }

    private static int countTokens(String text) {
        return text.split("\\s+").length; // Count words based on spaces
    }

}
