package com.course_graph.dto;

import com.course_graph.enums.SubjectStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CourseStatusDTO <T> {
    private T data;
    private String status;
}
