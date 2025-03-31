package com.course_graph.entity;

import com.course_graph.dto.SubjectDTO;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

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
    private String semester;

    @Column(nullable = false, length = 100)
    private String grade;

    @Column(nullable = false, length = 200)
    private String type;

    @Column(nullable = false)
    private int createdAt;

    @Column(nullable = false)
    private int deletedAt;

    @Builder
    public static SubjectEntity toSubjectEntity(SubjectDTO subjectDTO) {
        SubjectEntity subjectEntity = new SubjectEntity();
        subjectEntity.code = subjectDTO.getCode();
        subjectEntity.name = subjectDTO.getName();
        subjectEntity.credit = subjectDTO.getCredit();
        subjectEntity.semester = subjectDTO.getSemester();
        subjectEntity.grade = subjectDTO.getGrade();
        subjectEntity.type = subjectDTO.getType();
        subjectEntity.createdAt = subjectDTO.getCreatedAt();
        subjectEntity.deletedAt = subjectDTO.getDeletedAt();
        return subjectEntity;
    }

    public void extendDeletedAt(int newDeletedAt) {
        this.deletedAt = newDeletedAt;
    }
}
