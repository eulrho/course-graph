package com.course_graph.repository;

import com.course_graph.entity.CurriculumEntity;
import com.course_graph.enums.Track;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CurriculumRepository extends JpaRepository<CurriculumEntity, Long> {
    List<CurriculumEntity> findAllByTrack(Track track);
}
