package com.course_graph.service;

import com.course_graph.Exception.RestApiException;
import com.course_graph.chatGPT.ChatGPTPromptBuilder;
import com.course_graph.dto.*;
import com.course_graph.entity.*;
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

    public List<ChatGPTReplyDTO> getGptReply(List<ScheduleDTO> schedules, List<ScheduleTimeDTO> generalSchedules, int targetCredit) {
        String prompt = ChatGPTPromptBuilder.buildSchedulePrompt(schedules, generalSchedules, targetCredit);
        String gptReply = gptService.callChatGpt(prompt);
        System.out.println("GPT Reply: " + gptReply);
        try {
            ObjectMapper mapper = new ObjectMapper();

            return mapper.readValue(
                    gptReply, new TypeReference<>() {}
            );
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
        List<ScheduleEntity> candidateEntity = scheduleRepository.findAllBySubjectGradeAndSubjectIdNotIn(request.getGrade(), excludeSubjectIds);
        List<ScheduleDTO> candidateDTO = getCandidateSchedules(candidateEntity);

        int currentCredit = calculateCredit(currentSchedules);
        int targetCredit = request.getTargetCredit() - currentCredit;
        if (targetCredit < 0) {
            throw new RestApiException(CustomErrorCode.INVALID_PARAMETER);
        }

        List<ChatGPTReplyDTO> chatGPTResponses = getGptReply(candidateDTO, generalSchedules, targetCredit);
        List<ScheduleRecommendResponse> recommendResponses = new ArrayList<>();

        if (chatGPTResponses.isEmpty()) {
            ScheduleRecommendResponse recommendResponse = ScheduleRecommendResponse.toScheduleRecommendResponse(
                    getCurrentMajorScheduleTime(currentSchedules),
                    generalSchedules,
                    currentCredit
            );
            recommendResponses.add(recommendResponse);
        }
        for (ChatGPTReplyDTO response : chatGPTResponses) {
            ScheduleRecommendResponse recommendResponse = ScheduleRecommendResponse.toScheduleRecommendResponse(
                    getCurrentMajorScheduleTime(currentSchedules),
                    generalSchedules,
                    currentCredit
            );
            addMajorScheduleTime(response.getSchedules(), candidateEntity, recommendResponse);
            recommendResponses.add(recommendResponse);
        }
        return recommendResponses;
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

    public List<ScheduleDTO> getCandidateSchedules(List<ScheduleEntity> candidates) {
        List<ScheduleDTO> candidateDTO = new ArrayList<>();
        HashMap<SubjectScheduleKey, ScheduleDTO> map = new HashMap<>();
        for (ScheduleEntity scheduleEntity : candidates)
            extractScheduleData.extractSchedules(map, scheduleEntity);
        for (SubjectScheduleKey key : map.keySet()) candidateDTO.add(map.get(key));
        return candidateDTO;
    }

    public int calculateCredit(List<ScheduleEntity> scheduleList) {
        int totalCredit = 0;
        List<SubjectEntity> completedSubject = new ArrayList<>();
        for (ScheduleEntity schedule : scheduleList) {
            if (completedSubject.contains(schedule.getSubjectEntity())) continue;

            totalCredit += schedule.getSubjectEntity().getCredit();
            completedSubject.add(schedule.getSubjectEntity());
        }
        return totalCredit;
    }

    public void addMajorScheduleTime(List<SubjectScheduleKey> keys, List<ScheduleEntity> candidateEntity, ScheduleRecommendResponse recommendResponse) {
        List<MajorScheduleTimeDTO> majorScheduleTimeDTOList = new ArrayList<>();
        HashMap<SubjectScheduleKey, MajorScheduleTimeDTO> map = new HashMap<>();
        for (SubjectScheduleKey key : keys) {
            map.put(key, null);
        }

        int totalCredit = 0;
        for (ScheduleEntity schedule : candidateEntity)
            totalCredit += extractScheduleData.extractMajorSchedulesTime(map, schedule);
        for (SubjectScheduleKey key : map.keySet()) majorScheduleTimeDTOList.add(map.get(key));
        recommendResponse.getSchedules().addAll(majorScheduleTimeDTOList);
        recommendResponse.setTotalCredit(recommendResponse.getTotalCredit() + totalCredit);
    }

    public List<MajorScheduleTimeDTO> getCurrentMajorScheduleTime(List<ScheduleEntity> currentSchedules) {
        List<MajorScheduleTimeDTO> majorScheduleTimeDTOList = new ArrayList<>();
        HashMap<SubjectScheduleKey, MajorScheduleTimeDTO> map = new HashMap<>();

        // 기존 시간표 추가
        for (ScheduleEntity scheduleEntity : currentSchedules) {
            SubjectEntity subjectEntity = scheduleEntity.getSubjectEntity();
            SubjectScheduleKey key = new SubjectScheduleKey(subjectEntity.getCode(), scheduleEntity.getClassNumber());

            map.putIfAbsent(key, MajorScheduleTimeDTO.toMajorScheduleTimeDTO(scheduleEntity, new ArrayList<>()));
            map.get(key).getTimeList().add(scheduleEntity.getTime());
        }
        for (SubjectScheduleKey key : map.keySet()) majorScheduleTimeDTOList.add(map.get(key));
        return majorScheduleTimeDTOList;
    }
}