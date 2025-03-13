package com.AIApp.ollama.Service;

import com.AIApp.ollama.Dao.PdfEmbeddingRepository;
import com.AIApp.ollama.Entity.PdfEmbedding;
import com.AIApp.ollama.dto.SimilarDocumentDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Objects;

@Service
public class PdfEmbeddingService {

    private static final Logger log = LoggerFactory.getLogger(PdfEmbeddingService.class);
    @Autowired
    private PdfEmbeddingRepository repository;

    @Autowired
    private OllamaService ollamaService;

    public void processPdf(File pdfFile) throws Exception {
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

    public List<String> retrieveSimilarDoc(SimilarDocumentDTO similarDocumentDTO) throws Exception {
        try {
            float[] queryEmbedding = convertListToArray(ollamaService.generateEmbedding(similarDocumentDTO.getQuery()));
            return repository.findSimilarDocuments(queryEmbedding, Objects.isNull(similarDocumentDTO.getLimit()) ? 2 : similarDocumentDTO.getLimit());
        } catch (Exception e) {
            log.error("Error in query execution: ", e);
            throw e;
        }
    }

}
