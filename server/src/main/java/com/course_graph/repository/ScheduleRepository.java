package com.course_graph.repository;

import com.course_graph.entity.ScheduleEntity;
import com.course_graph.entity.SubjectEntity;
import com.course_graph.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<ScheduleEntity, Long> {
    List<ScheduleEntity> findAll();
    List<ScheduleEntity> findAllBySubjectEntityAndClassNumber(SubjectEntity subjectEntity, int classNumber);
    List<ScheduleEntity> findAllBySubjectEntity(SubjectEntity subjectEntity);
}
