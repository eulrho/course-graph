package com.course_graph.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleRecommendResponse {
    private List<ScheduleTimeDTO> schedule;
    private int totalCredit;
}
