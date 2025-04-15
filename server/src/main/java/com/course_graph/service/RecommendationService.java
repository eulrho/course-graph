package com.course_graph.service;

import com.course_graph.Exception.RestApiException;
import com.course_graph.chatGPT.ChatGPTPromptBuilder;
import com.course_graph.dto.ScheduleDTO;
import com.course_graph.dto.ScheduleRecommendRequest;
import com.course_graph.dto.ScheduleRecommendResponse;
import com.course_graph.dto.ScheduleTimeDTO;
import com.course_graph.entity.ScheduleEntity;
import com.course_graph.entity.UserEntity;
import com.course_graph.entity.UserGeneralScheduleEntity;
import com.course_graph.entity.UserScheduleEntity;
import com.course_graph.enums.CustomErrorCode;
import com.course_graph.repository.HistoryRepository;
import com.course_graph.repository.ScheduleRepository;
import com.course_graph.repository.UserGeneralScheduleRepository;
import com.course_graph.repository.UserScheduleRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final GptService gptService;
    private final HistoryRepository historyRepository;
    private final UserScheduleRepository userScheduleRepository;
    private final UserGeneralScheduleRepository userGeneralScheduleRepository;
    private final ScheduleRepository scheduleRepository;
    private final ExtractScheduleData extractScheduleData;

    public List<ScheduleRecommendResponse> getGptReply(List<ScheduleDTO> schedules, List<ScheduleTimeDTO> generalSchedules, int targetCredit) {
        String prompt = ChatGPTPromptBuilder.buildSchedulePrompt(schedules, generalSchedules, targetCredit);
        String gptReply = gptService.callChatGpt(prompt);
        System.out.println("GPT Reply: " + gptReply);
        try {
            ObjectMapper mapper = new ObjectMapper();

            List<ScheduleRecommendResponse> responses = mapper.readValue(
                    gptReply, new TypeReference<List<ScheduleRecommendResponse>>() {}
            );
            return responses;
        }
        catch (Exception e) {
            e.printStackTrace(); // 전체 에러 스택 출력
            throw new RuntimeException("GPT 응답 파싱 실패: " + e.getMessage());
        }
    }

    public List<ScheduleRecommendResponse> getRecommendation(ScheduleRecommendRequest request, UserEntity userEntity) {
        Set<Long> completedSubjectIds = historyRepository.findSubjectIdsByUserEntity(userEntity.getId());
        List<ScheduleEntity> currentSchedules = userScheduleRepository.findAllByUserEntity(userEntity)
                .stream()
                .map(UserScheduleEntity::getScheduleEntity)
                .toList();
        Set<Long> currentSubjectIds = currentSchedules.stream()
                .map(s -> s.getSubjectEntity().getId())
                .collect(Collectors.toSet());
        Set<Long> excludeSubjectIds = new HashSet<>(); // 제외할 과목 ID를 저장할 Set
        excludeSubjectIds.addAll(completedSubjectIds);
        excludeSubjectIds.addAll(currentSubjectIds);

        // 일반 스케줄
        List<ScheduleTimeDTO> generalSchedules = getGeneralScheduleTime(userEntity);
        // 추천 스케줄
        List<ScheduleDTO> candidates = getCandidateSchedules(request.getGrade(), excludeSubjectIds);

        int currentCredit = calculateCredit(currentSchedules);
        int targetCredit = request.getTargetCredit() - currentCredit;
        if (targetCredit < 0) {
            throw new RestApiException(CustomErrorCode.INVALID_PARAMETER);
        }
        List<ScheduleRecommendResponse> responses = getGptReply(candidates, generalSchedules, targetCredit);
        // 기존 시간표 정보 삽입
        responses.forEach(response -> {
            int totalCredit = response.getTotalCredit() + currentCredit;
            response.setTotalCredit(totalCredit);
            response.getSchedule().addAll(getScheduleTime(currentSchedules));
            response.getSchedule().addAll(generalSchedules);
        });
        return responses;
    }

    public List<ScheduleTimeDTO> getGeneralScheduleTime(UserEntity userEntity) {
        List<UserGeneralScheduleEntity> userGeneralScheduleEntityList = userGeneralScheduleRepository.findAllByUserEntity(userEntity);
        List<ScheduleTimeDTO> scheduleTimeDTOList = new ArrayList<>();
        HashMap<SubjectScheduleKey, ScheduleTimeDTO> generalMap = new HashMap<>();
        for (UserGeneralScheduleEntity userGeneralScheduleEntity : userGeneralScheduleEntityList)
            extractScheduleData.extractGeneralSchedulesTime(generalMap, userGeneralScheduleEntity);
        for (SubjectScheduleKey key : generalMap.keySet()) scheduleTimeDTOList.add(generalMap.get(key));
        return scheduleTimeDTOList;
    }

    public List<ScheduleDTO> getCandidateSchedules(String grade, Set<Long> excludeSubjectIds) {
        List<ScheduleEntity> candidates = scheduleRepository.findAllBySubjectGradeAndSubjectIdNotIn(grade, excludeSubjectIds);
        List<ScheduleDTO> candidateDTO = new ArrayList<>();
        HashMap<SubjectScheduleKey, ScheduleDTO> map = new HashMap<>();
        for (ScheduleEntity scheduleEntity : candidates)
            extractScheduleData.extractSchedules(map, scheduleEntity);
        for (SubjectScheduleKey key : map.keySet()) candidateDTO.add(map.get(key));
        return candidateDTO;
    }

    public int calculateCredit(List<ScheduleEntity> scheduleList) {
        int totalCredit = 0;
        for (ScheduleEntity schedule : scheduleList) {
            totalCredit += schedule.getSubjectEntity().getCredit();
        }
        return totalCredit;
    }

    public List<ScheduleTimeDTO> getScheduleTime(List<ScheduleEntity> currentSchedules) {
        List<ScheduleTimeDTO> scheduleTimeDTOList = new ArrayList<>();
        HashMap<SubjectScheduleKey, ScheduleTimeDTO> generalMap = new HashMap<>();
        for (ScheduleEntity scheduleEntity : currentSchedules)
            extractScheduleData.extractSchedulesTime(generalMap, scheduleEntity);
        for (SubjectScheduleKey key : generalMap.keySet()) scheduleTimeDTOList.add(generalMap.get(key));
        return scheduleTimeDTOList;
    }
}