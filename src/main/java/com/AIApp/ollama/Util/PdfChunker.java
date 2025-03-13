package com.AIApp.ollama.Util;

import java.util.ArrayList;
import java.util.List;

public class PdfChunker {

    private static final int MAX_TOKENS = 300; // Adjust based on LLM limits

    public static List<String> chunkText(String text) {
        List<String> chunks = new ArrayList<>();
        String[] paragraphs = text.split("\n\n"); // Split by paragraphs

        StringBuilder currentChunk = new StringBuilder();
        for (String paragraph : paragraphs) {
            paragraph = paragraph.trim();
            
            // If paragraph is a heading, start a new chunk
            if (isHeading(paragraph) || currentChunk.length() + paragraph.length() > MAX_TOKENS) {
                if (currentChunk.length() > 0) {
                    chunks.add(currentChunk.toString());
                }
                currentChunk = new StringBuilder(paragraph);
            } else {
                currentChunk.append("\n\n").append(paragraph);
            }
        }

        // Add last chunk
        if (currentChunk.length() > 0) {
            chunks.add(currentChunk.toString());
        }

        return chunks;
    }

    // Detect headings like "1. Introduction", "## Title", "CHAPTER 3"
    private static boolean isHeading(String paragraph) {
        return paragraph.matches("(?i)^(\\d+\\.\\s+|##\\s+|chapter\\s+\\d+).*");
    }

}
