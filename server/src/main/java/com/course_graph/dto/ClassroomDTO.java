package com.course_graph.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ClassroomDTO {
    String time;
    String room;

    public static ClassroomDTO toClassroomDTO(String time, String room) {
        ClassroomDTO classroomDTO = new ClassroomDTO();
        classroomDTO.setTime(time);
        classroomDTO.setRoom(room);
        return classroomDTO;
    }
}
