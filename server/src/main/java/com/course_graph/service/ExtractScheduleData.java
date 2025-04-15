package com.course_graph.service;

import com.course_graph.dto.ClassroomDTO;
import com.course_graph.dto.ScheduleDTO;
import com.course_graph.dto.ScheduleTimeDTO;
import com.course_graph.entity.ScheduleEntity;
import com.course_graph.entity.SubjectEntity;
import com.course_graph.entity.SubjectTypeEntity;
import com.course_graph.entity.UserGeneralScheduleEntity;
import com.course_graph.enums.Type;
import com.course_graph.repository.SubjectTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExtractScheduleData {
    private final SubjectTypeRepository subjectTypeRepository;
    int currentYear = 2025;

    public void extractSchedules(HashMap<SubjectScheduleKey, ScheduleDTO> map, ScheduleEntity scheduleEntity) {
        SubjectEntity subjectEntity = scheduleEntity.getSubjectEntity();
        SubjectScheduleKey key = new SubjectScheduleKey(subjectEntity.getName(), scheduleEntity.getClassNumber());
        ClassroomDTO classroomDTO = ClassroomDTO.toClassroomDTO(scheduleEntity.getTime(), scheduleEntity.getRoom());

        map.putIfAbsent(key, ScheduleDTO.toScheduleDTO(subjectEntity, getSubjectType(subjectEntity), scheduleEntity, new ArrayList<>()));
        map.get(key).getClassroomList().add(classroomDTO);
    }

    public void extractSchedulesTime(HashMap<SubjectScheduleKey, ScheduleTimeDTO> map, ScheduleEntity scheduleEntity) {
        SubjectEntity subjectEntity = scheduleEntity.getSubjectEntity();
        SubjectScheduleKey key = new SubjectScheduleKey(subjectEntity.getName(), scheduleEntity.getClassNumber());

        map.putIfAbsent(key, ScheduleTimeDTO.toScheduleTimeDTO(subjectEntity.getName(), new ArrayList<>()));
        map.get(key).getTimeList().add(scheduleEntity.getTime());
    }

    public void extractGeneralSchedulesTime(HashMap<SubjectScheduleKey, ScheduleTimeDTO> map, UserGeneralScheduleEntity userGeneralScheduleEntity) {
        SubjectScheduleKey key = new SubjectScheduleKey(userGeneralScheduleEntity.getName(), 0);

        map.putIfAbsent(key, ScheduleTimeDTO.toScheduleTimeDTO(userGeneralScheduleEntity.getName(), new ArrayList<>()));
        map.get(key).getTimeList().add(userGeneralScheduleEntity.getTime());
    }

    private Type getSubjectType(SubjectEntity subjectEntity) {
        Optional<SubjectTypeEntity> optionalSubjectType = subjectTypeRepository
                .findBySubjectEntityAndEndedAtGreaterThan(subjectEntity, currentYear);
        return optionalSubjectType.get().getType();
    }
}
