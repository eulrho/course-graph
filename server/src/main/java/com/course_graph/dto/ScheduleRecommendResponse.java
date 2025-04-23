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
    private List<MajorScheduleTimeDTO> schedules;
    private List<ScheduleTimeDTO> generalSubjects;
    private int totalCredit;

    public static ScheduleRecommendResponse toScheduleRecommendResponse(List<MajorScheduleTimeDTO> schedules, List<ScheduleTimeDTO> generalSubjects, int totalCredit) {
        ScheduleRecommendResponse scheduleRecommendResponse = new ScheduleRecommendResponse();
        scheduleRecommendResponse.setSchedules(schedules);
        scheduleRecommendResponse.setGeneralSubjects(generalSubjects);
        scheduleRecommendResponse.setTotalCredit(totalCredit);
        return scheduleRecommendResponse;
    }
}
