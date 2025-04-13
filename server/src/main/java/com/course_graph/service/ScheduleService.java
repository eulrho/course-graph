package com.course_graph.service;

import com.course_graph.Exception.RestApiException;
import com.course_graph.dto.ClassroomDTO;
import com.course_graph.dto.ScheduleUpdateRequest;
import com.course_graph.dto.ScheduleDTO;
import com.course_graph.entity.*;
import com.course_graph.enums.CustomErrorCode;
import com.course_graph.enums.ScheduleUpdateStatus;
import com.course_graph.enums.Type;
import com.course_graph.repository.ScheduleRepository;
import com.course_graph.repository.SubjectRepository;
import com.course_graph.repository.SubjectTypeRepository;
import com.course_graph.repository.UserScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final SubjectRepository subjectRepository;
    private final ScheduleRepository scheduleRepository;
    private final SubjectTypeRepository subjectTypeRepository;
    private final UserService userService;
    private final int currentYear = 2025;
    private final UserScheduleRepository userScheduleRepository;

    public List<ScheduleDTO> getAllSchedules(String grade) {
        List<ScheduleEntity> scheduleEntityList = scheduleRepository.findAll();
        List<ScheduleDTO> scheduleDTOList = new ArrayList<>();

        HashMap<SubjectScheduleKey, ScheduleDTO> map = new HashMap<>();
        for (ScheduleEntity scheduleEntity : scheduleEntityList) {
            if (!scheduleEntity.getSubjectEntity().getGrade().equals(grade)) continue;
            extractSchedules(map, scheduleEntity);
        }
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

    @Transactional
    public void updateSchedules(String email, List<ScheduleUpdateRequest> scheduleUpdateRequestList) {
        UserEntity userEntity = userService.getLoginUserByEmail(email);
        for (ScheduleUpdateRequest scheduleUpdateRequest : scheduleUpdateRequestList) {
            Optional<SubjectEntity> optionalSubject = subjectRepository.findByCodeAndName(scheduleUpdateRequest.getCode(), scheduleUpdateRequest.getName());
            SubjectEntity subjectentity = optionalSubject.orElseThrow(() -> new RestApiException(CustomErrorCode.INVALID_PARAMETER));
            List<ScheduleEntity> ScheduleEntityList = scheduleRepository
                    .findAllBySubjectEntityAndClassNumber(subjectentity, scheduleUpdateRequest.getClassNumber());

            for (ScheduleEntity scheduleEntity : ScheduleEntityList) {
                Optional<UserScheduleEntity> optionalUserSchedule = userScheduleRepository
                        .findByScheduleEntityAndUserEntity(scheduleEntity, userEntity);
                if (scheduleUpdateRequest.getStatus().equals(ScheduleUpdateStatus.ADD.toString())) {
                    if (optionalUserSchedule.isEmpty()) {
                        UserScheduleEntity userScheduleEntity = UserScheduleEntity.toUserScheduleEntity(scheduleEntity, userEntity);
                        userScheduleRepository.save(userScheduleEntity);
                    }
                }
                else if (scheduleUpdateRequest.getStatus().equals(ScheduleUpdateStatus.DELETE.toString())) {
                    if (optionalUserSchedule.isPresent()) {
                        UserScheduleEntity userScheduleEntity = optionalUserSchedule.get();
                        userScheduleRepository.delete(userScheduleEntity);
                    }
                }
                else throw new RestApiException(CustomErrorCode.INVALID_PARAMETER); // 잘못된 상태값
            }
        }
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
