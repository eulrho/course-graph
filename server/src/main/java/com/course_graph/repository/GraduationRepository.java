package com.course_graph.repository;

import com.course_graph.entity.GraduationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GraduationRepository extends JpaRepository<GraduationEntity, Long> {
    Optional<GraduationEntity> findByYear(int year);
}
