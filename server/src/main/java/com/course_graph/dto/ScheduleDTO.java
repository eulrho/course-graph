package com.course_graph.dto;

import com.course_graph.entity.ScheduleEntity;
import com.course_graph.entity.SubjectEntity;
import com.course_graph.enums.Type;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ScheduleDTO {
    private String code;
    private String name;
    private int credit;
    private String type;
    private String professor;
    private int classNumber;
    private List<ClassroomDTO> classroomList;

    public static ScheduleDTO toScheduleDTO(SubjectEntity subjectEntity, Type type, ScheduleEntity scheduleEntity, List<ClassroomDTO> classroomList) {
        ScheduleDTO scheduleDTO = new ScheduleDTO();
        scheduleDTO.setCode(subjectEntity.getCode());
        scheduleDTO.setName(subjectEntity.getName());
        scheduleDTO.setCredit(subjectEntity.getCredit());
        scheduleDTO.setType(type.getMessage());
        scheduleDTO.setProfessor(scheduleEntity.getProfessor());
        scheduleDTO.setClassNumber(scheduleEntity.getClassNumber());
        scheduleDTO.setClassroomList(classroomList);
        return scheduleDTO;
    }
}
