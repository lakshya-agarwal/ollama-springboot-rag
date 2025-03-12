package com.AIApp.ollama.Controller;

import java.io.File;
import java.util.List;

import com.AIApp.ollama.dto.SimilarDocumentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.AIApp.ollama.Service.PdfEmbeddingService;


@RestController
@RequestMapping("/api/pdf")
public class PdfController {

    @Autowired
    private PdfEmbeddingService pdfEmbeddingService;

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
}