package com.course_graph.repository;

import com.course_graph.entity.HistoryEntity;
import com.course_graph.entity.SubjectEntity;
import com.course_graph.entity.UserEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HistoryRepository extends JpaRepository<HistoryEntity, Long> {
    Optional<HistoryEntity> findByUserEntityAndSubjectEntity(UserEntity userEntity, SubjectEntity subjectEntity);
    Page<HistoryEntity> findAllByUserEntityOrderByIdAsc(UserEntity userEntity, Pageable pageable);
}
