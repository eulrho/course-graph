package com.course_graph.repository;

import com.course_graph.entity.SubjectEntity;
import com.course_graph.entity.SubjectTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubjectTypeRepository extends JpaRepository<SubjectTypeEntity, Long> {
    List<SubjectTypeEntity> findAllBySubjectEntityOrderByEndedAtDesc(SubjectEntity subjectEntity);
}
