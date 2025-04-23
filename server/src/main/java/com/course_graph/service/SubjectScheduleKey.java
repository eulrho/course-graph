package com.course_graph.service;

import com.course_graph.entity.SubjectEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubjectScheduleKey {
    private String name;
    private int classNumber;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubjectScheduleKey)) return false;
        SubjectScheduleKey that = (SubjectScheduleKey) o;
        return classNumber == that.classNumber &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, classNumber);
    }
}
