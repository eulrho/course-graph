package com.course_graph.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class GeneralScheduleUpdateRequest {
    private String name;
    private List<String> timeList;
    private String status;
}
