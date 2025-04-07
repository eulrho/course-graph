package com.course_graph.entity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
@Table(name = "subject_type")
public class SubjectTypeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private SubjectEntity subjectEntity;

    @Column(nullable = false, length = 200)
    private String type;

    @Column(nullable = false)
    private int startedAt;

    @Column(nullable = false)
    private int endedAt;

    @Builder
    public static SubjectTypeEntity toSubjectTypeEntity(SubjectEntity subjectEntity, String type, int startedAt, int endedAt) {
        SubjectTypeEntity subjectTypeEntity = new SubjectTypeEntity();
        subjectTypeEntity.subjectEntity = subjectEntity;
        subjectTypeEntity.type = type;
        subjectTypeEntity.startedAt = startedAt;
        subjectTypeEntity.endedAt = endedAt;
        return subjectTypeEntity;
    }

    public void extendEndedAt(int newEndedAt) {
        this.endedAt = newEndedAt;
    }
}
