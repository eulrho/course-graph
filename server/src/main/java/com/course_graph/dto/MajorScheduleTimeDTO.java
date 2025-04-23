package com.course_graph.dto;

import com.course_graph.entity.ScheduleEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MajorScheduleTimeDTO {
    private String code;
    private int classNumber;
    private String name;
    private List<String> timeList;

    public static MajorScheduleTimeDTO toMajorScheduleTimeDTO(ScheduleEntity scheduleEntity, List<String> timeList) {
        MajorScheduleTimeDTO majorScheduleTimeDTO = new MajorScheduleTimeDTO();
        majorScheduleTimeDTO.setCode(scheduleEntity.getSubjectEntity().getCode());
        majorScheduleTimeDTO.setClassNumber(scheduleEntity.getClassNumber());
        majorScheduleTimeDTO.setName(scheduleEntity.getSubjectEntity().getName());
        majorScheduleTimeDTO.setTimeList(timeList);
        return majorScheduleTimeDTO;
    }
}
