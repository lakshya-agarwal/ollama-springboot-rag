package com.AIApp.ollama.Dao;

import com.AIApp.ollama.Entity.PdfEmbedding;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PdfEmbeddingRepository extends CrudRepository<PdfEmbedding, Long> {

    @Query(value = "SELECT filename FROM embeddings ORDER BY embedding <-> CAST(:queryEmbedding AS vector) ASC LIMIT :retrieveLimit", nativeQuery = true)
    List<String> findSimilarDocuments(@Param("queryEmbedding") float[] queryEmbedding, @Param("retrieveLimit") Integer retrieveLimit);
}