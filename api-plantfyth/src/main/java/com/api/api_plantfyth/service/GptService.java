package com.api.api_plantfyth.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
@Service
public class GptService {
     @Value("${openai.api.key}")
     private String openaiKey;

     private final RestTemplate restTemplate = new RestTemplate();

    public String chamarGpt(String prompt)   {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openaiKey);

        Map<String, Object> body = Map.of(
            "model", "gpt-4o-mini",
            "messages", List.of(Map.of("role", "user", "content", prompt)),
            "max_tokens", 1000
        );  {

        ResponseEntity<Map> response = restTemplate.postForEntity(
            "https://api.openai.com/v1/chat/completions",
            new HttpEntity<>(body, headers),
            Map.class
        );

        List<Map> choices = (List<Map>) response.getBody().get("choices");
        return (String) ((Map) choices.get(0).get("message")).get("content");
    }
   }
}
