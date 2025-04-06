package com.course_graph.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CourseDTO {
    private String subjectName;
    private List<String> tracks;
    private String grade;

    public static CourseDTO toCourseDTO(String subjectName, List<String> tracks, String grade) {
        CourseDTO courseDTO = new CourseDTO();
        courseDTO.setSubjectName(subjectName);
        courseDTO.setTracks(tracks);
        courseDTO.setGrade(grade);
        return courseDTO;
    }
}

