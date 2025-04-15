package com.course_graph.repository;

import com.course_graph.entity.ScheduleEntity;
import com.course_graph.entity.SubjectEntity;
import com.course_graph.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface ScheduleRepository extends JpaRepository<ScheduleEntity, Long> {
    List<ScheduleEntity> findAll();
    List<ScheduleEntity> findAllBySubjectEntityAndClassNumber(SubjectEntity subjectEntity, int classNumber);

    // jpql
    @Query("""
    SELECT s
    FROM ScheduleEntity s
    WHERE s.subjectEntity.grade = :grade
    AND s.subjectEntity.id NOT IN :excludedSubjectIds
    """)
    List<ScheduleEntity> findAllBySubjectGradeAndSubjectIdNotIn(
            @Param("grade") String grade,
            @Param("excludedSubjectIds") Set<Long> excludedSubjectIds
    );
}
