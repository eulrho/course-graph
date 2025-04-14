package com.course_graph.repository;

import com.course_graph.entity.UserEntity;
import com.course_graph.entity.UserGeneralScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserGeneralScheduleRepository extends JpaRepository<UserGeneralScheduleEntity, Long> {
    List<UserGeneralScheduleEntity> findAllByUserEntityAndName(UserEntity userEntity, String name);
    List<UserGeneralScheduleEntity> findAllByUserEntity(UserEntity userEntity);
}
