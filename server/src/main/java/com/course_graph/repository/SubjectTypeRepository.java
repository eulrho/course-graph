package com.course_graph.repository;

import com.course_graph.entity.SubjectEntity;
import com.course_graph.entity.SubjectTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubjectTypeRepository extends JpaRepository<SubjectTypeEntity, Long> {
    List<SubjectTypeEntity> findAllBySubjectEntityOrderByEndedAtDesc(SubjectEntity subjectEntity);
    Optional<SubjectTypeEntity> findBySubjectEntityAndEndedAtGreaterThan(SubjectEntity subjectEntity, int year);
}
