package com.AIApp.ollama.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.AIApp.ollama.Dao.PdfEmbeddingRepository;
import com.AIApp.ollama.Entity.PdfEmbedding;

@Service
public class PdfEmbeddingService {
	
	@Autowired
    private PdfEmbeddingRepository repository;

    @Autowired
    private OllamaEmbeddingService ollamaService;

    public void processPdf(File pdfFile) throws IOException, Exception {
        PdfExtractorService extractorService = new PdfExtractorService();
        String text = extractorService.extractText(pdfFile);

        List<Float> vector = ollamaService.generateEmbedding(text);
        float[] embeddingArray = convertListToArray(vector);

        PdfEmbedding embedding = new PdfEmbedding();
        embedding.setFilename(pdfFile.getName());
        embedding.setEmbedding(embeddingArray);
        repository.save(embedding);
    }
    
    public static float[] convertListToArray(List<Float> list) {
        float[] array = new float[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i); // Unboxing Float to float
        }
        return array;
    }

}
