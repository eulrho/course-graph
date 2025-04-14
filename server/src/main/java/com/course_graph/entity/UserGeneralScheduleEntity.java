package com.course_graph.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
@Table(name = "user_general_schedule")
public class UserGeneralScheduleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 200)
    private String time;

    @Builder
    public static UserGeneralScheduleEntity toUserGeneralScheduleEntity(UserEntity userEntity, String name, String time) {
        UserGeneralScheduleEntity userGeneralScheduleEntity = new UserGeneralScheduleEntity();
        userGeneralScheduleEntity.userEntity = userEntity;
        userGeneralScheduleEntity.name = name;
        userGeneralScheduleEntity.time = time;
        return userGeneralScheduleEntity;
    }
}
