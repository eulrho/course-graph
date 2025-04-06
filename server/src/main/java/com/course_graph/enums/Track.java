package com.course_graph.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Track {
    INTELLIGENT_SYS("지능형시스템"),
    AUTO_V2X_COMM("자율주행차 V2X 통신시스템");

    private String message;
}
