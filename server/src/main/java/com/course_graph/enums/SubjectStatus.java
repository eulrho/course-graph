package com.course_graph.enums;

public enum SubjectStatus {
    TAKEN, NOT_TAKEN;

    @Override
    public String toString() {
        return name();
    }
}
