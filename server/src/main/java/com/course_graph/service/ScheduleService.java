package com.course_graph.service;

import com.course_graph.dto.ClassroomDTO;
import com.course_graph.dto.ScheduleDTO;
import com.course_graph.entity.*;
import com.course_graph.enums.Type;
import com.course_graph.repository.ScheduleRepository;
import com.course_graph.repository.SubjectTypeRepository;
import com.course_graph.repository.UserScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final SubjectTypeRepository subjectTypeRepository;
    private final UserService userService;
    private final int currentYear = 2025;
    private final UserScheduleRepository userScheduleRepository;

    public List<ScheduleDTO> getAllSchedules() {
        List<ScheduleEntity> scheduleEntityList = scheduleRepository.findAll();
        List<ScheduleDTO> scheduleDTOList = new ArrayList<>();

        HashMap<SubjectScheduleKey, ScheduleDTO> map = new HashMap<>();
        for (ScheduleEntity scheduleEntity : scheduleEntityList) extractSchedules(map, scheduleEntity);
        for (SubjectScheduleKey key : map.keySet()) scheduleDTOList.add(map.get(key));
        return scheduleDTOList;
    }

    public List<ScheduleDTO> getUserSchedules(String email) {
        UserEntity userEntity = userService.getLoginUserByEmail(email);
        List<UserScheduleEntity> userScheduleEntityList = userScheduleRepository.findAllByUserEntity(userEntity);
        List<ScheduleDTO> scheduleDTOList = new ArrayList<>();

        HashMap<SubjectScheduleKey, ScheduleDTO> map = new HashMap<>();
        for (UserScheduleEntity userScheduleEntity : userScheduleEntityList) {
            ScheduleEntity scheduleEntity = userScheduleEntity.getScheduleEntity();
            extractSchedules(map, scheduleEntity);
        }
        for (SubjectScheduleKey key : map.keySet()) scheduleDTOList.add(map.get(key));
        return scheduleDTOList;
    }

    public Type getSubjectType(SubjectEntity subjectEntity) {
        Optional<SubjectTypeEntity> optionalSubjectType = subjectTypeRepository
                .findBySubjectEntityAndEndedAtGreaterThan(subjectEntity, currentYear);
        return optionalSubjectType.get().getType();
    }

    public void extractSchedules(HashMap<SubjectScheduleKey, ScheduleDTO> map, ScheduleEntity scheduleEntity) {
        SubjectEntity subjectEntity = scheduleEntity.getSubjectEntity();
        SubjectScheduleKey key = new SubjectScheduleKey(subjectEntity, scheduleEntity.getClassNumber());
        ClassroomDTO classroomDTO = ClassroomDTO.toClassroomDTO(scheduleEntity.getTime(), scheduleEntity.getRoom());

        map.putIfAbsent(key, ScheduleDTO.toScheduleDTO(subjectEntity, getSubjectType(subjectEntity), scheduleEntity, new ArrayList<>()));
        map.get(key).getClassroomList().add(classroomDTO);
    }
}
