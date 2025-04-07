package com.course_graph.entity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
@Table(name = "subject_equivalence")
public class SubjectEquivalenceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "original_subject_id")
    private SubjectEntity originalSubjectEntity;

    @ManyToOne
    @JoinColumn(name = "equivalence_subject_id")
    private SubjectEntity equivalenceSubjectEntity;

    @Builder
    public static SubjectEquivalenceEntity toSubjectEquivalenceEntity(SubjectEntity originalSubjectEntity, SubjectEntity equivalenceSubjectEntity) {
        SubjectEquivalenceEntity subjectEquivalenceEntity = new SubjectEquivalenceEntity();
        subjectEquivalenceEntity.originalSubjectEntity = originalSubjectEntity;
        subjectEquivalenceEntity.equivalenceSubjectEntity = equivalenceSubjectEntity;
        return subjectEquivalenceEntity;
    }
}
