package com.course_graph.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Type {
    MAJOR_ELECTIVE("전공 선택"),
    MAJOR_REQUIRED("전공 필수");

    private String message;
}
