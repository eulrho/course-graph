package com.course_graph.service;

import com.course_graph.Exception.RestApiException;
import com.course_graph.dto.*;
import com.course_graph.entity.*;
import com.course_graph.enums.CustomErrorCode;
import com.course_graph.enums.ScheduleUpdateStatus;
import com.course_graph.enums.Type;
import com.course_graph.repository.*;
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
    private final UserGeneralScheduleRepository userGeneralScheduleRepository;

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

    public List<ScheduleTimeDTO> getUserSchedules(String email) {
        UserEntity userEntity = userService.getLoginUserByEmail(email);
        List<UserScheduleEntity> userScheduleEntityList = userScheduleRepository.findAllByUserEntity(userEntity);
        List<UserGeneralScheduleEntity> userGeneralScheduleEntityList = userGeneralScheduleRepository.findAllByUserEntity(userEntity);
        List<ScheduleTimeDTO> scheduleTimeDTOList = new ArrayList<>();

        HashMap<SubjectScheduleKey, ScheduleTimeDTO> map = new HashMap<>();
        for (UserScheduleEntity userScheduleEntity : userScheduleEntityList) {
            ScheduleEntity scheduleEntity = userScheduleEntity.getScheduleEntity();
            extractSchedulesTime(map, scheduleEntity);
        }
        for (UserGeneralScheduleEntity userGeneralScheduleEntity : userGeneralScheduleEntityList)
            extractGeneralSchedulesTime(map, userGeneralScheduleEntity);
        for (SubjectScheduleKey key : map.keySet()) scheduleTimeDTOList.add(map.get(key));
        return scheduleTimeDTOList;
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
                    else throw new RestApiException(CustomErrorCode.INVALID_PARAMETER);
                }
                else throw new RestApiException(CustomErrorCode.INVALID_PARAMETER); // 잘못된 상태값
            }
        }
    }

    @Transactional
    public void updateGeneralSchedules(String email, List<GeneralScheduleUpdateRequest> generalScheduleUpdateList) {
        UserEntity userEntity = userService.getLoginUserByEmail(email);
        for (GeneralScheduleUpdateRequest generalScheduleUpdate : generalScheduleUpdateList) {
            List<UserGeneralScheduleEntity> userGeneralScheduleEntityList = userGeneralScheduleRepository
                    .findAllByUserEntityAndName(userEntity, generalScheduleUpdate.getName());
            if (generalScheduleUpdate.getStatus().equals(ScheduleUpdateStatus.ADD.toString())) {
                if (userGeneralScheduleEntityList.isEmpty()) {
                    generalScheduleUpdate.getTimeList().forEach(time -> {
                        UserGeneralScheduleEntity userGeneralScheduleEntity = UserGeneralScheduleEntity.toUserGeneralScheduleEntity(
                                userEntity, generalScheduleUpdate.getName(), time);
                        userGeneralScheduleRepository.save(userGeneralScheduleEntity);
                    });
                }
            }
            else if (generalScheduleUpdate.getStatus().equals(ScheduleUpdateStatus.DELETE.toString())) {
                if (!userGeneralScheduleEntityList.isEmpty())
                    userGeneralScheduleRepository.deleteAll(userGeneralScheduleEntityList);
                else throw new RestApiException(CustomErrorCode.INVALID_PARAMETER);
            }
            else throw new RestApiException(CustomErrorCode.INVALID_PARAMETER); // 잘못된 상태값
        }
    }

    public Type getSubjectType(SubjectEntity subjectEntity) {
        Optional<SubjectTypeEntity> optionalSubjectType = subjectTypeRepository
                .findBySubjectEntityAndEndedAtGreaterThan(subjectEntity, currentYear);
        return optionalSubjectType.get().getType();
    }

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
}
