package com.course_graph.repository;

import com.course_graph.entity.ScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<ScheduleEntity, Long> {
}
