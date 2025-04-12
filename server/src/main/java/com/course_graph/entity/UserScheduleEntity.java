package com.course_graph.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
@Table(name = "user_schedule")
public class UserScheduleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private ScheduleEntity scheduleEntity;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @Builder
    public static UserScheduleEntity toUserScheduleEntity(ScheduleEntity scheduleEntity, UserEntity userEntity) {
        UserScheduleEntity userScheduleEntity = new UserScheduleEntity();
        userScheduleEntity.scheduleEntity = scheduleEntity;
        userScheduleEntity.userEntity = userEntity;
        return userScheduleEntity;
    }
}
