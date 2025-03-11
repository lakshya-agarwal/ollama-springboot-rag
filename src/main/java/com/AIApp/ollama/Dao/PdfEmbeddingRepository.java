package com.AIApp.ollama.Dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.AIApp.ollama.Entity.PdfEmbedding;

public interface PdfEmbeddingRepository extends CrudRepository<PdfEmbedding, Long> {

    @Query(value = "SELECT * FROM embeddings ORDER BY embedding <-> CAST(:queryVector AS vector) LIMIT 5", nativeQuery = true)
    List<PdfEmbedding> findRelevantEmbeddings(float[] queryVector);
}