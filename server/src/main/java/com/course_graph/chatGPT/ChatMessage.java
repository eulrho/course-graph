package com.course_graph.chatGPT;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {
    private String role; // "user" or "system"
    private String content;
}