package com.course_graph.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
@Table(name = "schedule")
public class ScheduleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private SubjectEntity subjectEntity;

    private int classNumber;

    @Column(nullable = false, length = 200)
    private String room;

    @Column(nullable = false, length = 200)
    private String time;

    @Column(nullable = false, length = 200)
    private String professor;

    @Builder
    public static ScheduleEntity toScheduleEntity(SubjectEntity subjectEntity, int classNumber, String room, String time, String professor) {
        ScheduleEntity scheduleEntity = new ScheduleEntity();
        scheduleEntity.subjectEntity = subjectEntity;
        scheduleEntity.classNumber = classNumber;
        scheduleEntity.room = room;
        scheduleEntity.time = time;
        scheduleEntity.professor = professor;
        return scheduleEntity;
    }
}
