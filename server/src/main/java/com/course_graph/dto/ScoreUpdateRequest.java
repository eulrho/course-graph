package com.course_graph.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ScoreUpdateRequest {
    private String subjectName;
    private String score;
}
