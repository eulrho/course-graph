package com.course_graph.enums;

public enum Type {
    MAJOR_ELECTIVE, MAJOR_REQUIRED;

    @Override
    public String toString() {
        return name();
    }
}
