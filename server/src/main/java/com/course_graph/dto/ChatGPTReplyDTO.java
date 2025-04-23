package com.course_graph.dto;

import com.course_graph.service.SubjectScheduleKey;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatGPTReplyDTO {
    private List<SubjectScheduleKey> schedules;
}
