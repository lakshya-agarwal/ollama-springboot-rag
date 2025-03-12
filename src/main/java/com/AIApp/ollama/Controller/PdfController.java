package com.AIApp.ollama.Controller;

import com.AIApp.ollama.Service.PdfEmbeddingService;
import com.AIApp.ollama.Service.PdfSummaryService;
import com.AIApp.ollama.dto.SimilarDocumentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;


@RestController
@RequestMapping("/api/pdf")
public class PdfController {

    private final PdfEmbeddingService pdfEmbeddingService;
    private final PdfSummaryService pdfSummaryService;

    @Autowired
    public PdfController(PdfEmbeddingService pdfEmbeddingService, PdfSummaryService pdfSummaryService) {
        this.pdfEmbeddingService = pdfEmbeddingService;
        this.pdfSummaryService = pdfSummaryService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadPdf(@RequestParam("file") MultipartFile file) {
        try {
            // Save file locally
            File pdfFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
            file.transferTo(pdfFile);

            // Process the PDF
            pdfEmbeddingService.processPdf(pdfFile);

            return ResponseEntity.ok("PDF uploaded and processed successfully: " + file.getName());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error processing PDF: " + e.getMessage());
        }
    }

    @GetMapping("/get-similar-documents")
    public ResponseEntity<Object> retrieveSimilarDocument(@RequestBody SimilarDocumentDTO similarDocumentDTO) {
        try {
            List<String> res = pdfEmbeddingService.retrieveSimilarDoc(similarDocumentDTO);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @GetMapping("/summarize")
    public ResponseEntity<Object> summarizePdf(@RequestParam("file") MultipartFile file, @RequestParam("length") String length) {
        try {
            return ResponseEntity.ok(pdfSummaryService.summarize(file, length));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}