package com.AIApp.ollama.Dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.AIApp.ollama.Entity.PdfEmbedding;

public interface PdfEmbeddingRepository extends JpaRepository<PdfEmbedding, Long> {
}