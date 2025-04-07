package com.course_graph.entity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
@Table(name = "graduation")
public class GraduationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    private int requiredMinCredit;

    @Column(nullable = false)
    private int electiveMinCredit;

    @Builder
    public static GraduationEntity toGraduationEntity(int year, int requiredMinCredit, int electiveMinCredit) {
        GraduationEntity graduationEntity = new GraduationEntity();
        graduationEntity.year = year;
        graduationEntity.requiredMinCredit = requiredMinCredit;
        graduationEntity.electiveMinCredit = electiveMinCredit;
        return graduationEntity;
    }
}
