package com.course_graph.service;

import com.course_graph.entity.SubjectEntity;
import lombok.AllArgsConstructor;

import java.util.Objects;

@AllArgsConstructor
public class SubjectScheduleKey {
    private SubjectEntity subjectEntity;
    private int classNumber;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubjectScheduleKey)) return false;
        SubjectScheduleKey that = (SubjectScheduleKey) o;
        return classNumber == that.classNumber &&
                Objects.equals(subjectEntity, that.subjectEntity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subjectEntity, classNumber);
    }
}
