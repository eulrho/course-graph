package com.course_graph.repository;

import com.course_graph.entity.SubjectEntity;
import com.course_graph.entity.SubjectEquivalenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubjectEquivalenceRepository extends JpaRepository<SubjectEquivalenceEntity, Long> {
    Optional<SubjectEquivalenceEntity> findByOriginalSubjectEntity(SubjectEntity originalSubjectEntity);
    Optional<SubjectEquivalenceEntity> findByEquivalenceSubjectEntity(SubjectEntity equivalenceSubjectEntity);
}
