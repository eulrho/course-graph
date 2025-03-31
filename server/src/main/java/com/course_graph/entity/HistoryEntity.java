package com.course_graph.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
@Table(name = "history")
public class HistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private SubjectEntity subjectEntity;

    private String score;

    @Builder
    public static HistoryEntity toHistoryEntity(UserEntity userEntity, SubjectEntity subjectEntity, String score) {
        HistoryEntity historyEntity = new HistoryEntity();
        historyEntity.userEntity = userEntity;
        historyEntity.subjectEntity = subjectEntity;
        historyEntity.score = score;
        return historyEntity;
    }

    public void edit(String newScore) {
        this.score = newScore;
    }
}

