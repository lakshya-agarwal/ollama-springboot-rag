package com.AIApp.ollama.Controller;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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

            return ResponseEntity.ok("PDF uploaded and processed successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error processing PDF: " + e.getMessage());
        }
    }
}