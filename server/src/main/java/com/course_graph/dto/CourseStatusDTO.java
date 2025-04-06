package com.course_graph.dto;

import com.course_graph.enums.SubjectStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CourseStatusDTO {
    private CourseDTO course;
    private String status;

    public static CourseStatusDTO toCourseStatusDTO(CourseDTO courseDTO) {
        CourseStatusDTO courseStatusDTO = new CourseStatusDTO();
        courseStatusDTO.setCourse(courseDTO);
        courseStatusDTO.setStatus(SubjectStatus.NOT_TAKEN.toString());
        return courseStatusDTO;
    }
}
