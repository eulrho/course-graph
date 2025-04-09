package com.course_graph.entity;

import com.course_graph.dto.ScheduleDTO;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "subject")
public class SubjectEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String code;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false)
    private int credit;

    @Column(nullable = false, length = 100)
    private String grade;

    @Column(nullable = false)
    private int createdAt;

    @Column(nullable = false)
    private int deletedAt;

    @OneToMany(mappedBy = "subjectEntity", cascade = CascadeType.ALL)
    private List<CurriculumEntity> curriculumEntityList = new ArrayList<>();

    @Builder
    public static SubjectEntity toSubjectEntity(String code, String name, int credit, String grade, int createdAt, int deletedAt) {
        SubjectEntity subjectEntity = new SubjectEntity();
        subjectEntity.code = code;
        subjectEntity.name = name;
        subjectEntity.credit = credit;
        subjectEntity.grade = grade;
        subjectEntity.createdAt = createdAt;
        subjectEntity.deletedAt = deletedAt;
        return subjectEntity;
    }

    public void extendDeletedAt(int newDeletedAt) {
        this.deletedAt = newDeletedAt;
    }

    public void addCurriculum(CurriculumEntity curriculumEntity) {
        this.curriculumEntityList.add(curriculumEntity);
    }
}
