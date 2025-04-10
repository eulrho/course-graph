package com.course_graph.dto;

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
    private String grade;
    private String type;
    private String professor;
    private int classNumber;
    private List<ClassroomDTO> classroomList;

    public static ScheduleDTO toScheduleDTO(String code, String name, int credit, String grade, Type type, String professor, int classNumber, List<ClassroomDTO> classroomList) {
        ScheduleDTO scheduleDTO = new ScheduleDTO();
        scheduleDTO.setCode(code);
        scheduleDTO.setName(name);
        scheduleDTO.setCredit(credit);
        scheduleDTO.setGrade(grade);
        scheduleDTO.setType(type);
        scheduleDTO.setProfessor(professor);
        scheduleDTO.setClassNumber(classNumber);
        scheduleDTO.setClassroomList(classroomList);
        return scheduleDTO;
    }

    public void setType(Type type) {
        this.type = type.toString();
    }
}
