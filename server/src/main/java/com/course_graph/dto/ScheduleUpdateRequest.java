package com.course_graph.dto;

import lombok.Getter;

@Getter
public class ScheduleUpdateRequest {
    private String code;
    private String name;
    private int classNumber;
    private String status;
}
