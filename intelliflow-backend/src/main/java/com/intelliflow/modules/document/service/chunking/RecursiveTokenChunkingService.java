package com.intelliflow.modules.document.service.chunking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Recursive Token Sliding Window Implementation of ChunkingService.
 * 
 * Splits documents recursively along semantic boundaries (\n\n, \n, sentence endings, spaces)
 * maintaining configured token window size and sliding token overlap.
 */
@Slf4j
@Service
public class RecursiveTokenChunkingService implements ChunkingService {

    private static final String[] SEPARATORS = new String[]{"\n\n", "\n", ". ", " "};

    @Override
    public List<TextChunkResult> chunkText(String fullText, int chunkSizeTokens, int chunkOverlapTokens) {
        if (fullText == null || fullText.isBlank()) {
            return List.of();
        }

        int targetChunkSize = Math.max(chunkSizeTokens, 50);
        int targetOverlap = Math.max(0, Math.min(chunkOverlapTokens, targetChunkSize / 2));

        List<String> rawChunks = recursiveSplit(fullText, SEPARATORS, 0, targetChunkSize);
        List<String> overlappedChunks = applySlidingWindowOverlap(rawChunks, targetChunkSize, targetOverlap);

        List<TextChunkResult> results = new ArrayList<>();
        for (int i = 0; i < overlappedChunks.size(); i++) {
            String content = overlappedChunks.get(i);
            int tokenCount = estimateTokenCount(content);
            results.add(new TextChunkResult(i, content, tokenCount));
        }

        log.info("Successfully chunked text payload (Length: {} chars) into {} chunks", fullText.length(), results.size());
        return results;
    }

    private List<String> recursiveSplit(String text, String[] separators, int separatorIndex, int targetChunkSize) {
        List<String> finalChunks = new ArrayList<>();
        if (text.isBlank()) {
            return finalChunks;
        }

        if (estimateTokenCount(text) <= targetChunkSize || separatorIndex >= separators.length) {
            finalChunks.add(text.trim());
            return finalChunks;
        }

        String separator = separators[separatorIndex];
        String[] splits = text.split(java.util.regex.Pattern.quote(separator));

        StringBuilder currentBuffer = new StringBuilder();
        for (String split : splits) {
            if (split.isBlank()) {
                continue;
            }

            String candidate = currentBuffer.isEmpty() ? split : currentBuffer + separator + split;
            if (estimateTokenCount(candidate) <= targetChunkSize) {
                currentBuffer = new StringBuilder(candidate);
            } else {
                if (!currentBuffer.isEmpty()) {
                    finalChunks.add(currentBuffer.toString().trim());
                    currentBuffer = new StringBuilder();
                }
                if (estimateTokenCount(split) > targetChunkSize) {
                    finalChunks.addAll(recursiveSplit(split, separators, separatorIndex + 1, targetChunkSize));
                } else {
                    currentBuffer.append(split);
                }
            }
        }

        if (!currentBuffer.isEmpty()) {
            finalChunks.add(currentBuffer.toString().trim());
        }

        return finalChunks;
    }

    private List<String> applySlidingWindowOverlap(List<String> chunks, int maxTokens, int overlapTokens) {
        if (chunks.size() <= 1 || overlapTokens <= 0) {
            return chunks;
        }

        List<String> result = new ArrayList<>();
        for (int i = 0; i < chunks.size(); i++) {
            String current = chunks.get(i);
            if (i > 0) {
                String previous = chunks.get(i - 1);
                String overlapPrefix = extractTrailingTokens(previous, overlapTokens);
                if (!overlapPrefix.isBlank()) {
                    current = overlapPrefix + " " + current;
                }
            }
            result.add(current);
        }
        return result;
    }

    private String extractTrailingTokens(String text, int maxTokens) {
        String[] words = text.split("\\s+");
        if (words.length <= maxTokens) {
            return text;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = words.length - maxTokens; i < words.length; i++) {
            builder.append(words[i]).append(" ");
        }
        return builder.toString().trim();
    }

    private int estimateTokenCount(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }
        // standard approximation: 1 token ~ 4 characters
        return Math.max(1, text.length() / 4);
    }
}
