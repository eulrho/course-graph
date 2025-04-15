package com.course_graph.service;

import com.course_graph.Exception.RestApiException;
import com.course_graph.dto.*;
import com.course_graph.entity.*;
import com.course_graph.enums.CustomErrorCode;
import com.course_graph.enums.ScheduleUpdateStatus;
import com.course_graph.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final SubjectRepository subjectRepository;
    private final ScheduleRepository scheduleRepository;
    private final SubjectTypeRepository subjectTypeRepository;
    private final UserService userService;
    private final UserScheduleRepository userScheduleRepository;
    private final UserGeneralScheduleRepository userGeneralScheduleRepository;
    private final RecommendationService recommendationService;
    private final ExtractScheduleData extractScheduleData;

    public List<ScheduleDTO> getAllSchedules(String grade) {
        List<ScheduleEntity> scheduleEntityList = scheduleRepository.findAll();
        List<ScheduleDTO> scheduleDTOList = new ArrayList<>();

        HashMap<SubjectScheduleKey, ScheduleDTO> map = new HashMap<>();
        for (ScheduleEntity scheduleEntity : scheduleEntityList) {
            if (!scheduleEntity.getSubjectEntity().getGrade().equals(grade)) continue;
            extractScheduleData.extractSchedules(map, scheduleEntity);
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
            extractScheduleData.extractSchedulesTime(map, scheduleEntity);
        }
        for (UserGeneralScheduleEntity userGeneralScheduleEntity : userGeneralScheduleEntityList)
            extractScheduleData.extractGeneralSchedulesTime(map, userGeneralScheduleEntity);
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

    public List<ScheduleRecommendResponse> recommendSchedules(String email, ScheduleRecommendRequest scheduleRecommendRequest) {
        UserEntity userEntity = userService.getLoginUserByEmail(email);
        List<String> grades = new ArrayList<>(Arrays.asList("1학년", "2학년", "3학년", "4학년"));
        if (!grades.contains(scheduleRecommendRequest.getGrade())) {
            throw new RestApiException(CustomErrorCode.INVALID_PARAMETER);
        }
        return recommendationService.getRecommendation(scheduleRecommendRequest, userEntity);
    }
}
