package com.AIApp.ollama.Service;

import com.AIApp.ollama.dto.GenerateRequestDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.StreamSupport;

import static com.AIApp.ollama.constants.OllamaConstants.*;

@Slf4j
@Service
public class OllamaService {

    private final RestTemplate restTemplate;

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
}
