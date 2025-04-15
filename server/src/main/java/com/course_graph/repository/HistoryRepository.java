package com.course_graph.repository;

import com.course_graph.entity.HistoryEntity;
import com.course_graph.entity.SubjectEntity;
import com.course_graph.entity.UserEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface HistoryRepository extends JpaRepository<HistoryEntity, Long> {
    Optional<HistoryEntity> findByUserEntityAndSubjectEntity(UserEntity userEntity, SubjectEntity subjectEntity);
    Page<HistoryEntity> findAllByUserEntityOrderByIdAsc(UserEntity userEntity, Pageable pageable);

    @Query("SELECT h.subjectEntity.id FROM HistoryEntity h WHERE h.userEntity.id = :userId")
    Set<Long> findSubjectIdsByUserEntity(Long userId);
}
