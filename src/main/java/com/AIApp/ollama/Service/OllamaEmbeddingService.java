package com.AIApp.ollama.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
public class OllamaEmbeddingService {
    private static final String OLLAMA_URL = "http://localhost:11434/api/embeddings";

    public List<Float> generateEmbedding(String text) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        String jsonBody = "{ \"model\": \"nomic-embed-text\", \"prompt\": " + new ObjectMapper().writeValueAsString(text) + " }";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(OLLAMA_URL))
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
}
