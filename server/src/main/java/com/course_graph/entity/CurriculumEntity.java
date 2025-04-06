package com.course_graph.entity;

import com.course_graph.enums.Track;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
@Table(name = "curriculum")
public class CurriculumEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private SubjectEntity subjectEntity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 200)
    private Track track;

    @Builder
    public static CurriculumEntity toCurriculumEntity(SubjectEntity subjectEntity, Track track) {
        CurriculumEntity curriculumEntity = new CurriculumEntity();
        curriculumEntity.subjectEntity = subjectEntity;
        curriculumEntity.track = track;
        return curriculumEntity;
    }
}
