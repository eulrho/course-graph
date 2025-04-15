package com.course_graph.chatGPT;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatChoice {
    private int index;
    private ChatMessage message;
}