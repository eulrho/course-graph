package com.course_graph.repository;

import com.course_graph.entity.ScheduleEntity;
import com.course_graph.entity.UserEntity;
import com.course_graph.entity.UserScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserScheduleRepository extends JpaRepository<UserScheduleEntity, Long> {
    List<UserScheduleEntity> findAllByUserEntity(UserEntity userEntity);
}
