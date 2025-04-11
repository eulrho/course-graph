package com.course_graph.repository;

import com.course_graph.entity.SubjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<SubjectEntity, Long> {
    List<SubjectEntity> findAllByDeletedAtGreaterThan(int currentYear);
    Optional<SubjectEntity> findByCodeAndName(String code, String name);
    Optional<SubjectEntity> findByCodeAndDeletedAtGreaterThan(String code, int deletedAt);
    Optional<SubjectEntity> findByName(String name);
    List<SubjectEntity> findAllByGrade(String grade);
    List<SubjectEntity> findAllByDeletedAt(int deletedAt);
}
