package com.AIApp.ollama.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimilarDocumentDTO {
    String query;
    Integer limit;
}
