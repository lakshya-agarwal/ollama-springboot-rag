package com.AIApp.ollama.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class PdfSummaryService {

    private final PdfExtractorService pdfExtractorService;
    private final OllamaService ollamaService;

    @Autowired
    public PdfSummaryService(PdfExtractorService pdfExtractorService, OllamaService ollamaService) {
        this.pdfExtractorService = pdfExtractorService;
        this.ollamaService = ollamaService;
    }

    public String summarize(MultipartFile file, String length) throws Exception {
        try {
            String pdfText = pdfExtractorService.extractText(file);
            String prompt = switch (length) {
                case "small" -> "Summarize this in a short sentence: ";
                case "medium" -> "Provide a brief summary: ";
                case "large" -> "Provide a detailed summary: ";
                default -> "Summarize this: ";
            };
            return ollamaService.generateSummary(prompt + pdfText);
        } catch (Exception e) {
            log.error("Error in Summarizing PDF", e);
            throw e;
        }
    }
}
