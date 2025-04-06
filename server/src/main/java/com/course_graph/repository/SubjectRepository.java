package com.course_graph.repository;

import com.course_graph.entity.SubjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<SubjectEntity, Long> {
    List<SubjectEntity> findAllByDeletedAtGreaterThan(int currentYear);
    List<SubjectEntity> findAllByCodeAndName(String code, String name);
    Optional<SubjectEntity> findByCodeAndDeletedAt(String code, int deletedAt);
    Optional<SubjectEntity> findByCodeAndDeletedAtGreaterThan(String code, int deletedAt);
}
