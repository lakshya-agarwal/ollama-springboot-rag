package com.AIApp.ollama.Service;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;

import com.AIApp.ollama.Dao.PdfEmbeddingRepository;

public class PdfEmbeddingService {
	
	 @Autowired
	    private PdfEmbeddingRepository repository;
	 
	 public void processPdf(File pdfFile) throws IOException{
		 
	 }

}
