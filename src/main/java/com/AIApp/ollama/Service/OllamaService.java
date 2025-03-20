package com.AIApp.ollama.Service;

import static com.AIApp.ollama.constants.OllamaConstants.OLLAMA_BASE_URL;
import static com.AIApp.ollama.constants.OllamaConstants.RESOURCE_PATH_EMBEDDINGS;
import static com.AIApp.ollama.constants.OllamaConstants.RESOURCE_PATH_GENERATE;
import static com.AIApp.ollama.constants.OllamaConstants.SUMMARY_MODEL;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.StreamSupport;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.AIApp.ollama.Dao.PdfEmbeddingRepository;
import com.AIApp.ollama.dto.GenerateRequestDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OllamaService {

    private final RestTemplate restTemplate;
    
    @Autowired
    private PdfEmbeddingRepository repository;

    @Autowired
    public OllamaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public List<Float> generateEmbedding(String text) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        String jsonBody = "{ \"model\": \"nomic-embed-text\", \"prompt\": " + new ObjectMapper().writeValueAsString(text) + " }";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(OLLAMA_BASE_URL + RESOURCE_PATH_EMBEDDINGS))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(response.body());

        JsonNode embeddingNode = rootNode.get("embedding");
        return StreamSupport.stream(embeddingNode.spliterator(), false)
                .map(JsonNode::asDouble)
                .map(Double::floatValue)
                .toList();
    }

    public String generateSummary(String prompt) {

        GenerateRequestDTO requestBody = new GenerateRequestDTO(SUMMARY_MODEL, prompt, false);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<GenerateRequestDTO> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> res = restTemplate.postForEntity(OLLAMA_BASE_URL + RESOURCE_PATH_GENERATE, entity, Map.class);
            if (res.getStatusCode().is2xxSuccessful() && !Objects.isNull(res.getBody()))
                return res.getBody().get("response").toString();
        } catch (Exception e) {
            log.error("Error in Generating Summary", e);
            throw e;
        }

        return null;
    }
    
    public String generateChatResponse(String query) throws Exception {
    	
    	List<Float> queryEmbeddingList = generateEmbedding(query);
    	float[] queryEmbeddingArray=PdfEmbeddingService.convertListToArray(queryEmbeddingList);
    	List<String> relevantDocs = repository.findRelevantDoc(queryEmbeddingArray, 3);
    	
    	StringBuilder context = new StringBuilder();
        for (String content : relevantDocs) {
           
            if (content != null) {
                context.append(content).append("\n\n");
            }
        }
    	
    	String prompt = "Use the following document excerpts to answer the question:\n\n" +
                context.toString() + "\n\nQuestion: " + query;
    	
    	return generateSummary(prompt);
    }
    
    private static String readFileContent(String filePath) {
    	 try (PDDocument document = PDDocument.load(new File(filePath))) {
             PDFTextStripper pdfTextStripper = new PDFTextStripper();
             return pdfTextStripper.getText(document);
         } catch (IOException e) {
             System.err.println("Error reading PDF: " + filePath);
             return null;
         }
    }
}
