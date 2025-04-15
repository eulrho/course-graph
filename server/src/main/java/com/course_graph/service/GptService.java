package com.course_graph.service;

import com.course_graph.chatGPT.ChatGPTConfig;
import com.course_graph.chatGPT.ChatMessage;
import com.course_graph.chatGPT.ChatRequest;
import com.course_graph.chatGPT.ChatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.http.HttpHeaders;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GptService {
    private final ChatGPTConfig chatGPTConfig;

    @Value("${chatgpt.api-url}")
    private String apiUrl;

    public String callChatGpt(String prompt) {
        HttpHeaders headers = chatGPTConfig.httpHeaders();

        List<ChatMessage> messages = List.of(new ChatMessage("user", prompt));
        ChatRequest request = new ChatRequest("gpt-4o-mini", messages);

        HttpEntity<ChatRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<ChatResponse> response = chatGPTConfig.restTemplate().exchange(
                apiUrl,
                HttpMethod.POST,
                entity,
                ChatResponse.class
        );

        return response.getBody().getChoices().get(0).getMessage().getContent();
    }
}
