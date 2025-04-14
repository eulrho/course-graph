package com.course_graph.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ScheduleTimeDTO {
    private String name;
    private List<String> timeList;

    public static ScheduleTimeDTO toScheduleTimeDTO(String name, List<String> timeList) {
        ScheduleTimeDTO scheduleTimeDTO = new ScheduleTimeDTO();
        scheduleTimeDTO.setName(name);
        scheduleTimeDTO.setTimeList(timeList);
        return scheduleTimeDTO;
    }
}
